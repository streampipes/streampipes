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

export enum UserPrivilege {
    PRIVILEGE_READ_PIPELINE = 'PRIVILEGE_READ_PIPELINE',
    PRIVILEGE_WRITE_PIPELINE = 'PRIVILEGE_WRITE_PIPELINE',

    PRIVILEGE_READ_ADAPTER = 'PRIVILEGE_READ_ADAPTER',
    PRIVILEGE_WRITE_ADAPTER = 'PRIVILEGE_WRITE_ADAPTER',

    PRIVILEGE_READ_PIPELINE_ELEMENT = 'PRIVILEGE_READ_PIPELINE_ELEMENT',
    PRIVILEGE_WRITE_PIPELINE_ELEMENT = 'PRIVILEGE_WRITE_PIPELINE_ELEMENT',

    PRIVILEGE_READ_DASHBOARD = 'PRIVILEGE_READ_DASHBOARD',
    PRIVILEGE_WRITE_DASHBOARD = 'PRIVILEGE_WRITE_DASHBOARD',

    PRIVILEGE_READ_DATA_EXPLORER_VIEW = 'PRIVILEGE_READ_DATA_EXPLORER_VIEW',
    PRIVILEGE_WRITE_DATA_EXPLORER_VIEW = 'PRIVILEGE_WRITE_DATA_EXPLORER_VIEW',

    PRIVILEGE_READ_DATA_EXPLORER_WIDGET = 'PRIVILEGE_READ_DATA_EXPLORER_WIDGET',
    PRIVILEGE_WRITE_DATA_EXPLORER_WIDGET = 'PRIVILEGE_WRITE_DATA_EXPLORER_WIDGET',

    PRIVILEGE_READ_DASHBOARD_WIDGET = 'PRIVILEGE_READ_DASHBOARD_WIDGET',
    PRIVILEGE_WRITE_DASHBOARD_WIDGET = 'PRIVILEGE_WRITE_DASHBOARD_WIDGET',

    PRIVILEGE_READ_APPS = 'PRIVILEGE_READ_APPS',
    PRIVILEGE_WRITE_APPS = 'PRIVILEGE_WRITE_APPS',

    PRIVILEGE_READ_NOTIFICATIONS = 'PRIVILEGE_READ_NOTIFICATIONS',

    PRIVILEGE_READ_FILES = 'PRIVILEGE_READ_FILES',
    PRIVILEGE_WRITE_FILES = 'PRIVILEGE_WRITE_FILES',

    PRIVILEGE_READ_ASSETS = 'PRIVILEGE_READ_ASSETS',
    PRIVILEGE_WRITE_ASSETS = 'PRIVILEGE_WRITE_ASSETS',

    PRIVILEGE_READ_LABELS = 'PRIVILEGE_READ_LABELS',
    PRIVILEGE_WRITE_LABELS = 'PRIVILEGE_WRITE_LABELS',
}
