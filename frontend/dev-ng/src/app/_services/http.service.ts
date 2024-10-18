import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { concatMap, from, map, mergeMap, Observable, reduce } from "rxjs";
import { Post, User } from "../_models/models";

@Injectable({
  providedIn: "root",
})
export class HttpService {
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

  fetchDogApi(): Observable<any> {
    return this.http.get("https://dog.ceo/api/breeds/image/random");
  }

  fetchRandomUser(breed: string): Observable<any> {
    return this.http.get(`https://randomuser.me/api/?seed=${breed}`);
  }

  async fetchDogApiAsync(): Promise<any> {
    return this.http.get("https://dog.ceo/api/breeds/image/random").toPromise();
  }
  async fetchRandomUserAsync(breed: string): Promise<any> {
    return this.http
      .get(`https://randomuser.me/api/?seed=${breed}`)
      .toPromise();
  }

  async fetchBothApisWithAsync(): Promise<any> {
    try {
      const dogApi = await this.fetchDogApiAsync();
      const breed = dogApi.message.split("/")[4];
      const userApi = await this.fetchRandomUserAsync(breed);
      return { dog: dogApi, user: userApi };
    } catch (error) {
      console.log(error);
      throw error;
    }
  }

  fetchBothApisWithConcatMap(): Observable<any> {
    return this.fetchDogApi().pipe(
      concatMap((dogResponse: any) => {
        const breed = dogResponse.message.split("/")[4];
        return this.fetchRandomUser(breed);
      })
    );
  }

  fetchBothApisWithMergeMap(): Observable<any> {
    return this.fetchDogApi().pipe(
      mergeMap((dogResponse: any) => {
        const breed = dogResponse.message.split("/")[4]; // Extract breed from the image URL
        return this.fetchRandomUser(breed);
      })
    );
  }

  fetchBothApisWithMergeMapV2(): Observable<any> {
    return this.fetchDogApi().pipe(
      mergeMap((dogResponse: any) => {
        const breed = dogResponse.message.split("/")[4]; // Extract breed from the image URL
        return this.fetchRandomUser(breed).pipe(
          map(userResponse=> {
            return {
              user: userResponse,
              dog: dogResponse
            }
          })
        );
      })
    );
  }

  fetchBothApisWithConcatMapV2(): Observable<any> {
    return this.fetchDogApi().pipe(
      concatMap((dogResponse: any) => {
        const breed = dogResponse.message.split("/")[4];
        return this.fetchRandomUser(breed).pipe(
          map((userResponse) => {
            return {
              user: userResponse,
              dog: dogResponse,
            };
          })
        );
      })
    );
  }
}
