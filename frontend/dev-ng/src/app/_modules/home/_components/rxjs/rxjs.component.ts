import { HttpClient } from "@angular/common/http";
import { Component, OnInit } from "@angular/core";
import { ThemePalette } from "@angular/material/core";
import { ProgressSpinnerMode } from "@angular/material/progress-spinner";
import { forkJoin, catchError, of } from "rxjs";
import { interval, fromEvent, takeUntil } from 'rxjs';

@Component({
    selector: 'app-rxjs',
    templateUrl: './rxjs.component.html',
    styleUrls: ['./rxjs.component.scss']
})
export class RxjsComponent implements OnInit{

  color: ThemePalette = 'primary';
  mode: ProgressSpinnerMode = 'determinate';
  value = 50;
  ngOnInit(): void {
    // const source = interval(1000);
    // const clicks = fromEvent(document, 'click');
    // const result = source.pipe(takeUntil(clicks));
    // result.subscribe(x => console.log(x));
  }
  /*
    postsData: any;
    userData: any;
    error: string | null = null;

    constructor(private http: HttpClient) {}

    ngOnInit(): void {
      this.fetchPostsAndUser();
    }

    fetchPostsAndUser() {
      forkJoin([
        this.fetchPosts(),
        this.fetchUser()
      ]).subscribe({
        next: ([posts, user]) => {
          console.log("posts: ",posts);
          console.log("user: ",user);
          this.error = null;
        },
        error: err => {
          this.postsData = null;
          this.userData = null;
          this.error = 'Failed to fetch data';
        }
      });
    }

    fetchPosts() {
      const url = 'https://jsonplaceholder.typicode.com/posts';
      return this.http.get(url).pipe(
        catchError(error => {
          this.error = `Posts Error: ${error.message}`;
          return of(null); // Return an empty observable on error
        })
      );
    }

    fetchUser() {
      const url = 'https://randomuser.me/api/';
      return this.http.get(url).pipe(
        catchError(error => {
          this.error = `User Error: ${error.message}`;
          return of(null); // Return an empty observable on error
        })
      );
    }
      */



}