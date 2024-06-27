/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.streampipes.model.client.user;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PasswordRecoveryToken extends AbstractMailToken {

  // This field should be called $type since this is the identifier used in the CouchDB view
  @SuppressWarnings("checkstyle:MemberName")
  @JsonIgnore
  private String $type = "password-recovery";

  public PasswordRecoveryToken() {

  }

  public static PasswordRecoveryToken create(String token, String username) {
    PasswordRecoveryToken passwordRecoveryToken = new PasswordRecoveryToken();
    passwordRecoveryToken.setToken(token);
    passwordRecoveryToken.setUsername(username);

    return passwordRecoveryToken;
  }

  @SuppressWarnings("checkstyle:MethodName")
  public String get$type() {
    return $type;
  }

  @SuppressWarnings({"checkstyle:MethodName", "checkstyle:ParameterName"})
  public void set$type(String $type) {
    this.$type = $type;
  }
}
