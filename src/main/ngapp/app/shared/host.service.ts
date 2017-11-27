import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

import {Host} from './host';

@Injectable()
export class HostService {

    constructor(public httpClient: HttpClient) {

    }

    public getHosts():Observable<Host[]> {
        return this.httpClient.get<Host[]>('/ng-commander/service/testruns');
    }
}