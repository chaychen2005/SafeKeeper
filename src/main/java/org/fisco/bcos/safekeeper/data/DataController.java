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
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.fisco.bcos.safekeeper.account.AccountService;
import org.fisco.bcos.safekeeper.base.code.ConstantCode;
import org.fisco.bcos.safekeeper.base.controller.BaseController;
import org.fisco.bcos.safekeeper.base.entity.BasePageResponse;
import org.fisco.bcos.safekeeper.base.entity.BaseResponse;
import org.fisco.bcos.safekeeper.base.enums.DataStatus;
import org.fisco.bcos.safekeeper.base.exception.SafeKeeperException;
import org.fisco.bcos.safekeeper.base.properties.ConstantProperties;
import org.fisco.bcos.safekeeper.base.tools.JacksonUtils;
import org.fisco.bcos.safekeeper.base.tools.SafeKeeperTools;
import org.fisco.bcos.safekeeper.data.entity.*;
import org.fisco.bcos.safekeeper.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping(value = "data")
public class DataController extends BaseController {

    @Autowired private DataService dataService;
    @Autowired private AccountService accountService;
    @Autowired private TokenService tokenService;

    /** add raw data. */
    @PostMapping(value = "/v1")
    @PreAuthorize(ConstantProperties.HAS_ROLE_VISITOR)
    public BaseResponse addRawData(@RequestBody @Valid DataRequestInfo info, BindingResult result)
            throws SafeKeeperException {
        checkBindResult(result);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info(
                "start addRawData. startTime:{} dataInfo:{}",
                startTime.toEpochMilli(),
                JacksonUtils.objToString(info));

        // current
        String currentAccount = getCurrentAccount(request);

        // dataJson to independent data row
        List<TbDataInfo> dataInfoList = dataService.dataJsonToRawDataList(currentAccount, info);

        // add data row
        dataService.addDataBatch(dataInfoList);

        log.info(
                "end addRawData. useTime:{} result:{}",
                Duration.between(startTime, Instant.now()).toMillis(),
                JacksonUtils.objToString(baseResponse));
        return baseResponse;
    }

    /** update single raw data. */
    @PatchMapping(value = "/v1")
    @PreAuthorize(ConstantProperties.HAS_ROLE_VISITOR)
    public BaseResponse updateRawData(
            @RequestBody @Valid DataRequestInfo info, BindingResult result)
            throws SafeKeeperException {
        checkBindResult(result);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info(
                "start updateRawData. startTime:{} dataInfo:{}",
                startTime.toEpochMilli(),
                JacksonUtils.objToString(info));

        // current
        String currentAccount = getCurrentAccount(request);

        // dataJson to independent data row
        List<TbDataInfo> dataInfoList = dataService.dataJsonToRawDataList(currentAccount, info);

        // update data row
        dataService.updateDataBatch(dataInfoList);

        log.info(
                "end updateRawData. useTime:{} result:{}",
                Duration.between(startTime, Instant.now()).toMillis(),
                JacksonUtils.objToString(baseResponse));
        return baseResponse;
    }

    /** query raw data. */
    @GetMapping(value = "/v1/{dataEntityId}")
    public BaseResponse queryRawData(@PathVariable("dataEntityId") String dataEntityId)
            throws SafeKeeperException {
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info(
                "start queryRawData. startTime: {} dataEntityId:{} ",
                startTime.toEpochMilli(),
                dataEntityId);

        if (dataEntityId == null || dataEntityId.equals("")) {
            throw new SafeKeeperException(ConstantCode.PARAM_EXCEPTION);
        }

        // current
        String currentAccount = getCurrentAccount(request);

        // query
        DataQueryParam queryParams = new DataQueryParam(currentAccount, dataEntityId);
        List<TbDataInfo> dataInfoList = dataService.queryData(queryParams);
        if (dataInfoList.size() > 0) {
            baseResponse.setData(dataService.rawDataListToDataNode(dataInfoList));
        } else {
            log.info("data info not exists");
            throw new SafeKeeperException(ConstantCode.DATA_NOT_EXISTS);
        }

        log.info(
                "end queryRawData. useTime:{} result:{}",
                Duration.between(startTime, Instant.now()).toMillis(),
                JacksonUtils.objToString(baseResponse));
        return baseResponse;
    }

