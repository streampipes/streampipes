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

package org.apache.streampipes.processors.filters.jvm.processor.sdt;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.processors.filters.jvm.processor.numericalfilter.ProcessingElementTestExecutor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TestSwingingDoorTrendingFilterProcessor {

  private final String sdtTimestampField = "sdtTimestampField";
  private final String sdtValueField = "sdtValueField";

  private SwingingDoorTrendingFilterProcessor processor;

  @BeforeEach
  public void setup() {
    processor = new SwingingDoorTrendingFilterProcessor();
  }

  @Test
  public void test(){
    Map<String, Object> userConfiguration = Map.of(
        SwingingDoorTrendingFilterProcessor.SDT_TIMESTAMP_FIELD_KEY, "::" + sdtTimestampField,
        SwingingDoorTrendingFilterProcessor.SDT_VALUE_FIELD_KEY, "::" + sdtValueField,
        SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_DEVIATION_KEY, "10.0",
        SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_MIN_INTERVAL_KEY, "100",
        SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_MAX_INTERVAL_KEY, "500"
    );

    List<Map<String, Object>> inputEvents = List.of(
        Map.of(sdtTimestampField, 0,
            sdtValueField, 50.0),
        Map.of(sdtTimestampField, 50,
            sdtValueField, 50.0),
        Map.of(sdtTimestampField, 200,
            sdtValueField, 100.0),
        Map.of(sdtTimestampField, 270,
            sdtValueField, 140.0),
        Map.of(sdtTimestampField, 300,
            sdtValueField, 250.0),
        Map.of(sdtTimestampField, 900,
            sdtValueField, 500.0),
        Map.of(sdtTimestampField, 1100,
            sdtValueField, 800.0),
        Map.of(sdtTimestampField, 1250,
            sdtValueField, 1600.0)
    );

    List<Map<String, Object>> outputEvents = List.of(
            Map.of(sdtTimestampField, 0,
                sdtValueField, 50.0),
            Map.of(sdtTimestampField, 270,
                sdtValueField, 140.0),
            Map.of(sdtTimestampField, 900,
                sdtValueField, 500.0),
            Map.of(sdtTimestampField, 1100,
                sdtValueField, 800.0)
    );

    ProcessingElementTestExecutor.run(processor, userConfiguration, inputEvents, outputEvents, null);
  }

  @Test
  public void testInvalidDeviationKey(){
    Map<String, Object> userConfiguration = Map.of(
        SwingingDoorTrendingFilterProcessor.SDT_TIMESTAMP_FIELD_KEY, "::" + sdtTimestampField,
        SwingingDoorTrendingFilterProcessor.SDT_VALUE_FIELD_KEY, "::" + sdtValueField,
        SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_DEVIATION_KEY, "-10.0",
        SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_MIN_INTERVAL_KEY, "100",
        SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_MAX_INTERVAL_KEY, "500"
    );

    List<Map<String, Object>> inputEvents = new ArrayList<>();

    List<Map<String, Object>> outputEvents = new ArrayList<>();

    Exception exception = new SpRuntimeException("Compression Deviation should be positive!");

    ProcessingElementTestExecutor
        .runWithException(processor, userConfiguration, inputEvents, outputEvents, null, exception);
  }

  @Test
  public void testNegativeMinimumTime(){
    Map<String, Object> userConfiguration = Map.of(
        SwingingDoorTrendingFilterProcessor.SDT_TIMESTAMP_FIELD_KEY, "::" + sdtTimestampField,
        SwingingDoorTrendingFilterProcessor.SDT_VALUE_FIELD_KEY, "::" + sdtValueField,
        SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_DEVIATION_KEY, "10.0",
        SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_MIN_INTERVAL_KEY, "-100",
        SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_MAX_INTERVAL_KEY, "500"
    );

    List<Map<String, Object>> inputEvents = new ArrayList<>();

    List<Map<String, Object>> outputEvents = new ArrayList<>();

    Exception exception = new SpRuntimeException("Compression Minimum Time Interval should be >= 0!");

    ProcessingElementTestExecutor
        .runWithException(processor, userConfiguration, inputEvents, outputEvents, null, exception);
  }

  @Test
  public void testInvalidTimeInterval(){
    Map<String, Object> userConfiguration = Map.of(
        SwingingDoorTrendingFilterProcessor.SDT_TIMESTAMP_FIELD_KEY, "::" + sdtTimestampField,
        SwingingDoorTrendingFilterProcessor.SDT_VALUE_FIELD_KEY, "::" + sdtValueField,
        SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_DEVIATION_KEY, "10.0",
        SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_MIN_INTERVAL_KEY, "1000",
        SwingingDoorTrendingFilterProcessor.SDT_COMPRESSION_MAX_INTERVAL_KEY, "500"
    );

    List<Map<String, Object>> inputEvents = new ArrayList<>();

    List<Map<String, Object>> outputEvents = new ArrayList<>();

    Exception exception = new
        SpRuntimeException("Compression Minimum Time Interval should be < Compression Maximum Time Interval!");

    ProcessingElementTestExecutor
        .runWithException(processor, userConfiguration, inputEvents, outputEvents, null, exception);
  }
}