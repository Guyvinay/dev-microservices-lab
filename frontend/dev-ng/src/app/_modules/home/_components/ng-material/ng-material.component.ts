import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { DateAdapter } from '@angular/material/core';
import { User } from 'src/app/_models/models';
import { CustomDataSource } from 'src/app/_services/CustomeDataSource';

@Component({
  selector: 'app-ng-material',
  templateUrl: './ng-material.component.html',
  styleUrls: ['./ng-material.component.scss']
})
export class NgMaterialComponent implements OnInit {


  displayedColumns: string[] = [];

  dataSource: CustomDataSource;

  constructor(
    private http: HttpClient
  ) { }

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.http.get<User[]>('https://jsonplaceholder.typicode.com/users')
    .subscribe(
      (response: User[])=> {
        this.dataSource = new CustomDataSource(response);
        console.log("response ",response);
        if (response.length > 0) {
          console.log("this.displayedColumns ",this.displayedColumns);
          this.displayedColumns = Object.keys(response[0]);
        }
      }
    )
  }

}
