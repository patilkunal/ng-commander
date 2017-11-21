import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { CategoriesComponent } from './categories.component';
import { CategoryDetailComponent } from './detail/category-detail.component';
import { CategoriesService } from './categories.service';
import { CategoriesRoutingModule } from './categories-routing.module';

@NgModule({
  //imports only accept NgModules
  imports: [CommonModule, CategoriesRoutingModule, FormsModule],
  //declaration only accept component, pipe or directive
  declarations: [
    CategoriesComponent,
    CategoryDetailComponent    
  ],
  //Export what you want to make visible outside
  //No need to export CategoriesComponent since we are providing the routing
  exports: [],
  providers: [CategoriesService]
})
export class CategoriesModule {

}