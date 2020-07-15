/**
 * Copyright 2014-2020 the original author or authors.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fisco.bcos.safekeeper.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.safekeeper.base.code.ConstantCode;
import org.fisco.bcos.safekeeper.base.exception.SafeKeeperException;
import org.fisco.bcos.safekeeper.base.tools.JacksonUtils;
import org.fisco.bcos.safekeeper.data.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** services for data. */
@Log4j2
@Service
public class DataService {

    @Autowired private DataMapper dataMapper;

    static final String CREDIT_STATUS_AVAILABLE = "0";
    static final String CREDIT_STATUS_USED = "1";
    static final String CREDIT_STATUS_FROZEN = "2";

    /** add data row. */
    public void addDataRow(TbDataInfo dataInfo) throws SafeKeeperException {
        log.debug("start addDataRow. data info:{}", JacksonUtils.objToString(dataInfo));

        // check data
        DataQueryParam queryParams =
                new DataQueryParam(
                        dataInfo.getAccount(),
                        dataInfo.getDataEntityId(),
                        dataInfo.getDataFieldId());
        dataNotExist(queryParams);

        // add data row
        Integer affectRow = dataMapper.addDataRow(dataInfo);

        // check result
        // checkDbAffectRow(affectRow);

        log.debug("end addDataRow. affectRow:{}", affectRow);
    }

    /** add data batch. */
    @Transactional
    public void addDataBatch(List<TbDataInfo> dataInfoList) throws SafeKeeperException {
        log.debug("start addDataBatch.");

        Integer affectRow = 0;
        for (int i = 0; i < dataInfoList.size(); i++) {
            TbDataInfo dataInfo = dataInfoList.get(i);
            log.debug("data[{}] info:{}", i, JacksonUtils.objToString(dataInfo));

            // check data no exist
            DataQueryParam dataQueryParam =
                    new DataQueryParam(
                            dataInfo.getAccount(),
                            dataInfo.getDataEntityId(),
                            dataInfo.getDataFieldId());
            dataNotExist(dataQueryParam);

            // add data
            affectRow += dataMapper.addDataRow(dataInfo);
        }
        if (affectRow != dataInfoList.size()) {
            throw new SafeKeeperException(ConstantCode.INSERT_DATA_ERROR);
        }

        log.debug("end addDataBatch. affectRow:{}", dataInfoList.size());
    }

    /** update data row. */
    public Integer updateDataRow(TbDataInfo dataInfo) throws SafeKeeperException {
        log.debug("start updateDataRow. data info:{}", JacksonUtils.objToString(dataInfo));

        // update data row
        Integer affectRow = dataMapper.updateDataRow(dataInfo);

        log.debug("end updateDataRow. affectRow:{}", affectRow);

        return affectRow;
    }

    /** update data batch. */
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

    /** query the data. */
    public List<TbDataInfo> queryData(DataQueryParam queryParams) {
        log.debug("start queryData. query info:{}", JacksonUtils.objToString(queryParams));
        List<TbDataInfo> dataRow = dataMapper.queryData(queryParams);
        log.debug("end queryData. accountRow:{} ", JacksonUtils.objToString(dataRow));
        return dataRow;
    }

    /** query count of data. */
    public int countOfData(
            String account, String dataEntityId, String dataFieldId, int dataStatus) {
        log.debug(
                "start countOfData. account: {} dataEntityId: {} dataFieldId: {} dataStatus: {} ",
                account,
                dataEntityId,
                dataFieldId,
                dataStatus);
        Integer dataCount = dataMapper.countOfData(account, dataEntityId, dataFieldId, dataStatus);
        int count = dataCount == null ? 0 : dataCount.intValue();
        log.debug("end countOfData. count: {} ", count);
        return count;
    }

    /** query data list. */
    public List<TbDataInfo> listOfData(DataListParam param) {
        log.debug("start listOfData. param:{} ", JacksonUtils.objToString(param));
        List<TbDataInfo> list = dataMapper.listOfData(param);
        log.debug("end listOfData. list:{} ", JacksonUtils.objToString(list));
        return list;
    }

    /** delete data info. */
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

    /** query existence of data. */
    public int existOfData(DataQueryParam queryParams) {
        log.debug("start existOfData. info:{} ", JacksonUtils.objToString(queryParams));
        Integer dataCount = dataMapper.existOfData(queryParams);
        int count = dataCount == null ? 0 : dataCount.intValue();
        log.debug("end existOfData. count:{} ", count);
        return count;
    }

