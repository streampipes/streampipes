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

package org.apache.streampipes.connect.iiot.protocol.stream;

import org.apache.streampipes.commons.exceptions.connect.ParseException;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.connect.adapter.IEventCollector;
import org.apache.streampipes.model.connect.adapter.Parser;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record BrokerEventProcessor(Parser parser,
                                   IEventCollector collector) implements InternalEventProcessor<byte[]> {

  private static final Logger LOG = LoggerFactory.getLogger(BrokerEventProcessor.class);

  @Override
  public void onEvent(byte[] payload) {
    try {
      parser.parse(IOUtils.toInputStream(new String(payload), "UTF-8"), collector);
    } catch (ParseException e) {
      LOG.error("Error while parsing: " + e.getMessage());
    }
  }
}
