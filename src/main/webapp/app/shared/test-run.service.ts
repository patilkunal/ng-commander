import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

import {TestRunInstance} from './test-run-instance';


@Injectable()
export class TestRunService {

    constructor(private http: HttpClient )
    {}

    getTestRuns(): Observable<TestRunInstance[]> {
        return this.http.get<TestRunInstance[]>('/ng-commander/service/testruns');
    }
}