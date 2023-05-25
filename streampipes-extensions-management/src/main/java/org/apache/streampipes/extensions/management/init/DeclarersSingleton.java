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

package org.apache.streampipes.extensions.management.init;

import org.apache.streampipes.dataformat.SpDataFormatFactory;
import org.apache.streampipes.dataformat.SpDataFormatManager;
import org.apache.streampipes.extensions.api.connect.AdapterInterface;
import org.apache.streampipes.extensions.api.declarer.DataStreamDeclarer;
import org.apache.streampipes.extensions.api.declarer.Declarer;
import org.apache.streampipes.extensions.api.declarer.IStreamPipesFunctionDeclarer;
import org.apache.streampipes.extensions.api.declarer.PipelineTemplateDeclarer;
import org.apache.streampipes.extensions.api.declarer.SemanticEventConsumerDeclarer;
import org.apache.streampipes.extensions.api.declarer.SemanticEventProcessingAgentDeclarer;
import org.apache.streampipes.extensions.management.model.SpServiceDefinition;
import org.apache.streampipes.messaging.SpProtocolDefinitionFactory;
import org.apache.streampipes.messaging.SpProtocolManager;
import org.apache.streampipes.model.grounding.TransportFormat;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.SpConfig;
import org.apache.streampipes.svcdiscovery.api.model.ConfigItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DeclarersSingleton implements IDeclarersSingleton {

  private static final Logger LOG = LoggerFactory.getLogger(DeclarersSingleton.class);
  private static final String Http = "http://";
  private static final String Colon = ":";
  private static final String Slash = "/";
  private static DeclarersSingleton instance;
  private SpServiceDefinition serviceDefinition;

  private Map<String, SemanticEventProcessingAgentDeclarer> epaDeclarers;
  private Map<String, SemanticEventConsumerDeclarer> consumerDeclarers;
  private Map<String, PipelineTemplateDeclarer> pipelineTemplateDeclarers;
  private Map<String, DataStreamDeclarer> streamDeclarers;
  private Map<String, IStreamPipesFunctionDeclarer> functions;

  private Map<String, TransportProtocol> supportedProtocols;
  private Map<String, TransportFormat> supportedFormats;

  private Map<String, AdapterInterface> adapters;

  private String serviceId;

  private int port;
  private String route;
  private String hostName;


  private DeclarersSingleton() {
    this.epaDeclarers = new HashMap<>();
    this.consumerDeclarers = new HashMap<>();
    this.streamDeclarers = new HashMap<>();
    this.pipelineTemplateDeclarers = new HashMap<>();
    this.supportedProtocols = new HashMap<>();
    this.supportedFormats = new HashMap<>();
    this.adapters = new HashMap<>();
    this.functions = new HashMap<>();
    this.route = "/";
  }

  public static DeclarersSingleton getInstance() {
    if (DeclarersSingleton.instance == null) {
      DeclarersSingleton.instance = new DeclarersSingleton();
    }
    return DeclarersSingleton.instance;
  }


  public void populate(String host, Integer port, SpServiceDefinition serviceDef) {
    this.serviceDefinition = serviceDef;
    this.registerConfigs(serviceDef.getServiceGroup(), serviceDef.getServiceName(), serviceDef.getKvConfigs());
    this.setHostName(host);
    this.setPort(port);
    this.addDeclarers(serviceDef.getDeclarers());
    this.serviceId = serviceDef.getServiceId();
    this.registerProtocols(serviceDef.getProtocolDefinitionFactories());
    this.registerDataFormats(serviceDef.getDataFormatFactories());
    serviceDef.getAdapters().forEach(a -> this.adapters.put(a.declareConfig().getAdapterDescription().getAppId(), a));
    serviceDef.getFunctions().forEach(f -> this.functions.put(f.getFunctionConfig().getFunctionId().getId(), f));
  }

  private void registerConfigs(String serviceGroup,
                               String serviceName,
                               Map<String, ConfigItem> configs) {
    LOG.info("Registering {} configs in key/value store", configs.size());
    SpConfig spConfig = SpServiceDiscovery.getSpConfig(serviceGroup);
    configs.values().forEach(spConfig::register);
    spConfig.register(ConfigItem.from("SP_SERVICE_NAME", serviceName, ""));
  }

  public void addDeclarers(List<Declarer<?>> allDeclarers) {
    allDeclarers.forEach(this::add);
  }

  @Deprecated
  /**
   * @Deprecated Use ServiceDefinitionBuilder instead
   */
  public DeclarersSingleton add(Declarer<?> d) {
    if (d instanceof SemanticEventProcessingAgentDeclarer) {
      addEpaDeclarer((SemanticEventProcessingAgentDeclarer) d);
    } else if (d instanceof DataStreamDeclarer) {
      addStreamDeclarer((DataStreamDeclarer) d);
    } else if (d instanceof SemanticEventConsumerDeclarer) {
      addConsumerDeclarer((SemanticEventConsumerDeclarer) d);
    } else if (d instanceof PipelineTemplateDeclarer) {
      addPipelineTemplateDeclarer((PipelineTemplateDeclarer) d);
    }

    return getInstance();
  }

  public Map<String, Declarer<?>> getDeclarers() {
    Map<String, Declarer<?>> result = new HashMap<>();
    result.putAll(epaDeclarers);
    result.putAll(streamDeclarers);
    result.putAll(consumerDeclarers);
    result.putAll(pipelineTemplateDeclarers);
    return result;
  }

  public void registerProtocol(SpProtocolDefinitionFactory<?> protocol) {
    SpProtocolManager.INSTANCE.register(protocol);
    this.supportedProtocols.put(protocol.getTransportProtocolClass(),
        protocol.getTransportProtocol());
  }

  public void registerProtocols(List<SpProtocolDefinitionFactory<?>> protocols) {
    protocols.forEach(this::registerProtocol);
  }

  public void registerDataFormat(SpDataFormatFactory dataFormatDefinition) {
    SpDataFormatManager.INSTANCE.register(dataFormatDefinition);
    this.supportedFormats.put(dataFormatDefinition.getTransportFormatRdfUri(),
        dataFormatDefinition.getTransportFormat());
  }

  public void registerDataFormats(SpDataFormatFactory... dataFormatDefinitions) {
    registerDataFormats(Arrays.asList(dataFormatDefinitions));
  }

  public void registerDataFormats(List<SpDataFormatFactory> dataFormatDefinitions) {
    dataFormatDefinitions.forEach(this::registerDataFormat);
  }

  private void addEpaDeclarer(SemanticEventProcessingAgentDeclarer epaDeclarer) {
    epaDeclarers.put(epaDeclarer.declareModel().getAppId(), epaDeclarer);
  }

  private void addStreamDeclarer(DataStreamDeclarer streamDeclarer) {
    streamDeclarers.put(streamDeclarer.declareModel().getAppId(), streamDeclarer);
    checkAndStartExecutableStreams(streamDeclarer);
  }

  private void addConsumerDeclarer(SemanticEventConsumerDeclarer consumerDeclarer) {
    consumerDeclarers.put(consumerDeclarer.declareModel().getAppId(), consumerDeclarer);
  }

  private void addPipelineTemplateDeclarer(PipelineTemplateDeclarer pipelineTemplateDeclarer) {
    pipelineTemplateDeclarers.put(pipelineTemplateDeclarer.declareModel().getAppId(),
        pipelineTemplateDeclarer);
  }

  public Map<String, SemanticEventProcessingAgentDeclarer> getEpaDeclarers() {
    return epaDeclarers;
  }

  public Map<String, DataStreamDeclarer> getStreamDeclarers() {
    return streamDeclarers;
  }

  public Map<String, SemanticEventConsumerDeclarer> getConsumerDeclarers() {
    return consumerDeclarers;
  }

  public Map<String, PipelineTemplateDeclarer> getPipelineTemplateDeclarers() {
    return pipelineTemplateDeclarers;
  }

  public Collection<TransportProtocol> getSupportedProtocols() {
    return this.supportedProtocols
        .values()
        .stream()
        .map(p -> new Cloner().protocol(p))
        .collect(Collectors.toList());
  }

  public Collection<TransportFormat> getSupportedFormats() {
    return this.supportedFormats.values()
        .stream()
        .map(TransportFormat::new)
        .collect(Collectors.toList());
  }

  public int getPort() {
    return this.port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getHostName() {
    return this.hostName;
  }

  public void setHostName(String host) {
    this.hostName = host;
  }

  public void setRoute(String route) {
    this.route = Slash + route + Slash;
  }

  public String getBaseUri() {
    return Http + hostName + Colon + port + route;
  }

  public String getServiceId() {
    return serviceId;
  }

  public Map<String, IStreamPipesFunctionDeclarer> getFunctions() {
    return functions;
  }

  public Collection<AdapterInterface> getAdapters() {
    return adapters.values();
  }

  public Map<String, AdapterInterface> getAdapterMap() {
    return adapters;
  }

  public void setAdapters(List<AdapterInterface> adapters) {
    adapters.forEach(a -> this.adapters.put(a.declareConfig().getAdapterDescription().getAppId(), a));
  }

  public Optional<AdapterInterface> getAdapter(String id) {
    return getAdapters().stream()
        .filter(adapter -> adapter.declareConfig()
            .getAdapterDescription()
            .getAppId().equals(id))
        .findFirst();
  }

  private void checkAndStartExecutableStreams(DataStreamDeclarer declarer) {
    if (declarer.isExecutable()) {
      declarer.executeStream();
    }
  }

  public SpServiceDefinition toServiceDefinition(String serviceId) {
    SpServiceDefinition serviceDef = new SpServiceDefinition();
    serviceDef.setServiceId(serviceId);
    serviceDef.setDefaultPort(this.getPort());

    // TODO create complete service definition
    return serviceDef;
  }

  public SpServiceDefinition getServiceDefinition() {
    return this.serviceDefinition;
  }
}
