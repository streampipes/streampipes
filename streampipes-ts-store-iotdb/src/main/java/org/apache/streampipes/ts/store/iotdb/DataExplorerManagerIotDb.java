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

package org.apache.streampipes.ts.store.iotdb;

import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.dataexplorer.DataExplorerSchemaManagement;
import org.apache.streampipes.dataexplorer.api.*;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.ts.store.iotdb.sanitize.DataLakeMeasurementSanitizerIotDb;

import java.util.List;

public enum DataExplorerManagerIotDb implements IDataExplorerManager {

  INSTANCE;

  @Override
  public IDataLakeMeasurementCounter getMeasurementCounter(List<DataLakeMeasure> allMeasurements, List<String> measurementsToCount) {
    return null;
  }

  @Override
  public IDataExplorerQueryManagement getQueryManagement(IDataExplorerSchemaManagement dataExplorerSchemaManagement) {
    return null;
  }

  @Override
  public IDataExplorerSchemaManagement getSchemaManagement() {
    return new DataExplorerSchemaManagement(StorageDispatcher.INSTANCE
                                                             .getNoSqlStore()
                                                             .getDataLakeStorage());
  }

  @Override
  public ITimeSeriesStorage getTimeseriesStorage(DataLakeMeasure measure) {
    return new TimeSeriesStorageIotDb(measure, new IotDbPropertyConverter(), new IotDbSessionProvider());
  }

  @Override
  public IDataLakeMeasurementSanitizer getMeasurementSanitizer(IStreamPipesClient client, DataLakeMeasure measure) {
    return new DataLakeMeasurementSanitizerIotDb(client, measure);
  }
}
