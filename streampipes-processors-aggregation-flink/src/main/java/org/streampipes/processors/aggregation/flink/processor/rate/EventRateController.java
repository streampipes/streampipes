/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.processors.aggregation.flink.processor.rate;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.processors.aggregation.flink.config.AggregationFlinkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Locales;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.utils.Assets;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

public class EventRateController extends FlinkDataProcessorDeclarer<EventRateParameter> {

  private static final String RATE_KEY = "rate";

  @Override
  public DataProcessorDescription declareModel() {

    return ProcessingElementBuilder.create("org.streampipes.processors.aggregation.flink.rate")
            .category(DataProcessorType.AGGREGATE)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .withLocales(Locales.EN)
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredProperty(EpRequirements.anyProperty())
                    .build())
            .outputStrategy(OutputStrategies.fixed(EpProperties.doubleEp(Labels.empty(), "rate",
                    "http://schema.org/Number")))
            .requiredIntegerParameter(Labels.withId(RATE_KEY))
            .build();
  }

  @Override
  public FlinkDataProcessorRuntime<EventRateParameter> getRuntime(DataProcessorInvocation graph,
                                                                  ProcessingElementParameterExtractor extractor) {
    Integer avgRate = extractor.singleValueParameter(RATE_KEY, Integer.class);

    EventRateParameter staticParam = new EventRateParameter(graph, avgRate);

    return new EventRateProgram(staticParam, AggregationFlinkConfig.INSTANCE.getDebug());

  }
}