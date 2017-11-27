import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';

import {Category} from '../shared/category';

@Injectable()
export class CategoriesService {

    private URL: string = 'service/categories';

    constructor(private httpClient: HttpClient) {

    }
    getCategories(): Observable<Category[]> {
        return this.httpClient.get<Category[]>(this.URL);
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