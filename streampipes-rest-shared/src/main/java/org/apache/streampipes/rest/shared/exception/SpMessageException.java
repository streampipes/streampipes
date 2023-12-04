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

package org.apache.streampipes.rest.shared.exception;

import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.Notifications;

import org.springframework.http.HttpStatus;

public class SpMessageException extends RuntimeException {

  private final Message message;
  private final HttpStatus status;

  public SpMessageException(HttpStatus status,
                            Message message) {
    this.status = status;
    this.message = message;
  }

  public SpMessageException(HttpStatus status,
                            Throwable throwable) {
    this.status = status;
    this.message = Notifications.error(throwable.getMessage());
  }

  public Message getSpMessage() {
    return message;
  }

  public HttpStatus getStatus() {
    return status;
  }
}
