import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RxjsComponent } from './components/rxjs/rxjs.component';
import { GitComponent } from './components/git/git.component';
import { HomeComponent } from './home/home.component';
import { ReassignmentListComponent } from './reassignment-list/reassignment-list.component';

const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  {
    path: 'home',
    component: HomeComponent,
    children: [
      { path: 'task/reassignment-list', component: ReassignmentListComponent },
    ],
  },
  { path: 'task/reassignment-list', component: ReassignmentListComponent },
  { path: 'rxjs', component: RxjsComponent },
  {path:'git', component:GitComponent}  // Wildcard route for a 404 page
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
