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

import { Component, Input, OnInit } from '@angular/core';
import {
    DatalakeRestService,
    SelectedFilter,
    SourceConfig,
} from '@streampipes/platform-services';
import { ChartConfigurationService } from '../../../../../../data-explorer-shared/services/chart-configuration.service';
import { DataExplorerFieldProviderService } from '../../../../../../data-explorer-shared/services/data-explorer-field-provider-service';

@Component({
    selector: 'sp-filter-selection-panel',
    templateUrl: './filter-selection-panel.component.html',
})
export class FilterSelectionPanelComponent implements OnInit {
    @Input() sourceConfig: SourceConfig;

    tagValues: Map<string, string[]> = new Map<string, string[]>();

    constructor(
        private widgetConfigService: ChartConfigurationService,
        private fieldProviderService: DataExplorerFieldProviderService,
        private dataLakeRestService: DatalakeRestService,
    ) {}

    ngOnInit(): void {
        this.sourceConfig.queryConfig.fields.forEach(f => {
            this.tagValues.set(f.runtimeName, []);
        });
        const fieldProvider = this.fieldProviderService.generateFieldLists([
            this.sourceConfig,
        ]);
        this.sourceConfig.queryConfig.fields
            .filter(f =>
                fieldProvider.booleanFields.find(
                    df =>
                        df.measure === this.sourceConfig.measureName &&
                        df.runtimeName === f.runtimeName,
                ),
            )
            .forEach(f => this.tagValues.set(f.runtimeName, ['true', 'false']));
        const fields = this.sourceConfig.queryConfig.fields
            .filter(f =>
                fieldProvider.dimensionFields.find(
                    df =>
                        df.measure === this.sourceConfig.measureName &&
                        df.runtimeName === f.runtimeName,
                ),
            )
            .map(f => f.runtimeName);
        this.dataLakeRestService
            .getTagValues(this.sourceConfig.measureName, fields)
            .subscribe(response => {
                Object.keys(response).forEach(key => {
                    this.tagValues.set(key, response[key]);
                });
            });
    }

    addFilter() {
        const newFilter: SelectedFilter = {
            operator: '=',
            value: '',
        };
        this.sourceConfig.queryConfig.selectedFilters.push(newFilter);
        this.widgetConfigService.notify({
            refreshData: true,
            refreshView: true,
        });
        this.updateWidget();
    }

    remove(index: number) {
        this.sourceConfig.queryConfig.selectedFilters.splice(index, 1);

        this.widgetConfigService.notify({
            refreshData: true,
            refreshView: true,
        });
        this.updateWidget();
    }

    updateWidget() {
        let update = true;
        this.sourceConfig.queryConfig.selectedFilters.forEach(filter => {
            if (!filter.field || !filter.value || !filter.operator) {
                update = false;
            }
        });

        if (update) {
            this.widgetConfigService.notify({
                refreshData: true,
                refreshView: true,
            });
        }
    }
}
