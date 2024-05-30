import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { endWith } from 'rxjs';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {
  filters = {name:''}
  constructor(
    private router : Router
  ){}

  navigateToReassignmentList(){
    const encode = btoa(JSON.stringify(this.filters));
    this.router.navigate(['task/reassignment-list'],
    {
      queryParams:{f:encode},
      queryParamsHandling:'merge'
    });
  }
  navigateToAngMaterial() {
    this.router.navigate(['material']);
  }
}
