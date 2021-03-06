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
package tech.pegasys.pantheon.consensus.clique.jsonrpc.methods;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static tech.pegasys.pantheon.ethereum.core.Address.fromHexString;

import tech.pegasys.pantheon.consensus.clique.CliqueBlockHeaderFunctions;
import tech.pegasys.pantheon.consensus.common.VoteTally;
import tech.pegasys.pantheon.consensus.common.VoteTallyCache;
import tech.pegasys.pantheon.ethereum.core.Address;
import tech.pegasys.pantheon.ethereum.core.BlockHeader;
import tech.pegasys.pantheon.ethereum.core.BlockHeaderTestFixture;
import tech.pegasys.pantheon.ethereum.core.Hash;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.JsonRpcRequest;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.exception.InvalidJsonRpcParameters;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.parameters.JsonRpcParameter;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.queries.BlockWithMetadata;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.queries.BlockchainQueries;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.queries.TransactionWithMetadata;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcError;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcErrorResponse;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcSuccessResponse;
import tech.pegasys.pantheon.util.bytes.BytesValue;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.AssertionsForClassTypes;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CliqueGetSignersAtHashTest {

  private CliqueGetSignersAtHash method;
  private BlockHeader blockHeader;
  private List<Address> validators;
  private List<String> validatorsAsStrings;

  @Mock private BlockchainQueries blockchainQueries;
  @Mock private VoteTallyCache voteTallyCache;
  @Mock private BlockWithMetadata<TransactionWithMetadata, Hash> blockWithMetadata;
  @Mock private VoteTally voteTally;

  public static final String BLOCK_HASH =
      "0xe36a3edf0d8664002a72ef7c5f8e271485e7ce5c66455a07cb679d855818415f";

  @Before
  public void setup() {
    method = new CliqueGetSignersAtHash(blockchainQueries, voteTallyCache, new JsonRpcParameter());

    final byte[] genesisBlockExtraData =
        Hex.decode(
            "52657370656374206d7920617574686f7269746168207e452e436172746d616e42eb768f2244c8811c63729a21a3569731535f067ffc57839b00206d1ad20c69a1981b489f772031b279182d99e65703f0076e4812653aab85fca0f00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");

    blockHeader =
        new BlockHeaderTestFixture()
            .blockHeaderFunctions(new CliqueBlockHeaderFunctions())
            .extraData(BytesValue.wrap(genesisBlockExtraData))
            .buildHeader();

    validators =
        asList(
            fromHexString("0x42eb768f2244c8811c63729a21a3569731535f06"),
            fromHexString("0x7ffc57839b00206d1ad20c69a1981b489f772031"),
            fromHexString("0xb279182d99e65703f0076e4812653aab85fca0f0"));
    validatorsAsStrings = validators.stream().map(Object::toString).collect(toList());
  }

  @Test
  public void returnsMethodName() {
    assertThat(method.getName()).isEqualTo("clique_getSignersAtHash");
  }

  @Test
  @SuppressWarnings("unchecked")
  public void failsWhenNoParam() {
    final JsonRpcRequest request =
        new JsonRpcRequest("2.0", "clique_getSignersAtHash", new Object[] {});

    final Throwable thrown = AssertionsForClassTypes.catchThrowable(() -> method.response(request));

    assertThat(thrown)
        .isInstanceOf(InvalidJsonRpcParameters.class)
        .hasMessage("Missing required json rpc parameter at index 0");
  }

  @Test
  @SuppressWarnings("unchecked")
  public void returnsValidatorsForBlockHash() {
    final JsonRpcRequest request =
        new JsonRpcRequest("2.0", "clique_getSignersAtHash", new Object[] {BLOCK_HASH});

    when(blockchainQueries.blockByHash(Hash.fromHexString(BLOCK_HASH)))
        .thenReturn(Optional.of(blockWithMetadata));
    when(blockWithMetadata.getHeader()).thenReturn(blockHeader);
    when(voteTallyCache.getVoteTallyAfterBlock(blockHeader)).thenReturn(voteTally);
    when(voteTally.getValidators()).thenReturn(validators);

    final JsonRpcSuccessResponse response = (JsonRpcSuccessResponse) method.response(request);
    assertEquals(validatorsAsStrings, response.getResult());
  }

  @Test
  public void failsOnInvalidBlockHash() {
    final JsonRpcRequest request =
        new JsonRpcRequest("2.0", "clique_getSigners", new Object[] {BLOCK_HASH});

    when(blockchainQueries.blockByHash(Hash.fromHexString(BLOCK_HASH)))
        .thenReturn(Optional.empty());

    final JsonRpcErrorResponse response = (JsonRpcErrorResponse) method.response(request);
    assertThat(response.getError().name()).isEqualTo(JsonRpcError.INTERNAL_ERROR.name());
  }
}
