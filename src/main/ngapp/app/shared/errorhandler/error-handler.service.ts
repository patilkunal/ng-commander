import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class ErrorHandlerService {

    public handleHttpError(err: HttpErrorResponse) {
        if (err.error instanceof Error) {
            // A client-side or network error occurred. Handle it accordingly.
            console.log('An error occurred:', err.error.message);
        } else {
            // The backend returned an unsuccessful response code.
            // The response body may contain clues as to what went wrong,
            console.log(`Backend returned code ${err.status}, body was: ${err.error}`);
        }
    }

    public consoleLog(err: HttpErrorResponse) {
        console.log(err);
        return Observable.throw(err.message || "Server Error");
    }
}