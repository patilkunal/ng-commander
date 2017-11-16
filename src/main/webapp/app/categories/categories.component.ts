import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { CategoriesService } from './categories.service';
import { Category } from '../shared/category';
import { Subscription } from 'rxjs';

@Component({
  selector: 'commander-categories',
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.css']
})
export class CategoriesComponent implements OnInit {

  private categories: Category[];
  private categoriesSub: Subscription;
  constructor(private service: CategoriesService) { }

  ngOnInit() {
    this.categoriesSub = this.service.getCategories()
              .subscribe((data) => { this.categories = data },
              (err: HttpErrorResponse) => {
                if (err.error instanceof Error) {
                  // A client-side or network error occurred. Handle it accordingly.
                  console.log('An error occurred:', err.error.message);
                } else {
                  // The backend returned an unsuccessful response code.
                  // The response body may contain clues as to what went wrong,
                  console.log(`Backend returned code ${err.status}, body was: ${err.error}`);
              }
            });
  }

  ngOnDestroy() {
    this.categoriesSub.unsubscribe();
  }
}
