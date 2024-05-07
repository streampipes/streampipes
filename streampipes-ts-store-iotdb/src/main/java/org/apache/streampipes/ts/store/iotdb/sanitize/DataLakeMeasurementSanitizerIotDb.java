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

package org.apache.streampipes.ts.store.iotdb.sanitize;

import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataexplorer.DataLakeMeasurementSanitizer;
import org.apache.streampipes.model.datalake.DataLakeMeasure;

public class DataLakeMeasurementSanitizerIotDb extends DataLakeMeasurementSanitizer {
  public DataLakeMeasurementSanitizerIotDb(IStreamPipesClient client, DataLakeMeasure measure) {
    super(client, measure);
  }

  @Override
  protected void cleanDataLakeMeasure() throws SpRuntimeException {
      measure.setMeasureName(new MeasureNameSanitizerIotDb().sanitize(measure.getMeasureName()));

      measure.getEventSchema()
             .getEventProperties()
             .forEach(eventProperty -> eventProperty.setRuntimeName(
                 new IotDbNameSanitizer().renameReservedKeywords(eventProperty.getRuntimeName()))
             );
  }

  protected DataLakeMeasure getMeasure() {
    return measure;
  }
}