    /** boolean the data is exist. */
    public void dataExist(DataQueryParam queryParams) throws SafeKeeperException {
        if (StringUtils.isBlank(queryParams.getDataEntityId())) {
            throw new SafeKeeperException(ConstantCode.EMPTY_DATA_ENTITY_ID);
        }
        if (StringUtils.isBlank(queryParams.getDataFieldId())) {
            throw new SafeKeeperException(ConstantCode.EMPTY_DATA_FIELD_ID);
        }
        int count = existOfData(queryParams);
        if (count == 0) {
            throw new SafeKeeperException(ConstantCode.DATA_NOT_EXISTS);
        }
    }

    /** boolean the data is not exist. */
    public void dataNotExist(DataQueryParam queryParams) throws SafeKeeperException {
        if (StringUtils.isBlank(queryParams.getDataEntityId())) {
            throw new SafeKeeperException(ConstantCode.EMPTY_DATA_ENTITY_ID);
        }
        if (StringUtils.isBlank(queryParams.getDataFieldId())) {
            throw new SafeKeeperException(ConstantCode.EMPTY_DATA_FIELD_ID);
        }
        int count = existOfData(queryParams);
        if (count > 0) {
            throw new SafeKeeperException(ConstantCode.DATA_EXISTS);
        }
    }

    /** check db affect row. */
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

    public List<String> listOfDataIdByCoinStatus(String account, String status) {
        log.debug("start listOfDataIdByCoinStatus. account:{} status: {} ", account, status);
        List<String> list = dataMapper.listOfDataIdByCreditStatus(account, status);
        log.debug("end listOfDataIdByCoinStatus. list:{} ", JacksonUtils.objToString(list));
        return list;
    }

    @Transactional
    public JsonNode preAuthorization(String account, long target) {
        boolean find = false;
        List<CreditInfo> credits = listOfCreditWithCreditStatus(account, CREDIT_STATUS_AVAILABLE);
        List<String> selected = new ArrayList<>();
        long retValue = 0;

        for (int i = 0; i < credits.size(); i++) {
            CreditInfo credit = credits.get(i);
            credit.setValue(Long.parseLong(credit.getText()));
            log.debug(credit.getKey() + " " + credit.getValue());
            if (target == credit.getValue()) {
                String dataEntityId = credit.getKey();
                DataQueryParam dataQueryParam = new DataQueryParam(account, dataEntityId, "status");
                Integer count =
                        updateCreditStatus(
                                dataQueryParam, CREDIT_STATUS_AVAILABLE, CREDIT_STATUS_FROZEN);
                if (count > 0) {
                    selected.add(dataEntityId);
                    retValue += credit.getValue();
                    find = true;
                    break;
                }
            }
        }

        if (!find) {
            long totalValue = 0;
            log.debug("credentialList sorted by value. size: {} ", credits.size());
            for (int i = 0; i < credits.size(); i++) {
                CreditInfo credit = credits.get(i);
                log.debug(credit.getKey() + " " + credit.getValue());
                totalValue += credit.getValue();
            }

            if (target > totalValue) {
                log.debug(
                        "the account has not sufficient credits. target: {} only: {} ",
                        target,
                        totalValue);
                throw new SafeKeeperException(ConstantCode.NOT_SUFFICIENT_CREDITS);
            }

            // sort by value
            CreditInfo tmp;
            for (int i = 0; i < credits.size() - 1; i++) {
                for (int j = credits.size() - 1; j > 0; j--) {
                    if (credits.get(j - 1).getValue() < credits.get(j).getValue()) {
                        tmp = credits.get(j);
                        credits.set(j, credits.get(j - 1));
                        credits.set(j - 1, tmp);
                    }
                }
            }

            long remain = target;
            for (CreditInfo credit : credits) {
                String dataEntityId = credit.getKey();
                DataQueryParam dataQueryParam = new DataQueryParam(account, dataEntityId, "status");
                Integer count =
                        updateCreditStatus(
                                dataQueryParam, CREDIT_STATUS_AVAILABLE, CREDIT_STATUS_FROZEN);

                if (count > 0) {
                    remain -= credit.getValue();
                    selected.add(dataEntityId);
                    retValue += credit.getValue();
                    if (remain <= 0) {
                        break;
                    }
                }
            }

            // 多人竞争可能在这里都失败
            if (remain > 0) {
                log.debug(
                        "the account has not sufficient credits. target: {} only: {} ",
                        target,
                        target - remain);
                // use @Transactional to roll back
                /*for (String dataEntityId : selected) {
                    DataQueryParam dataQueryParam = new DataQueryParam(account, dataEntityId, "status");
                    Integer count = updateCreditStatus(dataQueryParam, CREDIT_STATUS_FROZEN, CREDIT_STATUS_AVAILABLE);
                    checkDbAffectRow(count);
                }*/
                throw new SafeKeeperException(ConstantCode.NOT_SUFFICIENT_CREDITS);
            }
        }

        List<JsonNode> listOfData = new ArrayList<>();
        for (String dataEntityId : selected) {
            DataQueryParam queryParams = new DataQueryParam(account, dataEntityId);
            List<TbDataInfo> dataInfoList = queryData(queryParams);
            JsonNode dateNode = rawDataListToDataNode(dataInfoList);
            listOfData.add(dateNode);
        }

        PreAuthorizationResult result = new PreAuthorizationResult();
        result.setCreditList(listOfData);
        result.setCreditValue(retValue);

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = JacksonUtils.objToString(result);
        JsonNode jsonNode = JacksonUtils.stringToJsonNode(jsonString);

        return jsonNode;
    }

