/*
 * Copyright 2018 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package tech.pegasys.pantheon.ethereum.jsonrpc.internal.methods;

import tech.pegasys.pantheon.ethereum.jsonrpc.RpcMethod;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.JsonRpcRequest;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.filter.FilterManager;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.parameters.JsonRpcParameter;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.queries.LogWithMetadata;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcError;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcErrorResponse;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcResponse;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcSuccessResponse;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.results.LogsResult;

import java.util.List;

public class EthGetFilterLogs implements JsonRpcMethod {

  private final FilterManager filterManager;
  private final JsonRpcParameter parameters;

  public EthGetFilterLogs(final FilterManager filterManager, final JsonRpcParameter parameters) {
    this.filterManager = filterManager;
    this.parameters = parameters;
  }

  @Override
  public String getName() {
    return RpcMethod.ETH_GET_FILTER_LOGS.getMethodName();
  }

  @Override
  public JsonRpcResponse response(final JsonRpcRequest request) {
    final String filterId = parameters.required(request.getParams(), 0, String.class);

    final List<LogWithMetadata> logs = filterManager.logs(filterId);
    if (logs != null) {
      return new JsonRpcSuccessResponse(request.getId(), new LogsResult(logs));
    }

    return new JsonRpcErrorResponse(request.getId(), JsonRpcError.LOGS_FILTER_NOT_FOUND);
  }
}