    /** delete raw data. */
    @DeleteMapping(value = "/v1/{dataEntityId}")
    @PreAuthorize(ConstantProperties.HAS_ROLE_VISITOR)
    public BaseResponse deleteRawData(@PathVariable("dataEntityId") String dataEntityId)
            throws SafeKeeperException {
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        String currentAccount = getCurrentAccount(request);
        log.info(
                "start deleteRawData. startTime:{} account: {} dataEntityId:{}",
                startTime.toEpochMilli(),
                currentAccount,
                dataEntityId);

        DataQueryParam queryParams = new DataQueryParam(currentAccount, dataEntityId);
        List<TbDataInfo> dataInfoList = dataService.queryData(queryParams);
        if (dataInfoList.size() == 0) {
            throw new SafeKeeperException(ConstantCode.DATA_NOT_EXISTS);
        }
        for (int i = 0; i < dataInfoList.size(); i++) {
            dataInfoList.get(i).setDataStatus(DataStatus.UNAVAILABLE.getValue());
            Integer count = dataService.updateDataRow(dataInfoList.get(i));
            dataService.checkDbAffectRow(count);
        }

        log.info(
                "end deleteRawData. useTime:{} result:{}",
                Duration.between(startTime, Instant.now()).toMillis(),
                JacksonUtils.objToString(baseResponse));
        return baseResponse;
    }

    /** query raw data list. */
    @GetMapping(value = "/v1")
    @PreAuthorize(ConstantProperties.HAS_ROLE_VISITOR)
    public BasePageResponse listRawData(
            @RequestParam(value = "pageNumber") Integer pageNumber,
            @RequestParam(value = "pageSize") Integer pageSize)
            throws SafeKeeperException {
        BasePageResponse pageResponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info(
                "start listRawData. startTime:{} pageNumber:{} pageSize:{} ",
                startTime.toEpochMilli(),
                pageNumber,
                pageSize);

        String currentAccount = getCurrentAccount(request);

        List<String> dataEntityIdList =
                dataService.listOfDataId(currentAccount, DataStatus.AVAILABLE.getValue());
        int count = dataEntityIdList.size();
        List<JsonNode> listOfData = new ArrayList<>();
        if (count > 0) {
            Map<String, Integer> map = getQueryRange(pageNumber, pageSize, count);
            for (Integer i = map.get("start"); i < map.get("end"); i++) {
                DataQueryParam queryParams =
                        new DataQueryParam(currentAccount, dataEntityIdList.get(i));
                List<TbDataInfo> dataInfoList = dataService.queryData(queryParams);
                listOfData.add(dataService.rawDataListToDataNode(dataInfoList));
            }
        }
        pageResponse.setData(listOfData);
        pageResponse.setTotalCount(count);

        log.info(
                "end listRawData. useTime:{} result:{}",
                Duration.between(startTime, Instant.now()).toMillis(),
                JacksonUtils.objToString(pageResponse));
        return pageResponse;
    }

    /** query raw data list. */
    @GetMapping(value = "/wedpr/vcl/v1/credentials")
    @PreAuthorize(ConstantProperties.HAS_ROLE_VISITOR)
    public BasePageResponse listCredentialsByStatus(
            @RequestParam(value = "pageNumber") Integer pageNumber,
            @RequestParam(value = "pageSize") Integer pageSize,
            @RequestParam(value = "credentialStatus") String status)
            throws SafeKeeperException {
        BasePageResponse pageResponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info(
                "start listRawData. startTime:{} pageNumber:{} pageSize:{} status:{}",
                startTime.toEpochMilli(),
                pageNumber,
                pageSize,
                status);

        String currentAccount = getCurrentAccount(request);

        List<String> dataEntityIdList =
                dataService.listOfDataIdByCoinStatus(currentAccount, status);
        int count = dataEntityIdList.size();
        List<JsonNode> listOfData = new ArrayList<>();
        if (count > 0) {
            Map<String, Integer> map = getQueryRange(pageNumber, pageSize, count);
            for (Integer i = map.get("start"); i < map.get("end"); i++) {
                DataQueryParam queryParams =
                        new DataQueryParam(currentAccount, dataEntityIdList.get(i));
                List<TbDataInfo> dataInfoList = dataService.queryData(queryParams);
                listOfData.add(dataService.rawDataListToDataNode(dataInfoList));
            }
        }
        pageResponse.setData(listOfData);
        pageResponse.setTotalCount(count);

        log.info(
                "end listRawData. useTime:{} result:{}",
                Duration.between(startTime, Instant.now()).toMillis(),
                JacksonUtils.objToString(pageResponse));
        return pageResponse;
    }

    /** get total value of unspent credits . */
    @GetMapping(value = "/wedpr/vcl/v1/credentials/balance")
    @PreAuthorize(ConstantProperties.HAS_ROLE_VISITOR)
    public BaseResponse balance() throws SafeKeeperException {
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        // current
        String currentAccount = getCurrentAccount(request);
        log.info(
                "start wedpr/vcl/v1/credentials/balance. account: {} startTime:{} ",
                currentAccount,
                startTime.toEpochMilli());

        // get total value
        JsonNode dataNode = dataService.balance(currentAccount);
        baseResponse.setData(dataNode);

        log.info(
                "end wedpr/vcl/v1/credentials/balance. useTime:{} result:{}",
                Duration.between(startTime, Instant.now()).toMillis(),
                JacksonUtils.objToString(baseResponse));
        return baseResponse;
    }

