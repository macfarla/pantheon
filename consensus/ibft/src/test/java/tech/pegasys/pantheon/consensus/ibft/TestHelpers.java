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
package tech.pegasys.pantheon.consensus.ibft;

import static java.util.Collections.singletonList;

import tech.pegasys.pantheon.consensus.ibft.messagewrappers.Commit;
import tech.pegasys.pantheon.consensus.ibft.messagewrappers.Prepare;
import tech.pegasys.pantheon.consensus.ibft.messagewrappers.Proposal;
import tech.pegasys.pantheon.consensus.ibft.messagewrappers.RoundChange;
import tech.pegasys.pantheon.consensus.ibft.payload.MessageFactory;
import tech.pegasys.pantheon.crypto.SECP256K1.KeyPair;
import tech.pegasys.pantheon.crypto.SECP256K1.Signature;
import tech.pegasys.pantheon.ethereum.core.Address;
import tech.pegasys.pantheon.ethereum.core.AddressHelpers;
import tech.pegasys.pantheon.ethereum.core.Block;
import tech.pegasys.pantheon.ethereum.core.BlockDataGenerator;
import tech.pegasys.pantheon.ethereum.core.BlockDataGenerator.BlockOptions;
import tech.pegasys.pantheon.ethereum.core.Hash;
import tech.pegasys.pantheon.util.bytes.BytesValue;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TestHelpers {

  public static ConsensusRoundIdentifier createFrom(
      final ConsensusRoundIdentifier initial, final int offSetSequence, final int offSetRound) {
    return new ConsensusRoundIdentifier(
        initial.getSequenceNumber() + offSetSequence, initial.getRoundNumber() + offSetRound);
  }

  public static Block createProposalBlock(
      final List<Address> validators, final ConsensusRoundIdentifier roundId) {
    final BytesValue extraData =
        new IbftExtraData(
                BytesValue.wrap(new byte[32]),
                Collections.emptyList(),
                Optional.empty(),
                roundId.getRoundNumber(),
                validators)
            .encode();
    final BlockOptions blockOptions =
        BlockOptions.create()
            .setExtraData(extraData)
            .setBlockNumber(roundId.getSequenceNumber())
            .setBlockHeaderFunctions(IbftBlockHeaderFunctions.forCommittedSeal());
    return new BlockDataGenerator().block(blockOptions);
  }

  public static Proposal createSignedProposalPayload(final KeyPair signerKeys) {
    return createSignedProposalPayloadWithRound(signerKeys, 0xFEDCBA98);
  }

  public static Proposal createSignedProposalPayloadWithRound(
      final KeyPair signerKeys, final int round) {
    final MessageFactory messageFactory = new MessageFactory(signerKeys);
    final ConsensusRoundIdentifier roundIdentifier =
        new ConsensusRoundIdentifier(0x1234567890ABCDEFL, round);
    final Block block =
        TestHelpers.createProposalBlock(singletonList(AddressHelpers.ofValue(1)), roundIdentifier);
    return messageFactory.createProposal(roundIdentifier, block, Optional.empty());
  }

  public static Prepare createSignedPreparePayload(final KeyPair signerKeys) {
    final MessageFactory messageFactory = new MessageFactory(signerKeys);
    final ConsensusRoundIdentifier roundIdentifier =
        new ConsensusRoundIdentifier(0x1234567890ABCDEFL, 0xFEDCBA98);
    return messageFactory.createPrepare(roundIdentifier, Hash.fromHexStringLenient("0"));
  }

  public static Commit createSignedCommitPayload(final KeyPair signerKeys) {
    final MessageFactory messageFactory = new MessageFactory(signerKeys);
    final ConsensusRoundIdentifier roundIdentifier =
        new ConsensusRoundIdentifier(0x1234567890ABCDEFL, 0xFEDCBA98);
    return messageFactory.createCommit(
        roundIdentifier,
        Hash.fromHexStringLenient("0"),
        Signature.create(BigInteger.ONE, BigInteger.TEN, (byte) 0));
  }

  public static RoundChange createSignedRoundChangePayload(final KeyPair signerKeys) {
    final MessageFactory messageFactory = new MessageFactory(signerKeys);
    final ConsensusRoundIdentifier roundIdentifier =
        new ConsensusRoundIdentifier(0x1234567890ABCDEFL, 0xFEDCBA98);
    return messageFactory.createRoundChange(roundIdentifier, Optional.empty());
  }
}
