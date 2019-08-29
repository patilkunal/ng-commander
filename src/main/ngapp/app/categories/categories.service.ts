import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import { ErrorHandlerService } from '../shared/error-handler.service';
import {Category} from '../shared/category';

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/catch';

@Injectable()
export class CategoriesService {

    private URL: string = 'service/categories';

    constructor(private httpClient: HttpClient, private errorHandler: ErrorHandlerService) {

    }
    getCategories(): Observable<Category[]> {
        return this.httpClient.get<Category[]>(this.URL);
                    //.map( (resp: Response) => resp.json());
                    
    }

    getCategory(id: number): Observable<Category> {
        return this.httpClient.get<Category>(this.URL + "/" + id);
    }

    saveCategory(cat: Category): Observable<any> {
        if(cat.id > 0) {
            return this.httpClient.put(this.URL + "/" + cat.id, cat);
        } else {
            return this.httpClient.post(this.URL, cat);            
        }
    }

    deleteCategory(id: number): Promise<any> {
        return this.httpClient.delete(this.URL).toPromise();
    }

}