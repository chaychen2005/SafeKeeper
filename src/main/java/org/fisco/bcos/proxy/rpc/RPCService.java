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
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.fisco.bcos.proxy.base.code.ConstantCode;
import org.fisco.bcos.proxy.base.entity.BaseResponse;
import org.fisco.bcos.proxy.base.exception.BcosNodeProxyException;
import org.fisco.bcos.proxy.rpc.entity.JsonRpcRequest;
import org.fisco.bcos.proxy.rpc.entity.JsonRpcResponse;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.request.Transaction;
import org.fisco.bcos.sdk.client.protocol.response.BlockNumber;
import org.fisco.bcos.sdk.client.protocol.response.Call;
import org.fisco.bcos.sdk.model.NodeVersion;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.utils.ObjectMapperFactory;
import org.springframework.stereotype.Service;

/** services for account data. */
@Log4j2
@Service
public class RPCService {

    /** getClientVersion. */
    public BaseResponse getClientVersion(JsonRpcRequest info, Client client)
            throws BcosNodeProxyException {
        log.info("start getClientVersion");
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        NodeVersion nodeVersion = client.getNodeVersion();
        nodeVersion.setId(info.getId());
        nodeVersion.setJsonrpc(info.getJsonrpc());
        baseResponse.setData(nodeVersion);
        return baseResponse;
    }

    /** getBlockNumber. */
    public BaseResponse getBlockNumber(JsonRpcRequest info, Client client)
            throws BcosNodeProxyException {
        log.info("start getClientVersion");
        BaseResponse baseResponse = new BaseResponse(ConstantCode.SUCCESS);
        BlockNumber blockNumber = client.getBlockNumber();
        blockNumber.setId(info.getId());
        blockNumber.setJsonrpc(info.getJsonrpc());
        baseResponse.setData(blockNumber.getResult());
        return baseResponse;
    }

    /** sendRawTransaction. */
    public BaseResponse sendRawTransaction(JsonRpcRequest info, Client client)
            throws BcosNodeProxyException {
        log.info("start getClientVersion");
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

    /** call. */
    public BaseResponse call(JsonRpcRequest info, Client client) throws BcosNodeProxyException {
        log.info("start getClientVersion");
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
