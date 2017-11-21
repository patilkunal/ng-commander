import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';

import {Category} from '../shared/category';

@Injectable()
export class CategoriesService {

    private URL2: string = '/assets/data/categories.json';
    private URL: string = '/assets/data/category.json';

    constructor(private httpClient: HttpClient) {

    }
    getCategories(): Observable<Category[]> {
        return this.httpClient.get(this.URL2);
    }

    getCategory(id: number): Observable<Category> {
        return this.httpClient.get(this.URL + id);
    }

    saveCategory(cat: Category): Observable<any> {
        return this.httpClient.post(this.URL, cat);
    }

    deleteCategory(id: number): Promise<any> {
        return this.httpClient.delete(this.URL + id).toPromise();
    }
}