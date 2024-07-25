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
import { Subject } from 'rxjs';
import {
    DateRange,
    QuickTimeSelection,
    TimeSelectionId,
    TimeSettings,
} from '@streampipes/platform-services';
import {
    startOfDay,
    startOfHour,
    startOfMonth,
    startOfWeek,
    startOfYear,
    subDays,
    subHours,
    subMinutes,
    subMonths,
    subWeeks,
    subYears,
} from 'date-fns';

@Injectable({ providedIn: 'root' })
export class TimeSelectionService {
    quickTimeSelections: QuickTimeSelection[] = [
        {
            label: 'Last 15 min',
            timeSelectionId: TimeSelectionId.LAST_15_MINUTES,
            startTime: now => subMinutes(now, 15),
            endTime: now => now,
        },
        {
            label: 'Last 1 hour',
            timeSelectionId: TimeSelectionId.LAST_HOUR,
            startTime: now => subHours(now, 1),
            endTime: now => now,
        },
        {
            label: 'Last 1 day',
            timeSelectionId: TimeSelectionId.LAST_DAY,
            startTime: now => subDays(now, 1),
            endTime: now => now,
        },
        {
            label: 'Last 1 week',
            timeSelectionId: TimeSelectionId.LAST_WEEK,
            startTime: now => subWeeks(now, 1),
            endTime: now => now,
        },
        {
            label: 'Last 1 month',
            timeSelectionId: TimeSelectionId.LAST_MONTH,
            startTime: now => subMonths(now, 1),
            endTime: now => now,
        },
        {
            label: 'Last 1 year',
            timeSelectionId: TimeSelectionId.LAST_YEAR,
            startTime: now => subYears(now, 1),
            endTime: now => now,
        },
        {
            label: 'Current day',
            timeSelectionId: TimeSelectionId.CURRENT_DAY,
            startTime: now => startOfDay(now),
            endTime: now => now,
        },
        {
            label: 'Current hour',
            timeSelectionId: TimeSelectionId.CURRENT_HOUR,
            startTime: now => startOfHour(now),
            endTime: now => now,
        },
        {
            label: 'Current week',
            timeSelectionId: TimeSelectionId.CURRENT_WEEK,
            startTime: now => startOfWeek(now),
            endTime: now => now,
        },
        {
            label: 'Current month',
            timeSelectionId: TimeSelectionId.CURRENT_MONTH,
            startTime: now => startOfMonth(now),
            endTime: now => now,
        },
        {
            label: 'Current year',
            timeSelectionId: TimeSelectionId.CURRENT_YEAR,
            startTime: now => startOfYear(now),
            endTime: now => now,
        },
    ];

    public getDateRange(quickSelection: QuickTimeSelection): DateRange {
        const now = new Date();
        return {
            startDate: quickSelection.startTime(now),
            endDate: quickSelection.endTime(now),
        };
    }

    public getDefaultTimeSettings(): TimeSettings {
        return this.getTimeSettings(
            TimeSelectionId.LAST_15_MINUTES,
            new Date(),
        );
    }

    public getTimeSettings(
        timeSelectionId: TimeSelectionId,
        now: Date,
    ): TimeSettings {
        const selection = this.getTimeSelection(timeSelectionId);
        return {
            startTime: selection.startTime(now).getTime(),
            endTime: selection.endTime(now).getTime(),
            dynamicSelection: -1,
            timeSelectionId: timeSelectionId,
        };
    }

    public getTimeSelection(timeSelectionId: TimeSelectionId) {
        return this.quickTimeSelections.find(
            s => s.timeSelectionId === timeSelectionId,
        );
    }

    public timeSelectionChangeSubject: Subject<TimeSettings> =
        new Subject<TimeSettings>();

    public notify(timeSettings: TimeSettings): void {
        this.timeSelectionChangeSubject.next(timeSettings);
    }
}
