import { BrowserModule } from "@angular/platform-browser";
import { NgModule } from "@angular/core";

import { StoreModule } from "@ngrx/store";
import { AppComponent } from "./app.component";
import { counterReducer } from "./store/state/counter.reducer";
import { CounterComponent } from "./counter/counter.component";
import { StoreDevtoolsModule } from "@ngrx/store-devtools";
import { PostComponent } from "./posts/post.component";
import { EffectsModule } from "@ngrx/effects";
import { PostEffects } from "./store/effects/post.effects";
import { postsReducer } from "./store/state/posts.reducer";
import { HttpClientModule } from "@angular/common/http";
import { RxjsComponent } from "./_modules/home/_components/rxjs/rxjs.component";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { NgMaterialComponent } from "./_modules/home/_components/ng-material/ng-material.component";
import { MatTableModule } from "@angular/material/table"; // Import MatTableModule
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatTabsModule } from "@angular/material/tabs";
import { MatMenuModule } from "@angular/material/menu";
import { MatTreeModule } from '@angular/material/tree';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTableComponent } from "./_modules/home/_components/ng-material/mat-table/mat.table.component";
import { userReducer } from "./store-v2/reducers/user.reducer";
import { UserEffects } from "./store-v2/effects/user.effects";
import { StoreUsageComponent } from "./store-v2/usage/store.usage.component";

@NgModule({
  declarations: [
    AppComponent,
    CounterComponent,
    PostComponent,
    RxjsComponent,
    StoreUsageComponent,
    NgMaterialComponent,
    MatTableComponent
  ],
  imports: [
    BrowserModule,
    MatProgressSpinnerModule,
    BrowserAnimationsModule,
    MatTableModule, // Add MatTableModule here
    MatTabsModule,
    MatPaginatorModule,
    MatMenuModule,
    HttpClientModule,
    MatTreeModule,
    MatIconModule,
    MatButtonModule,
    StoreModule.forRoot({ count: counterReducer, posts: postsReducer, users: userReducer }),
    EffectsModule.forRoot([PostEffects, UserEffects]),
    StoreDevtoolsModule.instrument({ maxAge: 25 }),
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}

/*
Use of this source code is governed by an MIT-style license that
can be found in the LICENSE file at https://github.com/ngrx/platform
*/
