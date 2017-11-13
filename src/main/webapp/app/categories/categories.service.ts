import { Injectable } from '@angular/core';

@Injectable()
export class Category {
    constructor(public id: number, public name: string, public description: string) {

    }
}

export class CategoriesService {

    getCategories() {

    }

    getCategory(id: number) {

    }
}