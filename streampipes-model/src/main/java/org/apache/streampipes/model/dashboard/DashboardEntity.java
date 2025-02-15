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

package org.apache.streampipes.model.dashboard;

import org.apache.streampipes.model.datalake.DataExplorerWidgetModel;
import org.apache.streampipes.model.shared.api.Storable;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.google.gson.annotations.SerializedName;

@JsonSubTypes({
    @JsonSubTypes.Type(DataExplorerWidgetModel.class)
})
public abstract class DashboardEntity implements Storable {

  @JsonAlias("_id")
  @SerializedName("_id")
  private String elementId;

  @JsonAlias("_rev")
  @SerializedName("_rev")
  private String rev;

  public DashboardEntity() {
    super();
  }

  @Override
  public String getRev() {
    return rev;
  }

  @Override
  public void setRev(String rev) {
    this.rev = rev;
  }

  @Override
  public String getElementId() {
    return elementId;
  }

  @Override
  public void setElementId(String elementId) {
    this.elementId = elementId;
  }
}
