import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { concatMap, from, map, Observable, reduce } from "rxjs";
import { Post, User } from "../_models/models";

@Injectable({
  providedIn: "root",
})
export class UserService {
  private usersUrl = "https://jsonplaceholder.typicode.com/users";
  private postsUrl = "https://jsonplaceholder.typicode.com/posts";

  constructor(private http: HttpClient) {}

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.usersUrl);
  }

  getPostsForAUser(userId: number): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.usersUrl}?userId=${userId}`);
  }

  fetchUserPosts(): Observable<{ user: User; posts: Post[] }[]> {
    return this.getUsers().pipe(
      concatMap((users) => {
        return from(users).pipe(
          concatMap((user) => {
            return this.getPostsForAUser(user.id).pipe(
              map((posts) => {
                return {
                  user: user,
                  posts: posts,
                };
              })
            );
          })
        );
      }),
      reduce((acc, userPost) => acc.concat(userPost), [])
    );
  }
}
