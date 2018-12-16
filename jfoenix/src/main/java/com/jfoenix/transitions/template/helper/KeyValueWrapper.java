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

import javafx.beans.value.WritableValue;

/**
 * Helper wrapper class which takes a {@link javafx.animation.KeyValue} and the target {@link
 * WritableValue}. <br>
 * Provides equals method which is based on the target {@link WritableValue}.
 *
 * @author Marcel Schlegel (schlegel11)
 * @version 1.0
 * @since 2018-12-12
 */
public class KeyValueWrapper<KV> {

  private final KV keyValue;
  private final WritableValue<Object> writableValue;

  public KeyValueWrapper(KV keyValue, WritableValue<Object> writableValue) {
    this.keyValue = keyValue;
    this.writableValue = writableValue;
  }

  public KV getKeyValue() {
    return keyValue;
  }

  public WritableValue<Object> getWritableValue() {
    return writableValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    KeyValueWrapper<?> that = (KeyValueWrapper<?>) o;
    return writableValue == that.writableValue;
  }

  @Override
  public int hashCode() {
    return writableValue.hashCode();
  }
}
