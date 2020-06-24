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
package org.fisco.bcos.safekeeper.account;

import org.fisco.bcos.safekeeper.account.entity.AccountInfo;
import org.fisco.bcos.safekeeper.account.entity.AccountListParam;
import org.fisco.bcos.safekeeper.account.entity.LoginInfo;
import org.fisco.bcos.safekeeper.account.entity.TbAccountInfo;
import org.fisco.bcos.safekeeper.base.code.ConstantCode;
import org.fisco.bcos.safekeeper.base.enums.AccountStatus;
import org.fisco.bcos.safekeeper.base.exception.SafeKeeperException;
import org.fisco.bcos.safekeeper.base.tools.JacksonUtils;
import org.fisco.bcos.safekeeper.base.tools.SafeKeeperTools;
import org.fisco.bcos.safekeeper.role.RoleService;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * services for account data.
 */
@Log4j2
@Service
public class AccountService {

    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private RoleService roleService;
    @Qualifier(value = "bCryptPasswordEncoder")
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * login.
     */
    public TbAccountInfo login(LoginInfo loginInfo) throws SafeKeeperException {
        log.info("start login. loginInfo:{}", JacksonUtils.objToString(loginInfo));
        String accountStr = loginInfo.getAccount();
        String passwordStr = loginInfo.getAccountPwd();

        if (!SafeKeeperTools.checkUserName(accountStr)) {
            log.info("fail login. invalid account format");
            throw new SafeKeeperException(ConstantCode.INVALID_ACCOUNT_FORMAT);
        }

        if (!SafeKeeperTools.checkPassword(passwordStr)) {
            log.info("fail login. invalid password format");
            throw new SafeKeeperException(ConstantCode.INVALID_PASSWORD_FORMAT);
        }

        // check account
        accountExist(accountStr);

        // check pwd
        if (StringUtils.isBlank(passwordStr)) {
            log.info("fail login. password is null");
            throw new SafeKeeperException(ConstantCode.PASSWORD_ERROR);
        }
        // encode by bCryptPasswordEncoder
        TbAccountInfo accountRow = accountMapper.queryByAccount(accountStr);
        if (!passwordEncoder.matches(passwordStr, accountRow.getAccountPwd())) {
            throw new SafeKeeperException(ConstantCode.PASSWORD_ERROR);
        }

        return accountRow;
    }

    /**
     * add account row.
     */
    public void addAccountRow(AccountInfo accountInfo, String creator) throws SafeKeeperException {
        log.debug("start addAccountRow.  AccountInfo:{}, creator:{} ", JacksonUtils.objToString(accountInfo), creator);

        String accountStr = accountInfo.getAccount();
        String passwordStr = accountInfo.getAccountPwd();
        Integer roleId = accountInfo.getRoleId();
        String publicKey = accountInfo.getPublicKey();

        if (!SafeKeeperTools.checkUserName(accountStr)) {
            log.info("fail addAccountRow. invalid account format");
            throw new SafeKeeperException(ConstantCode.INVALID_ACCOUNT_FORMAT);
        }

        if (!SafeKeeperTools.checkPassword(passwordStr)) {
            log.info("fail addAccountRow. invalid password format");
            throw new SafeKeeperException(ConstantCode.INVALID_PASSWORD_FORMAT);
        }

        if (roleId != 100001 && publicKey.length() != 128) {
            log.info("fail addAccountRow. invalid public key length for admin");
            throw new SafeKeeperException(ConstantCode.INVALID_PUBLIC_KEY_LENGTH);
        }

        // check account
        accountNotExist(accountStr);
        // check role id
        roleService.roleIdExist(roleId);
        // encode password
        String encryptStr = passwordEncoder.encode(passwordStr);
        // add account row
        TbAccountInfo rowInfo = new TbAccountInfo(accountStr, encryptStr, roleId, null, null, publicKey, creator);
        Integer affectRow = accountMapper.addAccountRow(rowInfo);

        // check result
        checkDbAffectRow(affectRow);

        log.debug("end addAccountRow. affectRow:{}", affectRow);
    }

