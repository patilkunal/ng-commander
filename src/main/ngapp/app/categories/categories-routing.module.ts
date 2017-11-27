import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { CategoriesComponent } from './categories.component';
import { CategoryDetailComponent } from './detail/category-detail.component';
import { CategoryResolve } from './category-resolve';

const routes: Routes = [
    {path: 'categories',
        children: [
            {path: '', redirectTo: 'list', pathMatch: 'full'},
            {path: 'list', component: CategoriesComponent}, 
            {
                path: ':id', 
                component: CategoryDetailComponent, 
                resolve: {
                    category: CategoryResolve
                }
            }, 
        ]
    }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CategoriesRoutingModule { }

