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

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.commons.exceptions.connect.ParseException;
import org.apache.streampipes.connect.iiot.adapters.IPullAdapter;
import org.apache.streampipes.connect.iiot.adapters.PullAdapterScheduler;
import org.apache.streampipes.extensions.management.connect.AdapterInterface;
import org.apache.streampipes.extensions.management.connect.adapter.parser.Parsers;
import org.apache.streampipes.extensions.management.connect.adapter.util.PollingSettings;
import org.apache.streampipes.extensions.management.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.management.context.IAdapterRuntimeContext;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.adapter.AdapterConfiguration;
import org.apache.streampipes.model.connect.adapter.IEventCollector;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;

import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HttpStreamProtocol implements AdapterInterface, IPullAdapter {

  private static final Logger logger = LoggerFactory.getLogger(HttpStreamProtocol.class);

  public static final String ID = "org.apache.streampipes.connect.iiot.protocol.stream.http";

  private static final String URL_PROPERTY = "url";
  private static final String INTERVAL_PROPERTY = "interval";

  private String url;
  private String accessToken;

  private PollingSettings pollingSettings;
  private PullAdapterScheduler pullAdapterScheduler;

  private BrokerEventProcessor processor;

  public HttpStreamProtocol() {
  }

  private void applyConfiguration(StaticPropertyExtractor extractor) {
    this.url = extractor.singleValueParameter(URL_PROPERTY, String.class);
    int interval = extractor.singleValueParameter(INTERVAL_PROPERTY, Integer.class);
    this.pollingSettings = PollingSettings.from(TimeUnit.SECONDS, interval);
    // TODO change access token to an optional parameter
//            String accessToken = extractor.singleValue(ACCESS_TOKEN_PROPERTY);
    this.accessToken = "";
  }

  private byte[] getDataFromEndpoint() throws ParseException {
    byte[] result = null;

    try {
      Request request = Request.Get(url)
          .connectTimeout(1000)
          .socketTimeout(100000);

      if (this.accessToken != null && !this.accessToken.equals("")) {
        request.setHeader("Authorization", "Bearer " + this.accessToken);
      }

      result = request
          .execute().returnContent().asBytes();

    } catch (Exception e) {
      logger.error("Error while fetching data from URL: " + url, e);
      throw new ParseException("Error while fetching data from URL: " + url);
    }
    if (result == null) {
      throw new ParseException("Could not receive Data from file: " + url);
    }

    return result;
  }

  @Override
  public AdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder
        .create(ID)
        .withSupportedParsers(Parsers.defaultParsers())
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .withCategory(AdapterType.Generic)
        .requiredTextParameter(Labels.withId(URL_PROPERTY))
        .requiredIntegerParameter(Labels.withId(INTERVAL_PROPERTY))
        .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    this.applyConfiguration(extractor.getStaticPropertyExtractor());
    this.processor = new BrokerEventProcessor(extractor.selectedParser(), (event) -> {
      collector.collect(event);
    });
    this.pullAdapterScheduler = new PullAdapterScheduler();
    this.pullAdapterScheduler.schedule(this, extractor.getAdapterDescription().getElementId());
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    this.pullAdapterScheduler.shutdown();
  }

  @Override
  public GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
                                       IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    this.applyConfiguration(extractor.getStaticPropertyExtractor());
    byte[] dataInputStream = getDataFromEndpoint();

    return extractor.selectedParser().getGuessSchema(new ByteArrayInputStream(dataInputStream));
  }

  @Override
  public void pullData() throws ExecutionException, RuntimeException, InterruptedException, TimeoutException {
    var result = getDataFromEndpoint();
    this.processor.onEvent(result);
  }

  @Override
  public PollingSettings getPollingInterval() {
    return pollingSettings;
  }
}
