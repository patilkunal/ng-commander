import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AlertModule } from 'ngx-bootstrap';

import { AppComponent } from './app.component';
import { NavComponent } from './nav/nav.component';
import { SharedModule } from './shared/shared.module';
import { HomeModule } from './home/home.module';
import { CategoriesModule } from './categories/categories.module';
import { AppRoutingModule } from './app.routing.module';
import { TestCreateComponent } from './test-create/test-create.component';
import { TestRunComponent } from './test-run/test-run.component';
import { HostsComponent } from './hosts/hosts.component';

@NgModule({
  imports: [
    BrowserModule,
    AppRoutingModule,
    HomeModule,
    CategoriesModule,
    SharedModule
  ],
  declarations: [
    AppComponent,
    NavComponent,
    TestCreateComponent,
    TestRunComponent,
    HostsComponent
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
