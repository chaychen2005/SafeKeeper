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
package org.fisco.bcos.key.mgr.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jdk.nashorn.internal.parser.Token;
import org.fisco.bcos.key.mgr.base.code.ConstantCode;
import org.fisco.bcos.key.mgr.base.exception.KeyMgrException;
import org.fisco.bcos.key.mgr.base.tools.JacksonUtils;
import org.fisco.bcos.key.mgr.data.entity.*;
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
    public void addDataRow(TbDataInfo dataInfo) throws KeyMgrException {
        log.debug("start addDataRow. data info:{}", JacksonUtils.objToString(dataInfo));

        // check data
        DataQueryParam queryParams = new DataQueryParam(dataInfo.getAccount(), dataInfo.getDataID(), dataInfo.getDataSubID());
        dataNotExist(queryParams);

        // add data row
        Integer affectRow = dataMapper.addDataRow(dataInfo);

        // check result
        checkDbAffectRow(affectRow);

        log.debug("end addDataRow. affectRow:{}", affectRow);
    }

    /**
     * add data batch.
     */
    @Transactional
    public void addDataBatch(List<TbDataInfo> dataInfoList) throws KeyMgrException {
        log.debug("start addDataBatch.");

        for (int i = 0; i < dataInfoList.size(); i++) {
            TbDataInfo dataInfo = dataInfoList.get(i);
            log.debug("data[{}] info:{}", i, JacksonUtils.objToString(dataInfo));
            // check data
            DataQueryParam queryParams = new DataQueryParam(dataInfo.getAccount(), dataInfo.getDataID(), dataInfo.getDataSubID());
            dataNotExist(queryParams);
            // add data batch
            Integer affectRow = dataMapper.addDataRow(dataInfo);
            // check result
            checkDbAffectRow(affectRow);
        }

        log.debug("end addDataBatch. affectRow:{}", dataInfoList.size());
    }

    /**
     * update data row.
     */
    public void updateDataRow(TbDataInfo dataInfo) throws KeyMgrException {
        log.debug("start updateDataRow. data info:{}", JacksonUtils.objToString(dataInfo));

        // check data
        DataQueryParam queryParams = new DataQueryParam(dataInfo.getAccount(), dataInfo.getDataID(), dataInfo.getDataSubID());
        dataExist(queryParams);

        // update data row
        Integer affectRow = dataMapper.updateDataRow(dataInfo);

        // check result
        checkDbAffectRow(affectRow);

        log.debug("end updateDataRow. affectRow:{}", affectRow);
    }

    /**
     * update data batch.
     */
    @Transactional
    public void updateDataBatch(List<TbDataInfo> dataInfoList) throws KeyMgrException {
        log.debug("start addDataBatch.");

        for (int i = 0; i < dataInfoList.size(); i++) {
            TbDataInfo dataInfo = dataInfoList.get(i);
            log.debug("data[{}] info:{}", i, JacksonUtils.objToString(dataInfo));
            // check data
            DataQueryParam queryParams = new DataQueryParam(dataInfo.getAccount(), dataInfo.getDataID(), dataInfo.getDataSubID());
            dataExist(queryParams);
            // update data batch
            Integer affectRow = dataMapper.updateDataRow(dataInfo);
            // check result
            checkDbAffectRow(affectRow);
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
    public int countOfData(String account, String dataID, String dataSubID, int dataStatus) {
        log.debug("start countOfData. account: {} dataID: {} dataSubID: {} dataStatus: {} ",
                account, dataID, dataSubID, dataStatus);
        Integer keyCount = dataMapper.countOfData(account, dataID, dataSubID, dataStatus);
        int count = keyCount == null ? 0 : keyCount.intValue();
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
    public void deleteDataRow(DataQueryParam queryParams) throws KeyMgrException {
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
    public void dataExist(DataQueryParam queryParams) throws KeyMgrException {
        if (StringUtils.isBlank(queryParams.getDataID())) {
            throw new KeyMgrException(ConstantCode.DATA_ID_EMPTY);
        }
        if (StringUtils.isBlank(queryParams.getDataSubID())) {
            throw new KeyMgrException(ConstantCode.DATA_SUB_ID_EMPTY);
        }
        int count = existOfData(queryParams);
        if (count == 0) {
            throw new KeyMgrException(ConstantCode.DATA_NOT_EXISTS);
        }
    }

    /**
     * boolean the data is not exist.
     */
    public void dataNotExist(DataQueryParam queryParams) throws KeyMgrException {
        if (StringUtils.isBlank(queryParams.getDataID())) {
            throw new KeyMgrException(ConstantCode.DATA_ID_EMPTY);
        }
        if (StringUtils.isBlank(queryParams.getDataSubID())) {
            throw new KeyMgrException(ConstantCode.DATA_SUB_ID_EMPTY);
        }
        int count = existOfData(queryParams);
        if (count > 0) {
            throw new KeyMgrException(ConstantCode.DATA_EXISTS);
        }
    }

    /**
     * check db affect row.
     */
    private void checkDbAffectRow(Integer affectRow) throws KeyMgrException {
        if (affectRow == 0) {
            log.warn("affect 0 rows of tb_data_info");
            throw new KeyMgrException(ConstantCode.DB_EXCEPTION);
        }
    }

    public List<String> listOfDataID(String account, int status) {
        log.debug("start listOfDataID. account:{} status: {} ", account, status);
        List<String> list = dataMapper.listOfDataID(account, status);
        log.debug("end listOfDataID. list:{} ", JacksonUtils.objToString(list));
        return list;
    }

    public List<JsonNode> getCredentialList(String account, long target) {
        List<TokenInfo> tokens = listOfTokenWithTokenStatus(account, "0");
        List<String> selectedTokens  = new ArrayList<>();
        boolean find = false;

        for (int i = 0; i < tokens.size(); i++) {
            TokenInfo token = tokens.get(i);
            token.setValue(Integer.valueOf(token.getText()));
            log.debug(token.getKey() + " " + token.getText() + " " + token.getValue());
            if (target == token.getValue()) {
                selectedTokens.add(token.getKey());
                find = true;
                break;
            }
        }

        if (!find) {
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

            long totalValue = 0;
            log.debug("after sort by value in getCredentialList. size: {} ", tokens.size());
            for (int i = 0; i < tokens.size(); i++) {
                TokenInfo token = tokens.get(i);
                log.debug(token.getKey() + " " + token.getText() + " " + token.getValue());
                totalValue += token.getValue();
            }

            if (target > totalValue) {
                log.debug("the account has not sufficient tokens. target: {} total: {} ", target, totalValue);
                throw new KeyMgrException(ConstantCode.NOT_SUFFICIENT_TOKENS);
            }

            for (TokenInfo token : tokens) {
                selectedTokens.add(token.getKey());
                target -= token.getValue();
                if (target < 0) {
                    break;
                }
            }
        }

        List<JsonNode> listOfData = new ArrayList<>();
        for (String dataID : selectedTokens) {
            DataQueryParam queryParams = new DataQueryParam(account, dataID);
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

    public JsonNode getUnspent(String account) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode dataNode = objectMapper.createObjectNode();
        List<String> unspentList = listOfValueWithTokenStatus(account,"0");
        ((ObjectNode) dataNode).put("unspentTotalValue", aggregateBalance(unspentList));
        return dataNode;
    }

    public JsonNode getSpent(String account) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode dataNode = objectMapper.createObjectNode();
        List<String> spentList = listOfValueWithTokenStatus(account,"1");
        ((ObjectNode) dataNode).put("spentTotalValue", aggregateBalance(spentList));
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

        String dataID = data.getKey();
        Iterator<Map.Entry<String,JsonNode>> jsonNodes = data.getValue().fields();
        while (jsonNodes.hasNext()) {
            Map.Entry<String, JsonNode> node = jsonNodes.next();
            TbDataInfo dataInfo = new TbDataInfo(currentAccount, dataID, node.getKey(), node.getValue().asText(),
                        "", "", "");
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
            ((ObjectNode) dataNode).put("key", dataInfoList.get(0).getDataID());
        }
        for (int i = 0; i < dataInfoList.size(); i++) {
            TbDataInfo dataInfo = dataInfoList.get(i);
            ((ObjectNode) dataNode).put(dataInfo.getDataSubID(), dataInfo.getPlainText());
        }
        return dataNode;
    }
}
