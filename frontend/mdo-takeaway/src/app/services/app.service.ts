import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AppService {

  private apiURL = 'https://api.github.com/users';

  constructor(
    private http : HttpClient
  ) { }

  getUserInfo(username:string): Observable<any> {
    return this.http.get<any>(`${this.apiURL}/${username}`);
  }

  downloadZipFile(value:string) {
    return this.http.get(`http://localhost:8080/files/${value}`, {responseType: 'arraybuffer'});
  }

}
