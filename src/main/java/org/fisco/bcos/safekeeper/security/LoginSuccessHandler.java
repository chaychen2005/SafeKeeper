/*
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
package org.fisco.bcos.safekeeper.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.fisco.bcos.safekeeper.account.AccountService;
import org.fisco.bcos.safekeeper.account.entity.TbAccountInfo;
import org.fisco.bcos.safekeeper.base.code.ConstantCode;
import org.fisco.bcos.safekeeper.base.entity.BaseResponse;
import org.fisco.bcos.safekeeper.base.properties.ConstantProperties;
import org.fisco.bcos.safekeeper.base.tools.JacksonUtils;
import org.fisco.bcos.safekeeper.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Log4j2
@Component("loginSuccessHandler")
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired private AccountService accountService;
    @Autowired private TokenService tokenService;
    @Autowired private ConstantProperties properties;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        log.debug("login success");

        String accountName = authentication.getName();
        String token;
        // delete old token and save new
        if (!properties.getWedpr()) {
            tokenService.deleteToken(null, accountName);
            token = tokenService.createToken(accountName, 1);
        } else {
            token = tokenService.getTokenFromAccount(accountName);
            if (token == null) {
                token = tokenService.createToken(accountName, 1);
            }
        }

        // response account info
        TbAccountInfo accountInfo = accountService.queryByAccount(accountName);
        Map<String, Object> rsp = new HashMap<>();
        rsp.put("roleName", accountInfo.getRoleName());
        rsp.put("account", accountName);
        rsp.put("accountStatus", accountInfo.getAccountStatus());
        rsp.put("token", token);

        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        baseResponse.setData(rsp);

        String backStr = JacksonUtils.objToString(baseResponse);
        log.debug("login backInfo:{}", backStr);

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(backStr);
    }
}
