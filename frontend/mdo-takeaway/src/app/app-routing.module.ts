import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RxjsComponent } from './components/rxjs/rxjs.component';
import { GitComponent } from './components/git/git.component';
import { HomeComponent } from './home/home.component';
import { ReassignmentListComponent } from './reassignment-list/reassignment-list.component';
import { MaterialComponent } from './components/material/material.component';
import { UtilsComponent } from './components/utils/utils.component';

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
  {path:'material', component:MaterialComponent},
  {path:'utils', component:UtilsComponent},
  {path:'git', component:GitComponent}  // Wildcard route for a 404 page
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
