import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Student } from '../interfaces/student';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StudentService {

  baseStudentUrl = "http:localhost:8080/api/students"

  constructor(
    private httpClient:HttpClient
  ) { }
  
  registerStudent(student:Student) : Observable<Student> {
    return this.httpClient.post<Student>(this.baseStudentUrl, student);
  }

}
