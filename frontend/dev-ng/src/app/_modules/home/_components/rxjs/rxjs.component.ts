import { HttpClient } from "@angular/common/http";
import { Component, OnInit } from "@angular/core";
import { ThemePalette } from "@angular/material/core";
import { ProgressSpinnerMode } from "@angular/material/progress-spinner";
import {
  catchError,
  concatMap,
  forkJoin,
  from,
  map,
  mergeMap,
  of,
  switchMap,
} from "rxjs";
import { User } from "src/app/_models/models";
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
    // this.fetchBothApisWithConcatMapV2();
    // this.fetchBothApisWithMergeMapV2();
    // this.fetchDataWithErrorHandling();
    // this.fetchUserDetailsWithMergeMap();
    // this.fetchUserDetailsWithConcatMap();
    // this.fetchUserDetailsWithMergeMapV2();
    this.fetchUserDetailsWithSwitchMap();
  }



  fetchDataWithErrorHandling() {
    forkJoin({
      users: this.httpService.getUsers().pipe(
        catchError((err) => {
          console.error("Users API failed:", err);
          return of([]); // Fallback: Return empty array
        })
      ),
      posts: this.httpService.getPosts().pipe(
        catchError((err) => {
          console.error("Posts API failed:", err);
          return of([]); // Fallback: Return empty array
        })
      ),
      comments: this.httpService.getComments().pipe(
        catchError((err) => {
          console.error("Comments API failed:", err);
          return of([]); // Fallback: Return empty array
        })
      ),
    }).subscribe((response) => {
      console.log("Users:", response.users);
      console.log("Posts:", response.posts);
      console.log("Comments:", response.comments);
    });
  }

  /**
   * when you need to fetch multiple independent data points without waiting for one to finish.
   */
  fetchUserDetailsWithMergeMap() {
    from([1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]) // List of user IDs
      .pipe(
        mergeMap((id) => this.httpService.getUserById(id)),
        catchError((err) => {
          console.error("Posts API failed:", err);
          return of([]); // Fallback: Return empty array
        })
      )
      .subscribe({
        next: (user: User | null) => {
          if (user) {
            console.log("User Details:", user);
          } else {
            console.log("Skipping failed user request.");
          }
        },
        error: (error) => {
          console.error("Unexpected error: ", error);
        },
      });
  }

  /**
   * Calls APIs one user at a time.
    Ensures first API completes before calling the second API.
    Best for ordered execution (e.g., processing transactions).
   */
  fetchUserDetailsWithConcatMap() {
    from([1, 2, 3, 4, 5]) // User IDs
      .pipe(
        concatMap((id) => {
          console.log("fetching user info for userId: ", id);
          return this.httpService.getUserById(id).pipe(
            concatMap((user) =>
              this.httpService.getPostsForAUser(user.id).pipe(
                catchError((err) => {
                  console.error(`Posts for User ${user.id} failed:`, err);
                  return of([]); // Fallback: Empty posts list
                }),
                map((posts) => ({ user, posts })) // Combine user & posts
              )
            ),
            catchError((err) => {
              console.error(`User ${id} fetch failed:`, err);
              return of(null); // Skip this user
            })
          );
        })
      )
      .subscribe((result) => {
        if (result) console.log("User & Posts:", result);
      });
  }

  /**
   * Calls both APIs for all users in parallel.
    Best for independent requests (where order doesn’t matter).
    Faster than concatMap, but can overload the server.
  */
  fetchUserDetailsWithMergeMapV2() {
    from([1, 2, 3, 4, 5]) // User IDs
      .pipe(
        mergeMap((id) => {
          console.log("fetching user info for userId: ", id);
          return this.httpService.getUserById(id).pipe(
            mergeMap((user) =>
              this.httpService.getPostsForAUser(user.id).pipe(
                catchError(() => of([])),
                map((posts) => ({ user, posts }))
              )
            ),
            catchError(() => of(null))
          );
        })
      )
      .subscribe((result) => {
        if (result) console.log("User & Posts:", result);
      });
  }

  fetchUserDetailsWithSwitchMap() {
    from([1, 2, 3, 4, 5]) // User IDs
      .pipe(
        switchMap((id) => {
          console.log("fetching user info for userId: ", id);
          return this.httpService.getUserById(id).pipe(
            switchMap((user) => {
              return this.httpService.getPostsForAUser(user.id).pipe(
                catchError(() => of([])),
                map((posts) => ({ user, posts }))
              );
            }),
            catchError(() => of(null))
          );
        })
      )
      .subscribe((result) => {
        if (result) console.log("Latest User & Posts:", result);
      });
  }

  async fetchBothApisWithAsync() {
    try {
      console.log("fetching fetchBothApisWithAsync");
      this.data = await this.httpService.fetchBothApisWithAsync();
      console.log("fetchBothApisWithAsync: ", this.data);
      console.log("fetched fetchBothApisWithAsync");
    } catch (error) {
      console.error("Error in fetching data", error);
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
