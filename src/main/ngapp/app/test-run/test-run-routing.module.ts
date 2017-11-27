import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {TestRunComponent} from './test-run.component';
import {TestRunEditComponent} from './edit/test-run-edit.component';
import {TestRunDetailComponent} from './detail/test-run-detail.component';

const routes: Routes = [
    {path: 'runtest',
        children: [
            {path:'', redirectTo:'list', pathMatch: 'full'},
            {path:'list', component: TestRunComponent},
            {path:':id', component: TestRunDetailComponent},
            {path: ':id/edit', component: TestRunEditComponent}
        ]
    }
];

@NgModule({
    imports: []
})
export class TestRunRoutingModule {

}