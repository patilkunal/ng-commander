import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';

import { ErrorHandlerComponent } from './error-handler-component';

@NgModule({
    imports: [ BrowserModule, FormsModule],
    declarations: [ErrorHandlerComponent]
})
export class ErrorHandlerModule {

}