    public List<CreditInfo> listOfCreditWithCreditStatus(String account, String status) {
        log.debug("start listOfCreditWithCreditStatus. account:{} status:{} ", account, status);
        List<CreditInfo> list = dataMapper.listOfCreditWithCreditStatus(account, status);
        log.debug("end listOfCreditWithCreditStatus. list:{} ", JacksonUtils.objToString(list));
        return list;
    }

    public JsonNode balance(String account) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode dataNode = objectMapper.createObjectNode();
        List<String> unspentList = listOfValueWithCreditStatus(account, CREDIT_STATUS_AVAILABLE);
        ((ObjectNode) dataNode).put("balance", aggregateBalance(unspentList));
        return dataNode;
    }

    public JsonNode expenditure(String account) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode dataNode = objectMapper.createObjectNode();
        List<String> spentList = listOfValueWithCreditStatus(account, CREDIT_STATUS_USED);
        ((ObjectNode) dataNode).put("expenditure", aggregateBalance(spentList));
        return dataNode;
    }

    private List<String> listOfValueWithCreditStatus(String account, String status) {
        log.debug("start listOfValueWithCreditStatus. account:{} status:{} ", account, status);
        List<String> list = dataMapper.listOfValueWithCreditStatus(account, status);
        log.debug("end listOfValueWithCreditStatus. list:{} ", JacksonUtils.objToString(list));
        return list;
    }

    private long aggregateBalance(List<String> valueList) {
        long balance = 0;
        for (String s : valueList) {
            balance += Integer.valueOf(s);
        }
        return balance;
    }

    /** data request to raw data struct list */
    public List<TbDataInfo> dataJsonToRawDataList(String currentAccount, DataRequestInfo data) {
        List<TbDataInfo> dataInfoList = new ArrayList<>();

        String dataEntityId = data.getKey();
        Iterator<Map.Entry<String, JsonNode>> jsonNodes = data.getValue().fields();
        while (jsonNodes.hasNext()) {
            Map.Entry<String, JsonNode> node = jsonNodes.next();
            TbDataInfo dataInfo =
                    new TbDataInfo(
                            currentAccount, dataEntityId, node.getKey(), node.getValue().asText());
            dataInfoList.add(dataInfo);
        }
        return dataInfoList;
    }

    /** raw data struct list to data json object */
    public JsonNode rawDataListToDataNode(List<TbDataInfo> dataInfoList) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode dataNode = objectMapper.createObjectNode();
        LocalDateTime time = null;
        if (dataInfoList.size() > 0) {
            ((ObjectNode) dataNode).put("key", dataInfoList.get(0).getDataEntityId());
            time = dataInfoList.get(0).getModifyTime();
        }
        for (int i = 0; i < dataInfoList.size(); i++) {
            TbDataInfo dataInfo = dataInfoList.get(i);
            ((ObjectNode) dataNode).put(dataInfo.getDataFieldId(), dataInfo.getDataFieldValue());
            LocalDateTime tmp = dataInfo.getModifyTime();
            if (tmp.isAfter(time)) {
                time = tmp;
            }
        }
        ZonedDateTime zonedDateTime = ZonedDateTime.of(time, ZoneId.of("UTC"));
        ((ObjectNode) dataNode)
                .put("lastModifyTime", zonedDateTime.format(DateTimeFormatter.ISO_INSTANT));
        return dataNode;
    }

    private Integer updateCreditStatus(
            DataQueryParam accountInfo, String srcDataStatus, String desDataStatus) {
        log.debug(
                "start updateCreditStatus. accountInfo:{} status:{}->{} ",
                JacksonUtils.objToString(accountInfo),
                srcDataStatus,
                desDataStatus);
        Integer count =
                dataMapper.updateCreditStatus(
                        accountInfo.getAccount(),
                        accountInfo.getDataEntityId(),
                        accountInfo.getDataFieldId(),
                        srcDataStatus,
                        desDataStatus);
        log.debug("end updateCreditStatus. count:{} ", count);
        return count;
    }
}
