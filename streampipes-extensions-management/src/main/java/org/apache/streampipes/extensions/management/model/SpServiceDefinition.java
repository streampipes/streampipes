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
package org.apache.streampipes.extensions.management.model;

import org.apache.streampipes.dataformat.SpDataFormatFactory;
import org.apache.streampipes.extensions.api.connect.AdapterInterface;
import org.apache.streampipes.extensions.api.declarer.Declarer;
import org.apache.streampipes.extensions.api.declarer.IStreamPipesFunctionDeclarer;
import org.apache.streampipes.messaging.SpProtocolDefinitionFactory;
import org.apache.streampipes.svcdiscovery.api.model.ConfigItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SpServiceDefinition {

  private String serviceGroup;
  private String serviceId;
  private String serviceName;
  private String serviceDescription;
  private Integer defaultPort;

  private List<Declarer<?>> declarers;
  private List<SpDataFormatFactory> dataFormatFactories;
  private List<SpProtocolDefinitionFactory<?>> protocolDefinitionFactories;
  private List<IStreamPipesFunctionDeclarer> functions;

  private List<AdapterInterface> adapters;

  private Map<String, ConfigItem> kvConfigs;

  public SpServiceDefinition() {
    this.serviceId = UUID.randomUUID().toString();
    this.declarers = new ArrayList<>();
    this.dataFormatFactories = new ArrayList<>();
    this.protocolDefinitionFactories = new ArrayList<>();
    this.kvConfigs = new HashMap<>();
    this.functions = new ArrayList<>();
    this.adapters = new ArrayList<>();
  }

  public String getServiceGroup() {
    return serviceGroup;
  }

  public void setServiceGroup(String serviceGroup) {
    this.serviceGroup = serviceGroup;
  }

  public String getServiceId() {
    return serviceId;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getServiceDescription() {
    return serviceDescription;
  }

  public void setServiceDescription(String serviceDescription) {
    this.serviceDescription = serviceDescription;
  }

  public Integer getDefaultPort() {
    return defaultPort;
  }

  public void setDefaultPort(Integer defaultPort) {
    this.defaultPort = defaultPort;
  }

  public void addDeclarer(Declarer<?> declarer) {
    this.declarers.add(declarer);
  }

  public void addDeclarers(List<Declarer<?>> declarers) {
    this.declarers.addAll(declarers);
  }

  public List<Declarer<?>> getDeclarers() {
    return declarers;
  }

  public void setDeclarers(List<Declarer<?>> declarers) {
    this.declarers = declarers;
  }

  public void addDataFormatFactory(SpDataFormatFactory factory) {
    this.dataFormatFactories.add(factory);
  }

  public void addDataFormatFactories(List<SpDataFormatFactory> factories) {
    this.dataFormatFactories.addAll(factories);
  }

  public void addConfig(ConfigItem configItem) {
    this.kvConfigs.put(configItem.getKey(), configItem);
  }

  public void addAdapter(AdapterInterface adapter) {
    this.adapters.add(adapter);
  }

  public void addAdapters(List<AdapterInterface> adapters) {
    this.adapters.addAll(adapters);
  }

  public List<SpDataFormatFactory> getDataFormatFactories() {
    return dataFormatFactories;
  }

  public void setDataFormatFactories(List<SpDataFormatFactory> dataFormatFactories) {
    this.dataFormatFactories = dataFormatFactories;
  }

  public void addProtocolDefinitionFactory(SpProtocolDefinitionFactory<?> factory) {
    this.protocolDefinitionFactories.add(factory);
  }

  public void addProtocolDefinitionFactories(List<SpProtocolDefinitionFactory<?>> factories) {
    this.protocolDefinitionFactories.addAll(factories);
  }

  public List<SpProtocolDefinitionFactory<?>> getProtocolDefinitionFactories() {
    return protocolDefinitionFactories;
  }

  public void setProtocolDefinitionFactories(List<SpProtocolDefinitionFactory<?>> protocolDefinitionFactories) {
    this.protocolDefinitionFactories = protocolDefinitionFactories;
  }

  public Map<String, ConfigItem> getKvConfigs() {
    return kvConfigs;
  }

  public void addStreamPipesFunction(IStreamPipesFunctionDeclarer function) {
    this.functions.add(function);
  }

  public List<IStreamPipesFunctionDeclarer> getFunctions() {
    return functions;
  }

  public List<AdapterInterface> getAdapters() {
    return adapters;
  }
}
