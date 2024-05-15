import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-reassignment-list',
  templateUrl: './reassignment-list.component.html',
  styleUrl: './reassignment-list.component.scss'
})
export class ReassignmentListComponent implements OnInit {
  decodedFilter : any;
  constructor(
    private activatedRoute : ActivatedRoute
  ){}
  ngOnInit(): void {
    const encoded = this.activatedRoute.snapshot.queryParams['f'];
    if(encoded){
      this.decodedFilter = JSON.parse(atob(encoded))
    }
  }


}