    /**
     * update password.
     */
    public void updatePassword(String targetAccount, String oldAccountPwd, String newAccountPwd)
        throws SafeKeeperException {
        log.debug("start updatePassword. targetAccount:{} oldAccountPwd:{} newAccountPwd:{}",
            targetAccount, oldAccountPwd, newAccountPwd);

        // query target account info
        TbAccountInfo targetRow = accountMapper.queryByAccount(targetAccount);
        if (targetRow == null) {
            log.warn("fail updatePassword. not found target account row. targetAccount:{}",
                targetAccount);
            throw new SafeKeeperException(ConstantCode.ACCOUNT_NOT_EXISTS);
        }

        if (!SafeKeeperTools.checkPassword(newAccountPwd)) {
            log.info("fail updatePassword. invalid new password format");
            throw new SafeKeeperException(ConstantCode.INVALID_PASSWORD_FORMAT);
        }

        if (StringUtils.equals(oldAccountPwd, newAccountPwd)) {
            log.warn("fail updatePassword. the new password cannot be same as old ");
            throw new SafeKeeperException(ConstantCode.NOW_PWD_EQUALS_OLD);
        }

        // check old password
        if (!passwordEncoder.matches(oldAccountPwd, targetRow.getAccountPwd())) {
            throw new SafeKeeperException(ConstantCode.PASSWORD_ERROR);
        }

        // update password
        targetRow.setAccountPwd(passwordEncoder.encode(newAccountPwd));
        targetRow.setAccountStatus(AccountStatus.NORMAL.getValue());
        Integer affectRow = accountMapper.updateAccountRow(targetRow);

        // check result
        checkDbAffectRow(affectRow);

        log.debug("end updatePassword. affectRow:{}", affectRow);

    }

    /**
     * query account info by acountName.
     */
    public TbAccountInfo queryByAccount(String accountStr) {
        log.debug("start queryByAccount. accountStr:{} ", accountStr);
        TbAccountInfo accountRow = accountMapper.queryByAccount(accountStr);
        log.debug("end queryByAccount. accountRow:{} ", JacksonUtils.objToString(accountRow));
        return accountRow;
    }

    /**
     * query count of account.
     */
    public int countOfAccount(String account) {
        log.debug("start countOfAccount. account:{} ", account);
        Integer accountCount = accountMapper.countOfAccount(account);
        int count = accountCount == null ? 0 : accountCount.intValue();
        log.debug("end countOfAccount. count:{} ", count);
        return count;
    }

    /**
     * query account list.
     */
    public List<TbAccountInfo> listOfAccount(AccountListParam param) {
        log.debug("start listOfAccount. param:{} ", JacksonUtils.objToString(param));
        List<TbAccountInfo> list = accountMapper.listOfAccount(param);
        log.debug("end listOfAccount. list:{} ", JacksonUtils.objToString(list));
        return list;
    }

    /**
     * delete account info.
     */
    public void deleteAccountRow(String account) throws SafeKeeperException {
        log.debug("start deleteAccountRow. account:{} ", account);

        // check account
        accountExist(account);

        // delete account row
        Integer affectRow = accountMapper.deleteAccountRow(account);

        // check result
        checkDbAffectRow(affectRow);

        log.debug("end deleteAccountRow. affectRow:{} ", affectRow);

    }

    /**
     * boolean account is exist.
     */
    public void accountExist(String account) throws SafeKeeperException {
        if (StringUtils.isBlank(account)) {
            log.warn("fail isAccountExit. account:{}", account);
            throw new SafeKeeperException(ConstantCode.ACCOUNT_NAME_EMPTY);
        }
        int count = countOfAccount(account);
        if (count == 0) {
            throw new SafeKeeperException(ConstantCode.ACCOUNT_NOT_EXISTS);
        }
    }

    /**
     * boolean account is not exit.
     */
    public void accountNotExist(String account) throws SafeKeeperException {
        if (StringUtils.isBlank(account)) {
            log.warn("fail isAccountExit. account:{}", account);
            throw new SafeKeeperException(ConstantCode.ACCOUNT_NAME_EMPTY);
        }
        int count = countOfAccount(account);
        if (count > 0) {
            throw new SafeKeeperException(ConstantCode.ACCOUNT_EXISTS);
        }
    }

    /**
     * check db affect row.
     */
    private void checkDbAffectRow(Integer affectRow) throws SafeKeeperException {
        if (affectRow == 0) {
            log.warn("affect 0 rows of tb_account");
            throw new SafeKeeperException(ConstantCode.DB_EXCEPTION);
        }
    }
}
