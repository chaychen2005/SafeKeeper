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
package org.fisco.bcos.safekeeper.security;

import java.util.ArrayList;
import java.util.List;
import org.fisco.bcos.safekeeper.account.AccountService;
import org.fisco.bcos.safekeeper.account.entity.TbAccountInfo;
import org.fisco.bcos.safekeeper.base.code.ConstantCode;
import org.fisco.bcos.safekeeper.base.tools.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** config acccount and role. */
@Service
public class AccountDetailsService implements UserDetailsService {

    @Autowired private AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        // query account
        TbAccountInfo accountRow = null;
        try {
            accountRow = accountService.queryByAccount(account);
        } catch (Exception e) {
            throw new UsernameNotFoundException(
                    JacksonUtils.objToString(ConstantCode.DB_EXCEPTION));
        }
        if (null == accountRow) {
            throw new UsernameNotFoundException(
                    JacksonUtils.objToString(ConstantCode.ACCOUNT_NOT_EXISTS));
        }

        // add role
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        list.add(new SimpleGrantedAuthority("ROLE_" + accountRow.getRoleName()));

        User authUser = new User(account, accountRow.getAccountPwd(), list);
        return authUser;
    }
}
