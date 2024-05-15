import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, catchError } from 'rxjs';
import { User } from '../modals/user';

@Injectable({
  providedIn: 'root'
})
export class GithubService {

  private apiUrl = 'https://api.github.com/users';
  private userSubject:BehaviorSubject<User> = new BehaviorSubject<User>({avatar_url:"",bio:"",email:"",login:"",name:"",node_id:""});

  constructor(private http: HttpClient) {}

  getUser(username: string): Observable<any> {
    const url = `${this.apiUrl}/${username}`;
    return this.http.get<any>(url).pipe(
      catchError(error => {
        console.error('Error fetching user:', error);
        throw 'Error fetching user';
      })
    );
  }
  getUserSubject():Observable<any>{
    return this.userSubject;
  }
  setUserSubject(user:any):void{
    this.userSubject.next(user);
  }
}
