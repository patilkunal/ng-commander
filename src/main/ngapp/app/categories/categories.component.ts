import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { CategoriesService } from './categories.service';
import { Category } from '../shared/category';
import { ErrorHandlerService } from '../shared/error-handler.service';
import { Subscription } from 'rxjs/Subscription';

@Component({
  selector: 'commander-categories',
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.css']
})
export class CategoriesComponent implements OnInit, OnDestroy {

  private categories: Category[];
  private categoriesSub: Subscription;

  constructor(private service: CategoriesService, private errorHandler: ErrorHandlerService) { }

  ngOnInit() {
    this.categoriesSub = this.service.getCategories()
              .subscribe((data) => { this.categories = data },
              (err) => this.errorHandler.handleHttpError(err)
            );
  }

  ngOnDestroy() {
    this.categoriesSub.unsubscribe();
  }

  deleteCategory(id: number) {
    this.service.deleteCategory(id).then(
        (data) => { 
          console.log('Delete success')
        }, 
        (err) => { console.log('Error deleting category'); this.errorHandler.handleHttpError(err) } );
  }
}
