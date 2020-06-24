
/**
 * Copyright 2014-2020  the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.fisco.bcos.safekeeper.dataescrow;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.fisco.bcos.safekeeper.account.AccountService;
import org.fisco.bcos.safekeeper.base.entity.BasePageResponse;
import org.fisco.bcos.safekeeper.base.enums.SqlSortType;
import org.fisco.bcos.safekeeper.base.properties.ConstantProperties;
import org.fisco.bcos.safekeeper.base.tools.JacksonUtils;
import org.fisco.bcos.safekeeper.dataescrow.entity.DataEscrowListParam;
import org.fisco.bcos.safekeeper.dataescrow.entity.EscrowedDataInfo;
import org.fisco.bcos.safekeeper.dataescrow.entity.TbDataEscrowInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.fisco.bcos.safekeeper.account.entity.TbAccountInfo;
import org.fisco.bcos.safekeeper.base.code.ConstantCode;
import org.fisco.bcos.safekeeper.base.controller.BaseController;
import org.fisco.bcos.safekeeper.base.entity.BaseResponse;
import org.fisco.bcos.safekeeper.base.exception.SafeKeeperException;
import org.fisco.bcos.safekeeper.base.tools.SafeKeeperTools;
import org.fisco.bcos.safekeeper.token.TokenService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping(value = "dataEscrow")
public class DataEscrowController extends BaseController {

    @Autowired
    private DataEscrowService dataService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TokenService tokenService;

    /**
     * add data info.
     */
    @PostMapping(value = "/add")
    @PreAuthorize(ConstantProperties.HAS_ROLE_VISITOR)
    public BaseResponse addData(@RequestBody @Valid EscrowedDataInfo dataInfo, BindingResult result)
            throws SafeKeeperException {
        checkBindResult(result);
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start addData. startTime:{} dataInfo:{}",
                startTime.toEpochMilli(), JacksonUtils.objToString(dataInfo));

        // current
        String currentAccount = getCurrentAccount(request);

        // add dat row
        dataService.addDataEscrowRow(currentAccount, dataInfo);

        // query row
        TbDataEscrowInfo tbDataEscrowInfo = dataService.queryDataEscrow(currentAccount, dataInfo.getDataID());
        baseResponse.setData(tbDataEscrowInfo);

        log.info("end addData useTime:{} result:{}", Duration.between(startTime, Instant.now()).toMillis(),
                JacksonUtils.objToString(baseResponse));
        return baseResponse;
    }

    /**
     * query data.
     */
    @GetMapping(value = "/query")
    public BaseResponse queryData(@RequestParam(value="account") String account,
                                  @RequestParam(value="dataID") String dataID) throws SafeKeeperException {
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start queryData. startTime:{} account:{} dataID:{}", startTime.toEpochMilli(), account, dataID);

        String currentAccount = getCurrentAccount(request);
        if (!account.equals(currentAccount)) {
            TbAccountInfo tbCurAccount = accountService.queryByAccount(account);
            if (tbCurAccount == null || !currentAccount.equals(tbCurAccount.getCreator())) {
                log.info("lack of access to the data");
                throw new SafeKeeperException(ConstantCode.LACK_ACCESS_DATA_ESCROW);
            }
        }

        int count = dataService.countOfData(account, dataID);
        if (count > 0) {
            TbDataEscrowInfo tbDataEscrowInfo = dataService.queryDataEscrow(account, dataID);
            baseResponse.setData(tbDataEscrowInfo);
        } else {
            log.info("data info not exists");
            throw new SafeKeeperException(ConstantCode.DATA_ESCROW_NOT_EXISTS);
        }

        log.info("end queryData useTime:{} result:{}", Duration.between(startTime, Instant.now()).toMillis(),
                JacksonUtils.objToString(baseResponse));
        return baseResponse;
    }

    /**
     * query data list.
     */
    @GetMapping(value = "/list")
    @PreAuthorize(ConstantProperties.HAS_ROLE_VISITOR)
    public BasePageResponse dataList(@RequestParam(value="pageNumber") Integer pageNumber,
                                     @RequestParam(value="pageSize") Integer pageSize) throws SafeKeeperException {
        BasePageResponse pagesponse = new BasePageResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();

        String account = getCurrentAccount(request);
        log.info("start dataList.  startTime:{} pageNumber:{} pageSize:{} account:{} ",
                startTime.toEpochMilli(), pageNumber, pageSize, account);

        int count = dataService.countOfDataOwnedByAccount(account);
        if (count > 0) {
            Integer start = Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize)
                    .orElse(0);
            DataEscrowListParam param = new DataEscrowListParam(start, pageSize, account, SqlSortType.DESC.getValue());
            List<TbDataEscrowInfo> listOfData = dataService.listOfDataOwnedByAccount(param);
            pagesponse.setData(listOfData);
            pagesponse.setTotalCount(count);
        }

        log.info("end dataList useTime:{} result:{}",
                Duration.between(startTime, Instant.now()).toMillis(), JacksonUtils.objToString(pagesponse));
        return pagesponse;
    }

    /**
     * delete data.
     */
    @DeleteMapping(value = "/delete")
    @PreAuthorize(ConstantProperties.HAS_ROLE_VISITOR)
    public BaseResponse deleteData(@RequestParam(value="dataID") String dataID) throws SafeKeeperException {
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        Instant startTime = Instant.now();
        log.info("start deleteData. startTime:{} dataID:{}", startTime.toEpochMilli(), dataID);

        String currentAccount = getCurrentAccount(request);

        dataService.deleteDataRow(currentAccount, dataID);

        log.info("end deleteData. useTime:{} result:{}", Duration.between(startTime, Instant.now()).toMillis(),
                JacksonUtils.objToString(baseResponse));
        return baseResponse;
    }

    /**
     * get current account.
     */
    private String getCurrentAccount(HttpServletRequest request) {
        String token = SafeKeeperTools.getToken(request);
        log.debug("getCurrentAccount account:{}", token);
        return tokenService.getValueFromToken(token);
    }
}
