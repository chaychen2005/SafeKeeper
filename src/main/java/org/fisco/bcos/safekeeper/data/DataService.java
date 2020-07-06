/**
 * Copyright 2014-2020  the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fisco.bcos.safekeeper.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.fisco.bcos.safekeeper.base.code.ConstantCode;
import org.fisco.bcos.safekeeper.base.exception.SafeKeeperException;
import org.fisco.bcos.safekeeper.base.tools.JacksonUtils;
import org.fisco.bcos.safekeeper.data.entity.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/**
 * services for data.
 */
@Log4j2
@Service
public class DataService {

    @Autowired
    private DataMapper dataMapper;

    /**
     * add data row.
     */
    public void addDataRow(TbDataInfo dataInfo) throws SafeKeeperException {
        log.debug("start addDataRow. data info:{}", JacksonUtils.objToString(dataInfo));

        // check data
        DataQueryParam queryParams = new DataQueryParam(dataInfo.getAccount(), dataInfo.getDataId(), dataInfo.getDataSubId());
        dataNotExist(queryParams);

        // add data row
        Integer affectRow = dataMapper.addDataRow(dataInfo);

        // check result
        // checkDbAffectRow(affectRow);

        log.debug("end addDataRow. affectRow:{}", affectRow);
    }

    /**
     * add data batch.
     */
    @Transactional
    public void addDataBatch(List<TbDataInfo> dataInfoList) throws SafeKeeperException {
        log.debug("start addDataBatch.");

        for (int i = 0; i < dataInfoList.size(); i++) {
            TbDataInfo dataInfo = dataInfoList.get(i);
            log.debug("data[{}] info:{}", i, JacksonUtils.objToString(dataInfo));
            // add data
            dataMapper.addDataRow(dataInfo);
        }

        log.debug("end addDataBatch. affectRow:{}", dataInfoList.size());
    }

    /**
     * update data row.
     */
    public Integer updateDataRow(TbDataInfo dataInfo) throws SafeKeeperException {
        log.debug("start updateDataRow. data info:{}", JacksonUtils.objToString(dataInfo));

        // update data row
        Integer affectRow = dataMapper.updateDataRow(dataInfo);

        log.debug("end updateDataRow. affectRow:{}", affectRow);

        return affectRow;
    }

    /**
     * update data batch.
     */
    @Transactional
    public void updateDataBatch(List<TbDataInfo> dataInfoList) throws SafeKeeperException {
        log.debug("start addDataBatch.");

        for (int i = 0; i < dataInfoList.size(); i++) {
            TbDataInfo dataInfo = dataInfoList.get(i);
            log.debug("data[{}] info:{}", i, JacksonUtils.objToString(dataInfo));
            // update data batch
            Integer affectRow = dataMapper.updateDataRow(dataInfo);
            // check result
            if (affectRow == 0) {
                throw new SafeKeeperException(ConstantCode.DATA_NOT_EXISTS);
            }
        }

        log.debug("end addDataBatch. affectRow:{}", dataInfoList.size());
    }

    /**
     * query the data.
     */
    public List<TbDataInfo> queryData(DataQueryParam queryParams) {
        log.debug("start queryData. query info:{}", JacksonUtils.objToString(queryParams));
        List<TbDataInfo> dataRow = dataMapper.queryData(queryParams);
        log.debug("end queryData. accountRow:{} ", JacksonUtils.objToString(dataRow));
        return dataRow;
    }

    /**
     * query count of data.
     */
    public int countOfData(String account, String dataId, String dataSubId, int dataStatus) {
        log.debug("start countOfData. account: {} dataId: {} dataSubId: {} dataStatus: {} ",
                account, dataId, dataSubId, dataStatus);
        Integer dataCount = dataMapper.countOfData(account, dataId, dataSubId, dataStatus);
        int count = dataCount == null ? 0 : dataCount.intValue();
        log.debug("end countOfData. count: {} ", count);
        return count;
    }

    /**
     * query data list.
     */
    public List<TbDataInfo> listOfData(DataListParam param) {
        log.debug("start listOfData. param:{} ", JacksonUtils.objToString(param));
        List<TbDataInfo> list = dataMapper.listOfData(param);
        log.debug("end listOfData. list:{} ", JacksonUtils.objToString(list));
        return list;
    }

    /**
     * delete data info.
     */
    public void deleteDataRow(DataQueryParam queryParams) throws SafeKeeperException {
        log.debug("start deleteDataRow. delete info:{} ", JacksonUtils.objToString(queryParams));

        // check data
        dataExist(queryParams);

        // delete data row
        Integer affectRow = dataMapper.deleteDataRow(queryParams);

        // check result
        checkDbAffectRow(affectRow);

        log.debug("end deleteDataRow. affectRow:{} ", affectRow);
    }

