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
package org.fisco.bcos.proxy.rpc;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.fisco.bcos.proxy.base.code.ConstantCode;
import org.fisco.bcos.proxy.base.controller.BaseController;
import org.fisco.bcos.proxy.base.entity.BaseResponse;
import org.fisco.bcos.proxy.base.exception.BcosNodeProxyException;
import org.fisco.bcos.proxy.base.tools.JacksonUtils;
import org.fisco.bcos.proxy.rpc.entity.JsonRpcRequest;
import org.fisco.bcos.proxy.rpc.entity.JsonRpcResponse;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.BcosSDKException;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.request.Transaction;
import org.fisco.bcos.sdk.client.protocol.response.BlockNumber;
import org.fisco.bcos.sdk.client.protocol.response.Call;
import org.fisco.bcos.sdk.model.ConstantConfig;
import org.fisco.bcos.sdk.model.NodeVersion;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping(value = "rpc")
public class RPCController extends BaseController {

    private Boolean initBcosSDK = false;
    private BcosSDK bcosSDK;
    private ConcurrentHashMap<Integer, Client> groupToClient = new ConcurrentHashMap<>();

    @PostMapping(value = "/v1")
    public BaseResponse sendRPC(@RequestBody @Valid JsonRpcRequest info, BindingResult result)
            throws BcosNodeProxyException {
        checkBindResult(result);
        BaseResponse baseResponse;
        Instant startTime = Instant.now();
        log.info(
                "start sendRPC. startTime:{} rpc request info:{}",
                startTime.toEpochMilli(),
                JacksonUtils.objToString(info));

        if (initBcosSDK == false) {
            initBcosSDK();
            initBcosSDK = true;
        }

        List<Object> params = info.getParams();
        if (params.size() < 1) {
            log.error("the size of `JsonRpcRequest.params` should be larger than 1");
            throw new BcosNodeProxyException(ConstantCode.PARAM_EXCEPTION);
        }
        Integer groupId = (Integer) params.get(0);
        Client client = getClientByGroupId(groupId);
        String method = info.getMethod();

        if (method.equals("getClientVersion")) {
            baseResponse = getClientVersion(info, client);
        } else if (method.equals("getBlockNumber")) {
            baseResponse = getBlockNumber(info, client);
        } else if (method.equals("sendRawTransaction")) {
            baseResponse = sendRawTransaction(info, client);
        } else if (method.equals("call")) {
            baseResponse = call(info, client);
        } else {
            log.error("invalid method");
            throw new BcosNodeProxyException(ConstantCode.INVALID_RPC_METHOD);
        }

        log.info(
                "end sendRPC. useTime:{} result:{}",
                Duration.between(startTime, Instant.now()).toMillis(),
                JacksonUtils.objToString(baseResponse));
        return baseResponse;
    }

    private void initBcosSDK() {
        log.info("init bcos sdk");
        final String configFile =
                RPCController.class
                        .getClassLoader()
                        .getResource(ConstantConfig.CONFIG_FILE_NAME)
                        .getPath();
        bcosSDK = BcosSDK.build(configFile);
    }

    private Client getClientByGroupId(Integer groupId) {
        if (!groupToClient.containsKey(groupId)) {
            try {
                Client client = bcosSDK.getClient(groupId);
                groupToClient.put(groupId, client);
            } catch (BcosSDKException e) {
                log.error("invalid groupId, id: " + groupId);
                throw new BcosNodeProxyException(ConstantCode.INVALID_GROUPID);
            }
        }
        return groupToClient.get(groupId);
    }

    private BaseResponse getClientVersion(JsonRpcRequest info, Client client) {
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        NodeVersion nodeVersion = client.getNodeVersion();
        nodeVersion.setId(info.getId());
        nodeVersion.setJsonrpc(info.getJsonrpc());
        baseResponse.setData(nodeVersion);
        return baseResponse;
    }

    private BaseResponse getBlockNumber(JsonRpcRequest info, Client client) {
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        BlockNumber blockNumber = client.getBlockNumber();
        blockNumber.setId(info.getId());
        blockNumber.setJsonrpc(info.getJsonrpc());
        baseResponse.setData(blockNumber.getResult());
        return baseResponse;
    }

    private BaseResponse sendRawTransaction(JsonRpcRequest info, Client client) {
        List<Object> params = info.getParams();
        if (params.size() != 2) {
            log.error("the size of `JsonRpcRequest.params` should be 2");
            throw new BcosNodeProxyException(ConstantCode.PARAM_EXCEPTION);
        }
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        String data = (String) params.get(1);
        TransactionReceipt receipt = client.sendRawTransactionAndGetReceipt(data);
        JsonRpcResponse jsonRpcResponse = new JsonRpcResponse();
        jsonRpcResponse.setId(info.getId());
        jsonRpcResponse.setJsonrpc(info.getJsonrpc());
        jsonRpcResponse.setResult(receipt);
        baseResponse.setData(jsonRpcResponse);
        return baseResponse;
    }

    private BaseResponse call(JsonRpcRequest info, Client client) {
        List<Object> params = info.getParams();
        if (params.size() != 2) {
            log.error("the size of `JsonRpcRequest.params` should be 2");
            throw new BcosNodeProxyException(ConstantCode.PARAM_EXCEPTION);
        }
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        String data = (String) params.get(1);
        try {
            Transaction transaction =
                    ObjectMapperFactory.getObjectMapper().readValue(data, Transaction.class);
            Call callFuncRet = client.call(transaction);
            callFuncRet.setId(info.getId());
            callFuncRet.setJsonrpc(info.getJsonrpc());
            baseResponse.setData(callFuncRet);
        } catch (IOException e) {
            log.error("inside json parser error");
            throw new BcosNodeProxyException(ConstantCode.INSIDE_JSON_PARSER_ERROR);
        }
        return baseResponse;
    }
}
