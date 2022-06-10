
import { Component, OnInit, ViewChild, ElementRef, EventEmitter, Output, NgZone } from '@angular/core';
import { FormControl ,FormsModule} from '@angular/forms';
import {CoreService} from '../core.service';
import {AuthService} from '../../auth/authservice.service';
import { ArticleModel} from '../../models/ArticleModel';
import { DatePipe } from '@angular/common';
import { Observable } from 'rxjs';
import { SourceModel } from '../../models/SourceModel';
import { MatSnackBar } from '@angular/material';
import { NgMatSearchBarModule } from 'ng-mat-search-bar';
import { THIS_EXPR } from '@angular/compiler/src/output/output_ast';
import { forEach } from '@angular/router/src/utils/collection';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css'],
  providers: [DatePipe]
})
export class SearchComponent {

  articles : Array<ArticleModel> = [];
  isAdded :string;
  searchText:string;
  constructor(private zone:NgZone,
             private _coreService:CoreService,
             private _authService:AuthService,
             private _datePipe:DatePipe,
             private _snackBar : MatSnackBar) { }

  numberOfLikes : number = 0;
  
  /*
  likeButtonClick(article) {
	if(this.numberOfLikes<1){
    this.numberOfLikes++;
    }
    this.updateDB(this.numberOfLikes, article);    
  }*/
  
  
  updateDB(numberOfLikes, article:ArticleModel ){
	  this._coreService.updateArticleCount(article).subscribe((response: any) => {
      ///this.zone.run(() => {
      		console.log("Added into DB");
          article.userId = this._authService.getUser();
          var i = this.articles.findIndex(r => article.title == r.title);
          var artObject = JSON.parse(response);
          artObject.countlike = 1;
          this.articles[i] = artObject;
      //}); 
    });
  }

  likeButtonClick(article: ArticleModel) {
    if(this.numberOfLikes<1){
    this.numberOfLikes++;
    }
    this.updateDB(this.numberOfLikes, article);
  }
  
  ngOnInit() {
    //this.getNews();
	  this.getDatabaseArticles();
  }

  
  getDatabaseArticles(){
	this._coreService.fetchMyArticles().subscribe((response: any) => {
        if (null != response) {
        response.map((val: any) => {
          
          this.articles.push(val);

        });

          this.articles.forEach(val => {
            if( val.userliked){

              val.countlike = 1;
            }else{
              val.countlike =  0;
            }
         });
      }
  });
}

  transformDate(myDate:Date) {
    return this._datePipe.transform(myDate, 'yyyy-MM-dd');
  }

  addToFavourite(article: ArticleModel): void{
    article.setIsAdded('true');
    this._coreService.addToFavMap(article['publishedAt'],article);

    this._coreService.addToFavourite(article).subscribe((response: any) => {
      this._snackBar.open(`Aricle added to wishlist successfully`,null,{duration : 2000,});
    });
    console.log(this._coreService.favMap.size + " **** after adding "+this._coreService.favMap.values());
  }
  
  getIsAddedInFav(publishedAt: string, title: string): string {
    let returnVal='false';
    if (this._coreService.favMap.size >0) {
      console.log(this._coreService.favMap.size + " **** after the time of search "+this._coreService.favMap.values());
      this._coreService.favMap.forEach((value: ArticleModel, key: string) => {
        let article: ArticleModel = this._coreService.favMap.get(key);
        if (article['publishedAt'] === publishedAt && article['title'] === title) {
          returnVal = 'true';
        }
      });
    }
     
    return returnVal;
    
    
  }

}


function callback(callback: any) {
  throw new Error('Function not implemented.');
}

