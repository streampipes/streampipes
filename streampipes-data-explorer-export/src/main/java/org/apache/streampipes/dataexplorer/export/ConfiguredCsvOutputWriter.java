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

package org.apache.streampipes.dataexplorer.export;

import org.apache.streampipes.dataexplorer.export.item.CsvItemGenerator;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.param.ProvidedRestQueryParams;
import org.apache.streampipes.model.datalake.param.SupportedRestQueryParams;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.StringJoiner;

public class ConfiguredCsvOutputWriter extends ConfiguredOutputWriter {

  private static final String LINE_SEPARATOR = "\n";
  private static final String COMMA = ",";
  private static final String SEMICOLON = ";";

  private CsvItemGenerator itemGenerator;
  private String delimiter = COMMA;
  private DataLakeMeasure schema;
  private String headerColumnNameStrategy;

  @Override
  public void configure(DataLakeMeasure schema,
                        ProvidedRestQueryParams params,
                        boolean ignoreMissingValues) {
    this.schema = schema;
    this.headerColumnNameStrategy = params
        .getProvidedParams()
        .getOrDefault(SupportedRestQueryParams.QP_HEADER_COLUMN_NAME, "key");

    if (params.has(SupportedRestQueryParams.QP_CSV_DELIMITER)) {
      delimiter = params.getAsString(SupportedRestQueryParams.QP_CSV_DELIMITER).equals("comma") ? COMMA : SEMICOLON;
    }
    this.itemGenerator = new CsvItemGenerator(delimiter);
  }

  @Override
  public void beforeFirstItem(OutputStream outputStream) {
    // do nothing
  }

  @Override
  public void afterLastItem(OutputStream outputStream) {
    // do nothing
  }

  @Override
  public void writeItem(OutputStream outputStream,
                        List<Object> row,
                        List<String> columnNames,
                        boolean firstObject) throws IOException {
    if (firstObject) {
      outputStream.write(toBytes(makeHeaderLine(columnNames)));
    }

    outputStream.write(toBytes(itemGenerator.createItem(row, columnNames) + LINE_SEPARATOR));
  }

  private String makeHeaderLine(List<String> columns) {
    StringJoiner joiner = new StringJoiner(this.delimiter);
    columns.forEach(c -> joiner.add(getHeaderName(schema, c, headerColumnNameStrategy)));
    return joiner + LINE_SEPARATOR;
  }
}