    /**
     * query existence of data.
     */
    public int existOfData(DataQueryParam queryParams) {
        log.debug("start existOfData. info:{} ", JacksonUtils.objToString(queryParams));
        Integer dataCount = dataMapper.existOfData(queryParams);
        int count = dataCount == null ? 0 : dataCount.intValue();
        log.debug("end existOfData. count:{} ", count);
        return count;
    }

    /**
     * boolean the data is exist.
     */
    public void dataExist(DataQueryParam queryParams) throws SafeKeeperException {
        if (StringUtils.isBlank(queryParams.getDataId())) {
            throw new SafeKeeperException(ConstantCode.DATA_ID_EMPTY);
        }
        if (StringUtils.isBlank(queryParams.getDataSubId())) {
            throw new SafeKeeperException(ConstantCode.DATA_SUB_ID_EMPTY);
        }
        int count = existOfData(queryParams);
        if (count == 0) {
            throw new SafeKeeperException(ConstantCode.DATA_NOT_EXISTS);
        }
    }

    /**
     * boolean the data is not exist.
     */
    public void dataNotExist(DataQueryParam queryParams) throws SafeKeeperException {
        if (StringUtils.isBlank(queryParams.getDataId())) {
            throw new SafeKeeperException(ConstantCode.DATA_ID_EMPTY);
        }
        if (StringUtils.isBlank(queryParams.getDataSubId())) {
            throw new SafeKeeperException(ConstantCode.DATA_SUB_ID_EMPTY);
        }
        int count = existOfData(queryParams);
        if (count > 0) {
            throw new SafeKeeperException(ConstantCode.DATA_EXISTS);
        }
    }

    /**
     * check db affect row.
     */
    public void checkDbAffectRow(Integer affectRow) throws SafeKeeperException {
        if (affectRow == 0) {
            log.warn("affect 0 rows of tb_data_info");
            throw new SafeKeeperException(ConstantCode.DB_EXCEPTION);
        }
    }

    public List<String> listOfDataId(String account, int status) {
        log.debug("start listOfDataId. account:{} status: {} ", account, status);
        List<String> list = dataMapper.listOfDataId(account, status);
        log.debug("end listOfDataId. list:{} ", JacksonUtils.objToString(list));
        return list;
    }

    public List<String> listOfDataIdByCoinStatus(String account, String plainText) {
        log.debug("start listOfDataIdByCoinStatus. account:{} plainText: {} ", account, plainText);
        List<String> list = dataMapper.listOfDataIdByCoinStatus(account, plainText);
        log.debug("end listOfDataIdByCoinStatus. list:{} ", JacksonUtils.objToString(list));
        return list;
    }

    public List<JsonNode> preAuthorization(String account, long target) {
        boolean find = false;
        List<TokenInfo> tokens = listOfTokenWithTokenStatus(account, "0");
        List<String> selected = new ArrayList<>();

        for (int i = 0; i < tokens.size(); i++) {
            TokenInfo token = tokens.get(i);
            token.setValue(Integer.valueOf(token.getText()));
            log.debug(token.getKey() + " " + token.getText() + " " + token.getValue());
            if (target == token.getValue()) {
                String dataId = token.getKey();
                DataQueryParam dataQueryParam = new DataQueryParam(account, dataId, "status");
                Integer count = updateDataStatus(dataQueryParam, "0", "2");
                if (count > 0) {
                    selected.add(token.getKey());
                    find = true;
                    break;
                }
            }
        }

        if (!find) {
            long totalValue = 0;
            log.debug("after sort by value in getCredentialList. size: {} ", tokens.size());
            for (int i = 0; i < tokens.size(); i++) {
                TokenInfo token = tokens.get(i);
                log.debug(token.getKey() + " " + token.getText() + " " + token.getValue());
                totalValue += token.getValue();
            }

            if (target > totalValue) {
                log.debug("the account has not sufficient tokens. target: {} total: {} ", target, totalValue);
                throw new SafeKeeperException(ConstantCode.NOT_SUFFICIENT_TOKENS);
            }

            // sort by value
            TokenInfo tmp;
            for (int i = 0; i < tokens.size() - 1; i++) {
                for (int j = tokens.size() - 1; j > 0; j--) {
                    if (tokens.get(j - 1).getValue() < tokens.get(j).getValue()) {
                        tmp = tokens.get(j);
                        tokens.set(j, tokens.get(j - 1));
                        tokens.set(j - 1, tmp);
                    }
                }
            }

            long remain = target;
            for (TokenInfo token : tokens) {
                String dataId = token.getKey();
                DataQueryParam dataQueryParam = new DataQueryParam(account, dataId, "status");
                Integer count = updateDataStatus(dataQueryParam, "0", "2");

                if (count > 0) {
                    remain -= Long.parseLong(token.getText());
                    selected.add(dataId);
                    if (remain <= 0) {
                        break;
                    }
                }
            }

            if (remain > 0) {
                log.debug("the account has not sufficient tokens. target: {} total: {} ", target , target - remain);
                // roll back
                for (String dataId : selected) {
                    DataQueryParam dataQueryParam = new DataQueryParam(account, dataId, "status");
                    Integer count = updateDataStatus(dataQueryParam, "2", "0");
                    checkDbAffectRow(count);
                }
                throw new SafeKeeperException(ConstantCode.NOT_SUFFICIENT_TOKENS);
            }
        }

        List<JsonNode> listOfData = new ArrayList<>();
        for (String dataId : selected) {
            DataQueryParam queryParams = new DataQueryParam(account, dataId);
            List<TbDataInfo> dataInfoList = queryData(queryParams);
            JsonNode dateNode = rawDataListToDataNode(dataInfoList);
            listOfData.add(dateNode);
        }

        return listOfData;
    }

