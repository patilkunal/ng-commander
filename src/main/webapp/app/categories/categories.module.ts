import { NgModule } from '@angular/core';
import { CategoriesComponent } from './categories.component';
import { CategoryDetailComponent } from './detail/category-detail.component';
import { CategoriesService } from './categories.service';
import { CategoriesRoutingModule } from './categories-routing.module';

@NgModule({
  //imports only accept NgModules
  imports: [CategoriesRoutingModule],
  //declaration only accept component, pipe or directive
  declarations: [
    CategoriesComponent,
    CategoryDetailComponent    
  ],
  //Export what you want to make visible outside
  exports: [],
  providers: [CategoriesService]
})
export class CategoriesModule {

}