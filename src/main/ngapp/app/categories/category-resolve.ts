import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Category } from '../shared/category';
import { CategoriesService } from './categories.service';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class CategoryResolve implements Resolve<Category> {

    constructor(private service: CategoriesService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Category | Observable<Category> | Promise<Category> {
        const id: number = parseInt(route.paramMap.get('id'));
        return this.service.getCategory( id );
        //throw new Error("Method not implemented.");
    }

}