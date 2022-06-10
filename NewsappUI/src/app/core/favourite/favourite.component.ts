import { Component, OnInit } from '@angular/core';
import { CoreService } from '../core.service';
import {ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';
import { ArticleModel } from '../../models/ArticleModel';
import { MatSnackBar ,MatDialog} from '@angular/material';

@Component({
  selector: 'app-favourite',
  templateUrl: './favourite.component.html',
  styleUrls: ['./favourite.component.css']
})
export class FavouriteComponent implements OnInit {
  articles: Array<ArticleModel> = [];
  constructor(private _coreService: CoreService, private _snackBar : MatSnackBar,
    private _dialog:MatDialog) { }

  ngOnInit() {
    this.fetchMyArticles();
  }

  fetchMyArticles(): void {
    this._coreService.fetchMyArticles().subscribe((response: any) => {
      if (null != response) {
        response.map((val: any) => {
          this.articles.push(val);
        })
      }
    });
  }

}
