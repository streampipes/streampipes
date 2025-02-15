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

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PlatformServicesCommons } from './commons.service';
import { Observable } from 'rxjs';
import {
    CompactPipeline,
    Message,
    Pipeline,
    PipelineElementRecommendationMessage,
    PipelineModificationMessage,
    PipelineOperationStatus,
    PipelineStatusMessage,
} from '../model/gen/streampipes-model';
import { map } from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
export class PipelineService {
    constructor(
        private http: HttpClient,
        private platformServicesCommons: PlatformServicesCommons,
    ) {}

    startPipeline(pipelineId: string): Observable<PipelineOperationStatus> {
        return this.http
            .get(`${this.apiBasePath}/pipelines/${pipelineId}/start`)
            .pipe(
                map(result =>
                    PipelineOperationStatus.fromData(
                        result as PipelineOperationStatus,
                    ),
                ),
            );
    }

    stopPipeline(
        pipelineId: string,
        forceStop?: boolean,
    ): Observable<PipelineOperationStatus> {
        const queryAppendix = forceStop ? '?forceStop=' + forceStop : '';
        return this.http
            .get(
                `${this.apiBasePath}/pipelines/${pipelineId}/stop${queryAppendix}`,
            )
            .pipe(
                map(result =>
                    PipelineOperationStatus.fromData(
                        result as PipelineOperationStatus,
                    ),
                ),
            );
    }

    getPipelineById(pipelineId: string): Observable<Pipeline> {
        return this.http
            .get(`${this.apiBasePath}/pipelines/${pipelineId}`)
            .pipe(map(response => Pipeline.fromData(response as Pipeline)));
    }

    convertToCompactPipeline(pipeline: Pipeline): Observable<CompactPipeline> {
        return this.http.post<CompactPipeline>(
            `${this.apiBasePath}/pipelines/compact`,
            pipeline,
        );
    }

    storePipeline(pipeline: Pipeline): Observable<Message> {
        return this.http.post(`${this.apiBasePath}/pipelines`, pipeline).pipe(
            map(response => {
                return Message.fromData(response as Message);
            }),
        );
    }

    updatePipeline(pipeline: Pipeline): Observable<Message> {
        const pipelineId = pipeline._id;
        return this.http
            .put(`${this.apiBasePath}/pipelines/${pipelineId}`, pipeline)
            .pipe(
                map(response => {
                    return Message.fromData(response as Message);
                }),
            );
    }

    getPipelines(): Observable<Pipeline[]> {
        return this.http.get(`${this.apiBasePath}/pipelines`).pipe(
            map(response => {
                return (response as any[]).map(p => Pipeline.fromData(p));
            }),
        );
    }

    deleteOwnPipeline(pipelineId): Observable<any> {
        return this.http.delete(`${this.apiBasePath}/pipelines/${pipelineId}`);
    }

    getPipelineStatusById(pipelineId): Observable<PipelineStatusMessage[]> {
        return this.http
            .get(`${this.apiBasePath}/pipelines/${pipelineId}/status`)
            .pipe(
                map(response => {
                    return (response as any[]).map(r =>
                        PipelineStatusMessage.fromData(r),
                    );
                }),
            );
    }

    getPipelinesContainingElementId(elementId: string): Observable<Pipeline[]> {
        return this.http
            .get(`${this.apiBasePath}/pipelines/contains/${elementId}`)
            .pipe(
                map(response => {
                    return (response as any[]).map(p => Pipeline.fromData(p));
                }),
            );
    }

    recommendPipelineElement(
        pipeline: Pipeline,
        currentDomId: string,
    ): Observable<PipelineElementRecommendationMessage> {
        return this.http
            .post(
                `${this.apiBasePath}/pipelines/recommend/${currentDomId}`,
                pipeline,
            )
            .pipe(
                map(data =>
                    PipelineElementRecommendationMessage.fromData(data as any),
                ),
            );
    }

    /**
     * Validates the given pipeline and returns a pipeline modification message.
     * The message describe how the pipeline should be modified.
     */
    validatePipeline(pipeline): Observable<PipelineModificationMessage> {
        return this.http
            .post(`${this.apiBasePath}/pipelines/validate`, pipeline)
            .pipe(
                map(data => {
                    return PipelineModificationMessage.fromData(data as any);
                }),
            );
    }

    get apiBasePath() {
        return this.platformServicesCommons.apiBasePath;
    }
}
