import {NgModule} from '@angular/core';
import {HttpClientModule} from '@angular/common/http';

import {TestCase} from './test-case';
import {TestCaseInstance} from './test-case-instance';
import {TestRunInstance} from './test-run-instance';
import {Host} from './host';

import {TestRunService} from './test-run.service';
import {HostService} from './host.service';

@NgModule({
    imports: [HttpClientModule],
    exports: [],
    providers:[TestRunService, HostService]
})
export class SharedModule {

}