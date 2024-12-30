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

import { Component, Input } from '@angular/core';
import {
    AdapterDescription,
    AdapterService,
} from '@streampipes/platform-services';
import { DialogRef } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-delete-adapter-dialog',
    templateUrl: './delete-adapter-dialog.component.html',
    styleUrls: ['./delete-adapter-dialog.component.scss'],
})
export class DeleteAdapterDialogComponent {
    @Input()
    adapter: AdapterDescription;

    isInProgress = false;
    currentStatus: any;
    adapterUsedByPipeline = false;
    deleteAssociatedPipelines = false;
    namesOfPipelinesUsingAdapter = '';
    namesOfPipelinesNotOwnedByUser = '';

    constructor(
        private dialogRef: DialogRef<DeleteAdapterDialogComponent>,
        private adapterService: AdapterService,
    ) {}

    close(refreshAdapters: boolean) {
        this.dialogRef.close(refreshAdapters);
    }

    deleteAdapter(deleteAssociatedPipelines: boolean) {
        this.isInProgress = true;
        this.currentStatus = 'Deleting adapter...';
        this.deleteAssociatedPipelines = deleteAssociatedPipelines;

        this.adapterService
            .deleteAdapter(this.adapter, deleteAssociatedPipelines)
            .subscribe({
                next: () => {
                    this.close(true);
                },
                error: error => {
                    if (error.status === 409) {
                        if (deleteAssociatedPipelines) {
                            this.namesOfPipelinesNotOwnedByUser = error.error;
                        } else {
                            this.namesOfPipelinesUsingAdapter = error.error;
                        }
                        this.adapterUsedByPipeline = true;
                        this.isInProgress = false;
                    }
                },
            });
    }
}