    public List<TokenInfo> listOfTokenWithTokenStatus(String account, String status) {
        log.debug("start listOfTokenWithTokenStatus. account:{} status:{} ", account, status);
        List<TokenInfo> list = dataMapper.listOfTokenWithTokenStatus(account, status);
        log.debug("end listOfTokenWithTokenStatus. list:{} ", JacksonUtils.objToString(list));
        return list;
    }

    public JsonNode getBalance(String account) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode dataNode = objectMapper.createObjectNode();
        List<String> unspentList = listOfValueWithTokenStatus(account,"0");
        ((ObjectNode) dataNode).put("unspent", aggregateBalance(unspentList));
        List<String> spentList = listOfValueWithTokenStatus(account, "1");
        ((ObjectNode) dataNode).put("spent", aggregateBalance(spentList));
        return dataNode;
    }

    public JsonNode getUnspentAmount(String account) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode dataNode = objectMapper.createObjectNode();
        List<String> unspentList = listOfValueWithTokenStatus(account,"0");
        ((ObjectNode) dataNode).put("balance", aggregateBalance(unspentList));
        return dataNode;
    }

    public JsonNode getSpentAmount(String account) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode dataNode = objectMapper.createObjectNode();
        List<String> spentList = listOfValueWithTokenStatus(account,"1");
        ((ObjectNode) dataNode).put("expenditure", aggregateBalance(spentList));
        return dataNode;
    }

    private List<String> listOfValueWithTokenStatus(String account, String status) {
        log.debug("start listOfValueWithTokenStatus. account:{} status:{} ", account, status);
        List<String> list = dataMapper.listOfValueWithTokenStatus(account, status);
        log.debug("end listOfValueWithTokenStatus. list:{} ", JacksonUtils.objToString(list));
        return list;
    }

    private long aggregateBalance(List<String> valueList) {
        long balance = 0;
        for (String s : valueList) {
            balance += Integer.valueOf(s);
        }
        return balance;
    }

    /**
     * data request to raw data struct list
     */
    public List<TbDataInfo> dataJsonToRawDataList(String currentAccount, DataRequestInfo data) {
        List<TbDataInfo> dataInfoList = new ArrayList<>();

        String dataId = data.getKey();
        Iterator<Map.Entry<String,JsonNode>> jsonNodes = data.getValue().fields();
        while (jsonNodes.hasNext()) {
            Map.Entry<String, JsonNode> node = jsonNodes.next();
            TbDataInfo dataInfo = new TbDataInfo(currentAccount, dataId, node.getKey(), node.getValue().asText());
            dataInfoList.add(dataInfo);
        }
        return dataInfoList;
    }

    /**
     * raw data struct list to data json object
     */
    public JsonNode rawDataListToDataNode(List<TbDataInfo> dataInfoList) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode dataNode = objectMapper.createObjectNode();
        if (dataInfoList.size() > 0) {
            ((ObjectNode) dataNode).put("key", dataInfoList.get(0).getDataId());
        }
        for (int i = 0; i < dataInfoList.size(); i++) {
            TbDataInfo dataInfo = dataInfoList.get(i);
            ((ObjectNode) dataNode).put(dataInfo.getDataSubId(), dataInfo.getPlainText());
        }
        return dataNode;
    }

    private  Integer updateDataStatus(DataQueryParam accountInfo, String srcDataStatus, String desDataStatus) {
        log.debug("start updateDataStatus. accountInfo:{} status:{}->{} ",
                JacksonUtils.objToString(accountInfo), srcDataStatus, desDataStatus);
        Integer count = dataMapper.updateDataStatus(accountInfo.getAccount(), accountInfo.getDataId(),
                accountInfo.getDataSubId(), srcDataStatus, desDataStatus);
        log.debug("end updateDataStatus. count:{} ", count);
        return count;
    }
}
