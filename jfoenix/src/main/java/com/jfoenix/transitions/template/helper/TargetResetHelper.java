/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.jfoenix.transitions.template.helper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Helper class which takes a reset behaviour for a specific {@link javafx.animation.KeyValue}.
 *
 * @author Marcel Schlegel (schlegel11)
 * @version 1.0
 * @since 2018-12-12
 */
public class TargetResetHelper<KV> {

  private final Set<KV> resetTargets = new HashSet<>();
  private Consumer<Void> resetProcessConsumer = Void -> {};

  public void computeKeyValue(KV keyValue, Consumer<KV> resetProcessConsumer) {
    if (resetTargets.add(keyValue)) {
      this.resetProcessConsumer =
          this.resetProcessConsumer.andThen(Void -> resetProcessConsumer.accept(keyValue));
    }
  }

  public void computeKeyValues(Collection<KV> keyValues, Consumer<KV> resetProcessConsumer) {
    keyValues.forEach(keyValue -> computeKeyValue(keyValue, resetProcessConsumer));
  }

  public void reset() {
    resetProcessConsumer.accept(null);
  }
}
