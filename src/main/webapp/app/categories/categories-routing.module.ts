import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { CategoriesComponent } from './categories.component';
import { CategoryDetailComponent } from './detail/category-detail.component';

const routes: Routes = [
    {path: 'categories',
        children: [
            {path: '', redirectTo: 'list', pathMatch: 'full'},
            {path: 'list', component: CategoriesComponent}, 
            {path: ':id', component: CategoryDetailComponent}, 
        ]
    }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CategoriesRoutingModule { }