    /** get total value of spent credits . */
    @GetMapping(value = "/wedpr/vcl/v1/credentials/expenditure")
    @PreAuthorize(ConstantProperties.HAS_ROLE_VISITOR)
    public BaseResponse expenditure() throws SafeKeeperException {
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        // current
        String currentAccount = getCurrentAccount(request);
        log.info(
                "start wedpr/vcl/v1/credentials/expenditure. startTime:{} account: {} ",
                startTime.toEpochMilli(),
                currentAccount);

        // get total value
        JsonNode dataNode = dataService.expenditure(currentAccount);
        baseResponse.setData(dataNode);

        log.info(
                "end wedpr/vcl/v1/credentials/expenditure. useTime:{} result:{}",
                Duration.between(startTime, Instant.now()).toMillis(),
                JacksonUtils.objToString(baseResponse));
        return baseResponse;
    }

    /** approve and return credit list, credit status 0->2. */
    @PatchMapping(value = "/wedpr/vcl/v1/credentials/approve")
    @PreAuthorize(ConstantProperties.HAS_ROLE_VISITOR)
    public BaseResponse approveCredentialList(@RequestParam(value = "value") long value)
            throws SafeKeeperException {
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        // current
        String currentAccount = getCurrentAccount(request);
        log.info(
                "start wedpr/vcl/v1/credentials/approve. startTime: {} account: {} value: {}",
                startTime.toEpochMilli(),
                currentAccount,
                value);

        if (value <= 0) {
            throw new SafeKeeperException(ConstantCode.PARAM_EXCEPTION);
        }

        //  approve and return credit list/value
        JsonNode result = dataService.preAuthorization(currentAccount, value);
        baseResponse.setData(result);

        log.info(
                "end wedpr/vcl/v1/credentials/approve. useTime:{} result:{}",
                Duration.between(startTime, Instant.now()).toMillis(),
                JacksonUtils.objToString(baseResponse));
        return baseResponse;
    }

    /** update credit list. */
    @PatchMapping(value = "/wedpr/vcl/v1/credentials/spend")
    @PreAuthorize(ConstantProperties.HAS_ROLE_VISITOR)
    public BaseResponse updateRawDataBatch(
            @RequestBody @Valid String spendListInfo, BindingResult result)
            throws SafeKeeperException {
        checkBindResult(result);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info(
                "start wedpr/vcl/v1/credentials/spend. startTime:{} dataInfo:{}",
                startTime.toEpochMilli(),
                spendListInfo);

        // current
        String currentAccount = getCurrentAccount(request);

        // dataJson to independent data row
        List<TbDataInfo> dataInfoList = new ArrayList<>();
        JsonNode dataList = JacksonUtils.stringToJsonNode(spendListInfo);
        for (int i = 0; i < dataList.size(); i++) {
            JsonNode dataNode = dataList.get(i);
            String dataEntityId = dataNode.get("key").asText();
            Iterator<Map.Entry<String, JsonNode>> jsonNodes = dataNode.get("value").fields();
            while (jsonNodes.hasNext()) {
                Map.Entry<String, JsonNode> node = jsonNodes.next();
                TbDataInfo dataInfo =
                        new TbDataInfo(
                                currentAccount,
                                dataEntityId,
                                node.getKey(),
                                node.getValue().asText());
                dataInfoList.add(dataInfo);
            }
        }
        // update data row batch
        dataService.updateDataBatch(dataInfoList);

        log.info(
                "end wedpr/vcl/v1/credentials/spend. useTime:{} result:{}",
                Duration.between(startTime, Instant.now()).toMillis(),
                JacksonUtils.objToString(baseResponse));
        return baseResponse;
    }

    /** get current account. */
    private String getCurrentAccount(HttpServletRequest request) {
        String token = SafeKeeperTools.getToken(request);
        log.debug("getCurrentAccount account:{}", token);
        return tokenService.getAccountFromToken(token);
    }

    private Map<String, Integer> getQueryRange(
            Integer pageNumber, Integer pageSize, Integer totalCount) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        Integer start = (pageNumber - 1) * pageSize;
        if (start < 0) {
            start = 0;
        }
        Integer end = pageNumber * pageSize;
        if (end > totalCount) {
            end = totalCount;
        }
        map.put("start", start);
        map.put("end", end);
        return map;
    }
}
