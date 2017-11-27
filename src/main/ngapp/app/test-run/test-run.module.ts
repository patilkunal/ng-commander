import { NgModule } from '@angular/core';
import {FormsModule} from '@angular/forms';

import {TestRunRoutingModule} from './test-run-routing.module';
import {TestRunComponent} from './test-run.component';
import {TestRunDetailComponent} from './detail/test-run-detail.component';
import {TestRunEditComponent} from './edit/test-run-edit.component';

@NgModule({
    imports: [TestRunRoutingModule],
    declarations: [TestRunComponent, TestRunDetailComponent, TestRunEditComponent],
    providers: []
})
export class TestRunModule {

}