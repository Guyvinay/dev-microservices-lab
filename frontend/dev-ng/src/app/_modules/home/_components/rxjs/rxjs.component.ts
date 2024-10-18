import { HttpClient } from "@angular/common/http";
import { Component, OnInit } from "@angular/core";
import { ThemePalette } from "@angular/material/core";
import { ProgressSpinnerMode } from "@angular/material/progress-spinner";
import { forkJoin, catchError, of, Observable, from } from "rxjs";
import { interval, fromEvent, takeUntil } from "rxjs";
import { Post, User } from "src/app/_models/models";
import { HttpService } from "src/app/_services/http.service";

@Component({
  selector: "app-rxjs",
  templateUrl: "./rxjs.component.html",
  styleUrls: ["./rxjs.component.scss"],
})
export class RxjsComponent implements OnInit {
  color: ThemePalette = "primary";
  mode: ProgressSpinnerMode = "determinate";
  value = 50;
  data: any;

  ngOnInit(): void {
    // const source = interval(1000);
    // const clicks = fromEvent(document, 'click');
    // const result = source.pipe(takeUntil(clicks));
    // result.subscribe(x => console.log(x));
    // this.fetchUserPosts();


    // this.fetchBothApisWithAsync();
    // this.fetchBothApisWithMergeMap();
    // this.fetchBothApisWithConcatMap();
    this.fetchBothApisWithConcatMapV2();
    this.fetchBothApisWithMergeMapV2();
  }

  async fetchBothApisWithAsync() {
    try {
      console.log("fetching fetchBothApisWithAsync");
      this.data = await this.httpService.fetchBothApisWithAsync();
      console.log("fetchBothApisWithAsync: ",this.data);
      console.log("fetched fetchBothApisWithAsync");
    } catch (error) {
      console.error('Error in fetching data', error);
    }
  }

  fetchBothApisWithMergeMap() {
    this.httpService.fetchBothApisWithMergeMap().subscribe((response) => {
      console.log("fetchBothApisWithMergeMap: ", response);
    });
  }

  fetchBothApisWithConcatMap() {
    this.httpService.fetchBothApisWithConcatMap().subscribe((response) => {
      console.log("fetchBothApisWithConcatMap: ", response);
    });
  }

  fetchBothApisWithConcatMapV2() {
    this.httpService.fetchBothApisWithConcatMapV2().subscribe((response) => {
      console.log("fetchBothApisWithConcatMapV2: ", response);
    });
  }

  fetchBothApisWithMergeMapV2() {
    this.httpService.fetchBothApisWithMergeMapV2().subscribe((response) => {
      console.log("fetchBothApisWithMergeMapV2: ", response);
    });
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

  constructor(private httpService: HttpService) {}

  fetchUserPosts() {
    this.httpService.fetchUserPosts().subscribe({
      next: (data) => {
        console.log(data);
      },
      error: (err) => {},
    });
  }
}
