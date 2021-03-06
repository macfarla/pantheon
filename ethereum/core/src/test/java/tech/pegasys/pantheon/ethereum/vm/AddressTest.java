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
package tech.pegasys.pantheon.ethereum.vm;

import tech.pegasys.pantheon.ethereum.core.Address;
import tech.pegasys.pantheon.util.bytes.BytesValue;

import org.junit.Assert;
import org.junit.Test;

public class AddressTest {

  @Test
  public void accountAddressToString() {
    final Address addr =
        Address.wrap(BytesValue.fromHexString("0x0000000000000000000000000000000000101010"));
    Assert.assertEquals("0x0000000000000000000000000000000000101010", addr.toString());
  }

  @Test
  public void accountAddressEquals() {
    final Address addr =
        Address.wrap(BytesValue.fromHexString("0x0000000000000000000000000000000000101010"));
    final Address addr2 =
        Address.wrap(BytesValue.fromHexString("0x0000000000000000000000000000000000101010"));

    Assert.assertEquals(addr, addr2);
  }

  @Test
  public void accountAddresHashCode() {
    final Address addr =
        Address.wrap(BytesValue.fromHexString("0x0000000000000000000000000000000000101010"));
    final Address addr2 =
        Address.wrap(BytesValue.fromHexString("0x0000000000000000000000000000000000101010"));

    Assert.assertEquals(addr.hashCode(), addr2.hashCode());
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidAccountAddress() {
    Address.wrap(BytesValue.fromHexString("0x00101010"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void tooShortAccountAddress() {
    Address.fromHexStringStrict("0x00101010");
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullAccountAddress() {
    Address.fromHexStringStrict(null);
  }
}
