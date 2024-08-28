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
package org.apache.streampipes.model.client.user;

import org.apache.streampipes.model.shared.annotation.TsModel;

@TsModel
public enum Privilege {
  // Pipelines
  PRIVILEGE_READ_PIPELINE(Constants.PRIVILEGE_READ_PIPELINE_VALUE),
  PRIVILEGE_WRITE_PIPELINE(Constants.PRIVILEGE_WRITE_PIPELINE_VALUE),

  // Adapters
  PRIVILEGE_READ_ADAPTER(Constants.PRIVILEGE_READ_ADAPTER_VALUE),
  PRIVILEGE_WRITE_ADAPTER(Constants.PRIVILEGE_WRITE_ADAPTER_VALUE),

  // Pipeline Elements
  PRIVILEGE_READ_PIPELINE_ELEMENT(Constants.PRIVILEGE_READ_PIPELINE_ELEMENT_VALUE),
  PRIVILEGE_WRITE_PIPELINE_ELEMENT(Constants.PRIVILEGE_WRITE_PIPELINE_ELEMENT_VALUE),

  // Dashboard
  PRIVILEGE_READ_DASHBOARD(Constants.PRIVILEGE_READ_DASHBOARD_VALUE),
  PRIVILEGE_WRITE_DASHBOARD(Constants.PRIVILEGE_WRITE_DASHBOARD_VALUE),

  // Dashboard widget
  PRIVILEGE_READ_DASHBOARD_WIDGET(Constants.PRIVILEGE_READ_DASHBOARD_WIDGET_VALUE),
  PRIVILEGE_WRITE_DASHBOARD_WIDGET(Constants.PRIVILEGE_WRITE_DASHBOARD_WIDGET_VALUE),

  // Data Explorer view
  PRIVILEGE_READ_DATA_EXPLORER_VIEW(Constants.PRIVILEGE_READ_DATA_EXPLORER_VIEW_VALUE),
  PRIVILEGE_WRITE_DATA_EXPLORER_VIEW(Constants.PRIVILEGE_WRITE_DATA_EXPLORER_VIEW_VALUE),

  // Data Explorer widget
  PRIVILEGE_READ_DATA_EXPLORER_WIDGET(Constants.PRIVILEGE_READ_DATA_EXPLORER_WIDGET_VALUE),
  PRIVILEGE_WRITE_DATA_EXPLORER_WIDGET(Constants.PRIVILEGE_WRITE_DATA_EXPLORER_WIDGET_VALUE),

  // Apps
  PRIVILEGE_READ_APPS(Constants.PRIVILEGE_READ_APPS_VALUE),
  PRIVILEGE_WRITE_APPS(Constants.PRIVILEGE_WRITE_APPS_VALUE),

  // NOTIFICATIONS
  PRIVILEGE_READ_NOTIFICATIONS(Constants.PRIVILEGE_READ_NOTIFICATIONS_VALUE),

  // FILES
  PRIVILEGE_READ_FILES(Constants.PRIVILEGE_READ_FILES_VALUE),
  PRIVILEGE_WRITE_FILES(Constants.PRIVILEGE_WRITE_FILES_VALUE),

  // ASSETS
  PRIVILEGE_READ_ASSETS(Constants.PRIVILEGE_READ_ASSETS_VALUE),
  PRIVILEGE_WRITE_ASSETS(Constants.PRIVILEGE_WRITE_ASSETS_VALUE),

  // GENERIC STORAGE
  PRIVILEGE_READ_GENERIC_STORAGE(Constants.PRIVILEGE_READ_GENERIC_STORAGE_VALUE),
  PRIVILEGE_WRITE_GENERIC_STORAGE(Constants.PRIVILEGE_WRITE_GENERIC_STORAGE_VALUE);

  private String privilegeString;

  Privilege(String privilegeString) {
    this.privilegeString = privilegeString;
  }

  public static final class Constants {
    public static final String PRIVILEGE_READ_PIPELINE_VALUE = "PRIVILEGE_READ_PIPELINE";
    public static final String PRIVILEGE_WRITE_PIPELINE_VALUE = "PRIVILEGE_WRITE_PIPELINE";

    public static final String PRIVILEGE_READ_ADAPTER_VALUE = "PRIVILEGE_READ_ADAPTER";
    public static final String PRIVILEGE_WRITE_ADAPTER_VALUE = "PRIVILEGE_WRITE_ADAPTER";

    public static final String PRIVILEGE_READ_PIPELINE_ELEMENT_VALUE = "PRIVILEGE_READ_PIPELINE_ELEMENT";
    public static final String PRIVILEGE_WRITE_PIPELINE_ELEMENT_VALUE = "PRIVILEGE_WRITE_PIPELINE_ELEMENT";

    public static final String PRIVILEGE_READ_DASHBOARD_VALUE = "PRIVILEGE_READ_DASHBOARD";
    public static final String PRIVILEGE_WRITE_DASHBOARD_VALUE = "PRIVILEGE_WRITE_DASHBOARD";

    public static final String PRIVILEGE_READ_DASHBOARD_WIDGET_VALUE = "PRIVILEGE_READ_DASHBOARD_WIDGET";
    public static final String PRIVILEGE_WRITE_DASHBOARD_WIDGET_VALUE = "PRIVILEGE_WRITE_DASHBOARD_WIDGET";

    public static final String PRIVILEGE_READ_DATA_EXPLORER_VIEW_VALUE = "PRIVILEGE_READ_DATA_EXPLORER_VIEW";
    public static final String PRIVILEGE_WRITE_DATA_EXPLORER_VIEW_VALUE = "PRIVILEGE_WRITE_DATA_EXPLORER_VIEW";

    public static final String PRIVILEGE_READ_DATA_EXPLORER_WIDGET_VALUE = "PRIVILEGE_READ_DATA_EXPLORER_WIDGET";
    public static final String PRIVILEGE_WRITE_DATA_EXPLORER_WIDGET_VALUE = "PRIVILEGE_WRITE_DATA_EXPLORER_WIDGET";

    public static final String PRIVILEGE_READ_APPS_VALUE = "PRIVILEGE_READ_APPS";
    public static final String PRIVILEGE_WRITE_APPS_VALUE = "PRIVILEGE_WRITE_APPS";

    public static final String PRIVILEGE_READ_NOTIFICATIONS_VALUE = "PRIVILEGE_READ_NOTIFICATIONS";

    public static final String PRIVILEGE_READ_FILES_VALUE = "PRIVILEGE_READ_FILES";
    public static final String PRIVILEGE_WRITE_FILES_VALUE = "PRIVILEGE_WRITE_FILES";

    public static final String PRIVILEGE_READ_ASSETS_VALUE = "PRIVILEGE_READ_ASSETS";
    public static final String PRIVILEGE_WRITE_ASSETS_VALUE = "PRIVILEGE_WRITE_ASSETS";

    public static final String PRIVILEGE_READ_GENERIC_STORAGE_VALUE = "PRIVILEGE_READ_GENERIC_STORAGE";
    public static final String PRIVILEGE_WRITE_GENERIC_STORAGE_VALUE = "PRIVILEGE_WRITE_GENERIC_STORAGE";
  }
}
