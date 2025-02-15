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

import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ChartConfigurationService } from '../../../../../data-explorer-shared/services/chart-configuration.service';
import { DataExplorerWidgetModel } from '@streampipes/platform-services';
import { ChartTypeService } from '../../../../../data-explorer-shared/services/chart-type.service';
import { DataExplorerChartRegistry } from '../../../../../data-explorer-shared/registry/data-explorer-chart-registry';
import { Subscription } from 'rxjs';

@Component({
    selector: 'sp-data-explorer-widget-appearance-settings',
    templateUrl: './data-explorer-widget-appearance-settings.component.html',
    styleUrls: ['./data-explorer-widget-appearance-settings.component.scss'],
})
export class DataExplorerWidgetAppearanceSettingsComponent
    implements OnInit, OnDestroy
{
    @Input() currentlyConfiguredWidget: DataExplorerWidgetModel;

    presetColors: string[] = [
        '#39B54A',
        '#1B1464',
        '#f44336',
        '#4CAF50',
        '#FFEB3B',
        '#FFFFFF',
        '#000000',
    ];

    widgetTypeSubscription: Subscription;
    extendedAppearanceConfigComponent: any;

    constructor(
        private widgetTypeService: ChartTypeService,
        private widgetRegistryService: DataExplorerChartRegistry,
        private widgetConfigurationService: ChartConfigurationService,
    ) {}

    ngOnInit(): void {
        this.findWidget(this.currentlyConfiguredWidget.widgetType);
        this.widgetTypeSubscription =
            this.widgetTypeService.chartTypeChangeSubject.subscribe(() => {
                this.findWidget(this.currentlyConfiguredWidget.widgetType);
            });
        if (
            !this.currentlyConfiguredWidget.baseAppearanceConfig.backgroundColor
        ) {
            this.currentlyConfiguredWidget.baseAppearanceConfig.backgroundColor =
                '#FFFFFF';
        }
        if (!this.currentlyConfiguredWidget.baseAppearanceConfig.textColor) {
            this.currentlyConfiguredWidget.baseAppearanceConfig.textColor =
                '#3e3e3e';
        }
    }

    findWidget(widgetType: string): void {
        const widget = this.widgetRegistryService.getChartTemplate(widgetType);
        if (widget) {
            this.extendedAppearanceConfigComponent =
                widget.widgetAppearanceConfigurationComponent;
        }
    }

    triggerViewUpdate() {
        this.widgetConfigurationService.notify({
            refreshView: true,
            refreshData: false,
        });
    }

    ngOnDestroy() {
        this.widgetTypeSubscription?.unsubscribe();
    }
}
