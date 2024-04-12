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

package org.apache.streampipes.processors.filters.jvm.processor.numericalfilter;

import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.manager.template.DataProcessorTemplateHandler;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.model.template.PipelineElementTemplate;
import org.apache.streampipes.model.template.PipelineElementTemplateConfig;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.test.generator.EventStreamGenerator;

import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProcessingElementTestExecutor {

  /**
   * This method is used to run a data processor with a given configuration and a list of input events.
   * It then verifies the output events against the expected output events.
   *
   * @param processor            The data processor under test.
   * @param userConfiguration    The user input configuration for the processor.
   * @param inputEvents          The list of input events to be processed.
   * @param expectedOutputEvents The list of expected output events.
   * @param expectedException    Exception expected to occur.
   */
  public static void runWithException(
      IStreamPipesDataProcessor processor,
      Map<String, Object> userConfiguration,
      List<Map<String, Object>> inputEvents,
      List<Map<String, Object>> expectedOutputEvents,
      Consumer<DataProcessorInvocation> invocationConfig,
      Exception expectedException
  ) {
    Exception exception = assertThrows(expectedException.getClass(), () -> {
      run(processor, userConfiguration, inputEvents, expectedOutputEvents, invocationConfig);
    });

    String expectedMessage = expectedException.getMessage();
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));

  }

  /**
   * This method is used to run a data processor with a given configuration and a list of input events.
   * It then verifies the output events against the expected output events.
   *
   * @param processor            The data processor under test.
   * @param userConfiguration    The user input configuration for the processor.
   * @param inputEvents          The list of input events to be processed.
   * @param expectedOutputEvents The list of expected output events.
   */
  public static void run(
      IStreamPipesDataProcessor processor,
      Map<String, Object> userConfiguration,
      List<Map<String, Object>> inputEvents,
      List<Map<String, Object>> expectedOutputEvents,
      Consumer<DataProcessorInvocation> invocationConfig
  ) {


    // initialize the extractor with the provided configuration of the user input
    var dataProcessorInvocation = getProcessorInvocation(processor, userConfiguration);
    if (!(invocationConfig == null)){
      invocationConfig.accept(dataProcessorInvocation);
    }

    var e = getProcessingElementParameterExtractor(dataProcessorInvocation);
    var mockParams = mock(IDataProcessorParameters.class);

    when(mockParams.getModel()).thenReturn(dataProcessorInvocation);
    when(mockParams.extractor()).thenReturn(e);

    // calls the onPipelineStarted method of the processor to initialize it
    processor.onPipelineStarted(mockParams, null, null);

    // mock the output collector to capture the output events and validate the results later
    var mockCollector = mock(SpOutputCollector.class);
    var spOutputCollectorCaptor = ArgumentCaptor.forClass(Event.class);


    // Iterate over all input events and call the onEvent method of the processor
    for (Map<String, Object> inputRawEvent : inputEvents) {
      processor.onEvent(getEvent(inputRawEvent), mockCollector);
    }

    // Validate the output of the processor
    verify(mockCollector, times(expectedOutputEvents.size())).collect(spOutputCollectorCaptor.capture());
    var resultingEvents = spOutputCollectorCaptor.getAllValues();
    IntStream.range(0, expectedOutputEvents.size())
             .forEach(i -> assertEquals(
                 expectedOutputEvents.get(i),
                 resultingEvents.get(i)
                                .getRaw()
             ));

    // validate that the processor is stopped correctly
    processor.onPipelineStopped();
  }

  private static ProcessingElementParameterExtractor getProcessingElementParameterExtractor(
      DataProcessorInvocation dataProcessorInvocation
  ) {
    return ProcessingElementParameterExtractor.from(dataProcessorInvocation);
  }

  private static DataProcessorInvocation getProcessorInvocation(
      IStreamPipesDataProcessor processor,
      Map<String, Object> userConfiguration
  ) {
    var pipelineElementTemplate = getPipelineElementTemplate(processor, userConfiguration);

    var invocation = new DataProcessorInvocation(
        processor
            .declareConfig()
            .getDescription()
    );

    invocation.setOutputStream(EventStreamGenerator.makeEmptyStream());


    return new DataProcessorTemplateHandler(
        pipelineElementTemplate,
        invocation,
        true
    )
        .applyTemplateOnPipelineElement();
  }

  private static PipelineElementTemplate getPipelineElementTemplate(
      IStreamPipesDataProcessor processor,
      Map<String, Object> userConfiguration
  ) {
    var staticProperties = processor
        .declareConfig()
        .getDescription()
        .getStaticProperties();


    var configs = new HashMap<String, PipelineElementTemplateConfig>();

    staticProperties.forEach(staticProperty -> {
      var value = userConfiguration.get(staticProperty.getInternalName());
      configs.put(
          staticProperty.getInternalName(),
          new PipelineElementTemplateConfig(true, true, value)
      );
    });

    return new PipelineElementTemplate("name", "description", configs);
  }

  private static Event getEvent(Map<String, Object> rawEvent) {

    // separate the prefix and remove it from the map
    Map<String, Object> eventMap = new HashMap<>(rawEvent);
    String selectorPrefix = eventMap.keySet().stream()
        .filter(s->s.contains("::"))
        .map(s->s.split("::")[0])
        .findFirst().orElse("");

    for (var key : eventMap.keySet()){
      if (key.contains("::")){
        var value = rawEvent.get(key);
        var newKey = key.split("::")[1];
        eventMap.remove(key);
        eventMap.put(newKey, value);
      }
    }
    var sourceInfo = new SourceInfo("", selectorPrefix);
    var schemaInfo = new SchemaInfo(null, new ArrayList<>());

    return EventFactory.fromMap(eventMap, sourceInfo, schemaInfo);
  }
}
