import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { CategoriesService } from '../categories.service';
import { Category } from '../../shared/category';
import { ErrorHandlerService } from '../../shared/error-handler.service';
import { Subscription } from 'rxjs/Subscription';
import 'rxjs/add/operator/switchMap';

@Component({
  selector: 'category-detail',
  templateUrl: './category-detail.component.html',
  styleUrls: ['./category-detail.component.css']
})
export class CategoryDetailComponent implements OnInit, OnDestroy {

  public category: Category;
  private id: number;
  private svcSubscription: Subscription;

  constructor(private actRoute: ActivatedRoute, private router: Router, private service: CategoriesService, private errorHandler: ErrorHandlerService) {  }

  ngOnInit() {
    this.category = new Category(0, '', '',0);
    this.actRoute.paramMap

    this.svcSubscription = this.service.getCategory(this.id)
      .subscribe((data) => {console.log(JSON.stringify(data)); this.category = data;},
      (err) => this.errorHandler.handleHttpError(err));
  }

  ngOnDestroy() {
    this.svcSubscription.unsubscribe();
  }

  save() {
    console.log("Saving category detail");
    this.router.navigate(["/categories"]);
  }

  cancel() {
    console.log("Cancelling save");
    this.router.navigate(["/categories"]);
  }



}
