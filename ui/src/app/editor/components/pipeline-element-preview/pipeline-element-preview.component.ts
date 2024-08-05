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
import { Subscription } from 'rxjs';
import { KeyValue } from '@angular/common';
import { LivePreviewService } from '../../../services/live-preview.service';

@Component({
    selector: 'sp-pipeline-element-preview',
    templateUrl: './pipeline-element-preview.component.html',
    styleUrls: ['./pipeline-element-preview.component.scss'],
})
export class PipelineElementPreviewComponent implements OnInit, OnDestroy {
    @Input()
    previewId: string;

    @Input()
    elementId: string;

    runtimeData: Record<string, any>;
    runtimeDataError = false;
    previewSub: Subscription;

    constructor(private livePreviewService: LivePreviewService) {}

    ngOnInit(): void {
        this.getLatestRuntimeInfo();
    }

    keyValueCompareFn = (
        a: KeyValue<string, any>,
        b: KeyValue<string, any>,
    ): number => {
        return a.key.localeCompare(b.key);
    };

    getLatestRuntimeInfo() {
        this.previewSub = this.livePreviewService.eventSub.subscribe(event => {
            if (event) {
                this.runtimeData = event[this.elementId];
            } else {
                this.runtimeDataError = true;
            }
        });
    }

    ngOnDestroy() {
        this.previewSub?.unsubscribe();
    }
}
