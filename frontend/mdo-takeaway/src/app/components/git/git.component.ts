import { Component, OnInit } from '@angular/core';
import { Subject, Subscription, debounceTime, distinctUntilChanged, of, switchMap } from 'rxjs';
import { GithubService } from '../../services/github.service';
import { coerceStringArray } from '@angular/cdk/coercion';
import { User } from '../../modals/user';

@Component({
  selector: 'app-git',
  templateUrl: './git.component.html',
  styleUrl: './git.component.scss'
})
export class GitComponent implements OnInit {
  subscription!:Subscription;
  searchSubscription:Subject<string> = new Subject();
  user: any;
  error: string | null = null;


  constructor(
    private gitService:GithubService
  ){}

  ngOnInit(): void {
    this.searchSubscription.pipe(
      debounceTime(1300),
      distinctUntilChanged()
    ).subscribe((searchStr: string)=>{
      this.getUserInfo(searchStr);
    });
    /*
    this.searchSubscription.pipe(
      debounceTime(1300),
      distinctUntilChanged(),
      switchMap((searchStr)=>{
        return this.gitService.getUser(searchStr);
      })
    ).subscribe({
      next:(user:any)=> {
        this.user=user;
        this.error=null;
      },
      error:(error:any)=>{
        this.error=error;
        this.user=null;
      },
      complete:()=>{
        console.log("completed");
      }
      });
      */
  }


  getUserInfo(searchStr: string) {
    this.gitService.getUser(searchStr)
    .subscribe({
      next:(user:any)=> {
        this.user=user;
        this.error=null;
      },
      error:(error:any)=>{
        this.error=error;
        this.user=null;
      },
      complete:()=>{
        console.log("completed");
      }
  })
  }
  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}