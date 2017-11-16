import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';

import {Category} from '../shared/category';

@Injectable()
export class CategoriesService {

    constructor(private httpClient: HttpClient) {

    }
    getCategories(): Observable<Category[]> {
        return this.httpClient.get('/assets/data/categories.json');
    }

    getCategory(id: number): Observable<Category> {
        return this.httpClient.get('/assets/data/category.json');
    }
}