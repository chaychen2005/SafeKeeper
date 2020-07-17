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
package org.fisco.bcos.safekeeper.security.customizeAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.fisco.bcos.safekeeper.account.AccountService;
import org.fisco.bcos.safekeeper.account.entity.TbAccountInfo;
import org.fisco.bcos.safekeeper.base.code.ConstantCode;
import org.fisco.bcos.safekeeper.base.exception.SafeKeeperException;
import org.fisco.bcos.safekeeper.base.tools.JacksonUtils;
import org.fisco.bcos.safekeeper.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Log4j2
public class TokenAuthenticationProvider implements AuthenticationProvider {

    @Autowired private TokenService tokenService;
    @Autowired private AccountService accountService;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String token = authentication.getName();
        String account = null;
        try {
            account = tokenService.getAccountFromToken(token);
            tokenService.updateExpireTime(token);
        } catch (SafeKeeperException e) {
            throw new CredentialsExpiredException(JacksonUtils.objToString(e.getRetCode()));
        } catch (Exception e) {
            throw new BadCredentialsException("db");
        }
        if (null == account) {
            throw new CredentialsExpiredException(
                    JacksonUtils.objToString(ConstantCode.ACCOUNT_NOT_EXISTS));
        }
        AbstractAuthenticationToken result = buildAuthentication(account);
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return TokenAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private Collection<SimpleGrantedAuthority> buildAuthorities(TbAccountInfo user) {
        final Collection<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
        List<String> authorities = getUserAuthorities(user);
        for (String authority : authorities) {
            simpleGrantedAuthorities.add(new SimpleGrantedAuthority(authority));
        }
        return simpleGrantedAuthorities;
    }

    private AbstractAuthenticationToken buildAuthentication(String account) {
        TbAccountInfo tbAccountInfo = accountService.queryByAccount(account);
        log.debug(tbAccountInfo + "****" + tbAccountInfo.getAccount());
        return new TokenAuthenticationToken(
                tbAccountInfo.getAccount(), buildAuthorities(tbAccountInfo));
    }

    private List<String> getUserAuthorities(TbAccountInfo user) {
        return Arrays.asList("ROLE_" + user.getRoleName());
    }
}
