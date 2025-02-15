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

package org.apache.streampipes.extensions.connectors.opcua.sink;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.connectors.opcua.client.ConnectedOpcUaClient;
import org.apache.streampipes.extensions.connectors.opcua.client.OpcUaClientProvider;
import org.apache.streampipes.extensions.connectors.opcua.config.OpcUaConfig;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.field.PrimitiveField;
import org.apache.streampipes.vocabulary.XSD;

import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class OpcUa {

  private static final Logger LOG = LoggerFactory.getLogger(OpcUa.class);

  private ConnectedOpcUaClient connectedClient;
  private final OpcUaConfig opcUaConfig;
  private OpcUaParameters params;

  private NodeId node;

  private Class<?> targetDataType;
  private Class<?> sourceDataType;

  // define a mapping of StreamPipes data types to Java classes
  private static final HashMap<String, Class<?>> XSDMatchings = new HashMap<>();

  static {
    XSDMatchings.put(XSD.DOUBLE.toString(), Double.class);
    XSDMatchings.put(XSD.INTEGER.toString(), Integer.class);
    XSDMatchings.put(XSD.INT.toString(), Integer.class);
    XSDMatchings.put(XSD.BOOLEAN.toString(), Boolean.class);
    XSDMatchings.put(XSD.STRING.toString(), String.class);
    XSDMatchings.put(XSD.FLOAT.toString(), Float.class);
  }

  // define potential mappings, left can be mapped to right
  private static final HashMap<Class<?>, Class<?>[]> compatibleDataTypes = new HashMap<>();

  static {
    compatibleDataTypes.put(Double.class, new Class[]{Float.class, String.class});
    compatibleDataTypes.put(Float.class, new Class[]{Double.class, String.class});
    compatibleDataTypes.put(Integer.class, new Class[]{Double.class, Float.class, String.class});
    compatibleDataTypes.put(Boolean.class, new Class[]{String.class});
    compatibleDataTypes.put(String.class, new Class[]{String.class});
  }

  private final OpcUaClientProvider clientProvider;

  public OpcUa(OpcUaClientProvider clientProvider,
               OpcUaParameters params) {
    this.clientProvider = clientProvider;
    this.params = params;
    this.opcUaConfig = params.config();
  }

  public void onInvocation() throws
      SpRuntimeException {

    try {
      this.node = NodeId.parse(params.selectedNode());
      this.connectedClient = clientProvider.getClient(opcUaConfig);

    } catch (Exception e) {
      throw new SpRuntimeException("Could not connect to OPC-UA server: " + params.config().getOpcServerURL());
    }

    // check whether input data type and target data type are compatible
    try {
      Variant value = this.connectedClient.getClient().getAddressSpace().getVariableNode(node).readValue().getValue();
      targetDataType = value.getValue().getClass();
      sourceDataType = XSDMatchings.get(params.mappingPropertyType());
      if (!sourceDataType.equals(targetDataType)) {
        if (Arrays.stream(compatibleDataTypes.get(sourceDataType)).noneMatch(dt -> dt.equals(targetDataType))) {
          throw new SpRuntimeException("Data Type of event of target node are not compatible");
        }
      }
    } catch (UaException e) {
      throw new SpRuntimeException("DataType of target node could not be determined: " + node.getIdentifier());
    }

  }

  public void onEvent(Event inputEvent) {

    Variant v = getValue(inputEvent);

    if (v == null) {
      LOG.error("Mapping property type: " + this.params.mappingPropertyType() + " is not supported");
    } else {

      DataValue value = new DataValue(v);
      CompletableFuture<StatusCode> f = this.connectedClient.getClient().writeValue(node, value);

      try {
        StatusCode status = f.get();
        if (status.isBad()) {
          if (status.getValue() == 0x80740000L) {
            LOG.error("Type missmatch! Tried to write value of type {} ", this.params.mappingPropertyType()
                + " but server did not accept this");
          } else if (status.getValue() == 0x803B0000L) {
            LOG.error("Wrong access level. Not allowed to write to nodes");
          }
          LOG.error(
              "Value: {} could not be written to node Id {} on OPC-UA server {}",
              value.getValue().toString(),
              node.getIdentifier(),
              params.config().getOpcServerURL());
        }
      } catch (InterruptedException | ExecutionException e) {
        LOG.error(
            "Exception: Value {} could not be written to node Id {} on OPC_UA server {}",
            value.getValue().toString(),
            node.getIdentifier(),
            params.config().getOpcServerURL());
      }
    }
  }

  public void onDetach() throws SpRuntimeException {
    clientProvider.releaseClient(opcUaConfig);
  }

  private Variant getValue(Event inputEvent) {
    Variant result = null;
    PrimitiveField propertyPrimitive =
        inputEvent.getFieldBySelector(this.params.mappingPropertySelector()).getAsPrimitive();

    if (targetDataType.equals(Integer.class)) {
      result = new Variant(propertyPrimitive.getAsInt());
    } else if (targetDataType.equals(Double.class)) {
      result = new Variant(propertyPrimitive.getAsDouble());
    } else if (targetDataType.equals(Boolean.class)) {
      result = new Variant(propertyPrimitive.getAsBoolean());
    } else if (targetDataType.equals(Float.class)) {
      result = new Variant(propertyPrimitive.getAsFloat());
    } else if (targetDataType.equals(String.class)) {
      result = new Variant(propertyPrimitive.getAsString());
    }

    return result;
  }
}
