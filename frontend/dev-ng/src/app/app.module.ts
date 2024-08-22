import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import {StoreModule} from '@ngrx/store'
import { AppComponent } from './app.component';
import { counterReducer } from './store/state/counter.reducer';
import { CounterComponent } from './counter/counter.component';
import { StoreDevtoolsModule } from '@ngrx/store-devtools'
import { PostComponent } from './posts/post.component';
import { EffectsModule } from '@ngrx/effects';
import { PostEffects } from './store/effects/post.effects';
import { postsReducer } from './store/state/posts.reducer';
import { HttpClientModule } from '@angular/common/http';
import { RxjsComponent } from './_modules/home/_components/rxjs/rxjs.component';

@NgModule({
  declarations: [AppComponent, CounterComponent, PostComponent, RxjsComponent],
  imports: [
    BrowserModule,
    HttpClientModule,
    StoreModule.forRoot({count: counterReducer, posts: postsReducer}),
    EffectsModule.forRoot([PostEffects]),
    StoreDevtoolsModule.instrument({maxAge: 25}),
  ],
  providers: [
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}


/*
Use of this source code is governed by an MIT-style license that
can be found in the LICENSE file at https://github.com/ngrx/platform
*/