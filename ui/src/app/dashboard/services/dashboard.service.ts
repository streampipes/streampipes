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

import { Dashboard } from '@streampipes/platform-services';
import { EditDashboardDialogComponent } from '../dialogs/edit-dashboard/edit-dashboard-dialog.component';
import { DialogService, PanelType } from '@streampipes/shared-ui';
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class DataExplorerDashboardService {
    constructor(private dialogService: DialogService) {}

    openDashboardModificationDialog(createMode: boolean, dashboard: Dashboard) {
        return this.dialogService.open(EditDashboardDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: createMode ? 'New Dashboard' : 'Edit Dashboard',
            width: '60vw',
            data: {
                createMode: createMode,
                dashboard: dashboard,
            },
        });
    }
}
