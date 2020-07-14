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
package org.fisco.bcos.safekeeper.dataescrow;

import java.util.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.safekeeper.base.code.ConstantCode;
import org.fisco.bcos.safekeeper.base.exception.SafeKeeperException;
import org.fisco.bcos.safekeeper.base.tools.JacksonUtils;
import org.fisco.bcos.safekeeper.dataescrow.entity.DataEscrowListParam;
import org.fisco.bcos.safekeeper.dataescrow.entity.EscrowedDataInfo;
import org.fisco.bcos.safekeeper.dataescrow.entity.TbDataEscrowInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** services for data to escrow. */
@Log4j2
@Service
public class DataEscrowService {

    @Autowired private DataEscrowMapper dataEscrowMapper;

    /** add data escrow row. */
    public void addDataEscrowRow(String account, EscrowedDataInfo dataInfo)
            throws SafeKeeperException {
        log.debug(
                "start addDataEscrowRow. account:{}, data info:{}",
                account,
                JacksonUtils.objToString(dataInfo));

        String dataEntityId = dataInfo.getDataEntityId();
        String creatorCipherText = dataInfo.getCreatorCipherText();
        String userCipherText = dataInfo.getUserCipherText();

        // check data no exist
        dataNotExist(account, dataEntityId);

        // add data row
        TbDataEscrowInfo rowInfo =
                new TbDataEscrowInfo(
                        account, dataEntityId, creatorCipherText, userCipherText, null);
        Integer affectRow = dataEscrowMapper.addDataRow(rowInfo);

        // check result
        checkDbAffectRow(affectRow);

        log.debug("end addDataEscrowRow. affectRow:{}", affectRow);
    }

    /** query the data. */
    public TbDataEscrowInfo queryDataEscrow(String account, String dataEntityId) {
        log.debug("start queryDataEscrow. account:{}, dataEntityId:{} ", account, dataEntityId);
        TbDataEscrowInfo dataRow = dataEscrowMapper.queryData(account, dataEntityId);
        log.debug("end queryDataEscrow. accountRow:{} ", JacksonUtils.objToString(dataRow));
        return dataRow;
    }

    /** query count of data. */
    public int countOfDataOwnedByAccount(String account) {
        log.debug("start countOfDataOwnedByAccount. account:{} ", account);
        Integer dataCount = dataEscrowMapper.countOfDataOwnedByAccount(account);
        int count = dataCount == null ? 0 : dataCount.intValue();
        log.debug("end countOfDataOwnedByAccount. count:{} ", count);
        return count;
    }

    /** query data list. */
    public List<TbDataEscrowInfo> listOfDataOwnedByAccount(DataEscrowListParam param) {
        log.debug("start listOfDataOwnedByAccount. param:{} ", JacksonUtils.objToString(param));
        List<TbDataEscrowInfo> list = dataEscrowMapper.listOfDataOwnedByAccount(param);
        log.debug("end listOfDataOwnedByAccount. list:{} ", JacksonUtils.objToString(list));
        return list;
    }

    /** delete escrow data info. */
    public void deleteDataRow(String account, String dataEntityId) throws SafeKeeperException {
        log.debug("start deleteDataRow. account:{}, dataEntityId:{} ", account, dataEntityId);

        // check data
        dataExist(account, dataEntityId);

        // delete account row
        Integer affectRow = dataEscrowMapper.deleteDataRow(account, dataEntityId);

        // check result
        checkDbAffectRow(affectRow);

        log.debug("end deleteDataRow. affectRow:{} ", affectRow);
    }

    /** query count of data. */
    public int countOfData(String account, String dataEntityId) {
        log.debug("start countOfData. account:{} dataEntityId:{} ", account, dataEntityId);
        Integer accountCount = dataEscrowMapper.countOfData(account, dataEntityId);
        int count = accountCount == null ? 0 : accountCount.intValue();
        log.debug("end countOfData. count:{} ", count);
        return count;
    }

    /** boolean the data is exist. */
    public void dataExist(String account, String dataEntityId) throws SafeKeeperException {
        if (StringUtils.isBlank(account)) {
            throw new SafeKeeperException(ConstantCode.EMPTY_ACCOUNT_NAME);
        }
        if (StringUtils.isBlank(dataEntityId)) {
            throw new SafeKeeperException(ConstantCode.EMPTY_DATA_ESCROW_ID);
        }
        int count = countOfData(account, dataEntityId);
        if (count == 0) {
            throw new SafeKeeperException(ConstantCode.DATA_ESCROW_NOT_EXISTS);
        }
    }

    /** boolean the data is not exist. */
    public void dataNotExist(String account, String dataEntityId) throws SafeKeeperException {
        if (StringUtils.isBlank(account)) {
            throw new SafeKeeperException(ConstantCode.EMPTY_ACCOUNT_NAME);
        }
        if (StringUtils.isBlank(dataEntityId)) {
            throw new SafeKeeperException(ConstantCode.EMPTY_DATA_ESCROW_ID);
        }
        int count = countOfData(account, dataEntityId);
        if (count > 0) {
            throw new SafeKeeperException(ConstantCode.DATA_ESCROW_EXISTS);
        }
    }

    /** check db affect row. */
    private void checkDbAffectRow(Integer affectRow) throws SafeKeeperException {
        if (affectRow == 0) {
            log.warn("affect 0 rows of tb_data_escrow_info");
            throw new SafeKeeperException(ConstantCode.DB_EXCEPTION);
        }
    }
}
