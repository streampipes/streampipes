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

import { EChartsOption, PieSeriesOption } from 'echarts';
import { DataTransformOption } from 'echarts/types/src/data/helper/transform';
import { SpBaseSingleFieldEchartsRenderer } from '../../../echarts-renderer/base-single-field-echarts-renderer';
import { Injectable } from '@angular/core';
import { PieChartWidgetModel } from './model/pie-chart-widget.model';
import { FieldUpdateInfo } from '../../../models/field-update.model';
import { ZRColor } from 'echarts/types/dist/shared';
import { ColorMappingService } from '../../../services/color-mapping.service';

@Injectable({ providedIn: 'root' })
export class SpPieRendererService extends SpBaseSingleFieldEchartsRenderer<
    PieChartWidgetModel,
    PieSeriesOption
> {
    constructor(private colorMappingService: ColorMappingService) {
        super();
    }

    addDatasetTransform(
        widgetConfig: PieChartWidgetModel,
    ): DataTransformOption {
        const field =
            widgetConfig.visualizationConfig.selectedProperty.fullDbName;
        return {
            type: 'ecSimpleTransform:aggregate',
            config: {
                resultDimensions: [
                    { name: 'name', from: field },
                    { name: 'value', from: 'time', method: 'count' },
                ],
                groupBy: field,
            },
        };
    }

    public handleUpdatedFields(
        fieldUpdateInfo: FieldUpdateInfo,
        widgetConfig: PieChartWidgetModel,
    ): void {
        this.fieldUpdateService.updateAnyField(
            widgetConfig.visualizationConfig.selectedProperty,
            fieldUpdateInfo,
        );
    }

    addAdditionalConfigs(option: EChartsOption) {
        // do nothing
    }

    addSeriesItem(
        name: string,
        datasetIndex: number,
        _widgetConfig: PieChartWidgetModel,
    ): PieSeriesOption {
        const innerRadius = _widgetConfig.visualizationConfig.selectedRadius;
        const colorMapping = _widgetConfig.visualizationConfig.colorMappings;
        return {
            name,
            type: 'pie',
            universalTransition: true,
            datasetIndex: datasetIndex,
            tooltip: {
                formatter: params => {
                    return `${params.marker} ${params.value[0]} <b>${params.value[1]}</b> (${params.percent}%)`;
                },
            },
            label: {
                formatter: params => {
                    return `${params.value[0]} (${params.percent}%)`;
                },
            },
            encode: { itemName: 'name', value: 'value' },
            radius: [innerRadius + '%', '90%'],
            itemStyle: {
                color: params => {
                    const category = params.data[0];
                    const color =
                        colorMapping.find(c => c.value === category.toString())
                            ?.color ||
                        this.colorMappingService.getDefaultColor(
                            params.dataIndex,
                        );
                    return color as ZRColor;
                },
            },
        };
    }

    initialTransforms(
        widgetConfig: PieChartWidgetModel,
        sourceIndex: number,
    ): DataTransformOption[] {
        const fieldSource = widgetConfig.visualizationConfig.selectedProperty;
        return fieldSource.sourceIndex === sourceIndex &&
            fieldSource.fieldCharacteristics.numeric
            ? [
                  {
                      type: 'sp:round',
                      config: {
                          fields: [
                              widgetConfig.visualizationConfig.selectedProperty,
                          ],
                          roundingValue:
                              widgetConfig.visualizationConfig.roundingValue,
                      },
                  },
              ]
            : [];
    }

    getSelectedField(widgetConfig: PieChartWidgetModel) {
        return widgetConfig.visualizationConfig.selectedProperty;
    }

    showAxes(): boolean {
        return false;
    }

    shouldApplySeriesPosition(): boolean {
        return true;
    }

    getDefaultSeriesName(widgetConfig: PieChartWidgetModel): string {
        return widgetConfig.visualizationConfig.selectedProperty.fullDbName;
    }
}
