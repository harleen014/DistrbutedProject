 import {SourceModel} from './SourceModel'

export class ArticleModel {
    constructor(
		 
     public author?:string,

     public title?:string,
   
     public description?:string,
   
     public url?:string,
   
     public urlToImage?:string,
   
     public content?:string,
   
     public publishedAt?:string,
   
     public userId?:string,
   
     public isAdded?:string,
     
     public countlike?:number,

     public userliked?:boolean,

     public totalLikes?:number,

     public sourceModel?:SourceModel

     ) {}

     public setIsAdded(val:string) :void {
         this.isAdded = val;
     }
     
     public setCountLike(val:number) :void {
         this.countlike = val;
     }
    }