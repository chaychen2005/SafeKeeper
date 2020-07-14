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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.fisco.bcos.safekeeper.base.code.ConstantCode;
import org.fisco.bcos.safekeeper.base.exception.SafeKeeperException;
import org.fisco.bcos.safekeeper.base.tools.JacksonUtils;
import org.fisco.bcos.safekeeper.base.tools.SafeKeeperTools;
import org.fisco.bcos.safekeeper.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class JsonLogoutSuccessHandler implements LogoutSuccessHandler {
    @Autowired private TokenService tokenService;

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {

        // get token
        String token;
        try {
            token = SafeKeeperTools.getToken(request);
        } catch (SafeKeeperException ex) {
            SafeKeeperTools.responseString(response, JacksonUtils.objToString(ex.getRetCode()));
            return;
        }
        // remove token
        tokenService.deleteToken(token, null);

        log.debug("logout success");
        SafeKeeperTools.responseString(response, JacksonUtils.objToString(ConstantCode.SUCCESS));
    }
}
