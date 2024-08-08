import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { HttpClient } from '@angular/common/http';
import { loadPosts, loadPostsError, loadPostsSuccess } from "../state/post.actions";
import { catchError, map, mergeMap, of, withLatestFrom } from "rxjs";
import { Store } from "@ngrx/store";
import { selectAllPosts } from "../state/posts.selector";

@Injectable()
export class PostEffects {

    constructor(
        private actions$ : Actions,
        private http : HttpClient,
        private store : Store
    ){}

    loadPosts$ = createEffect(() => {
        return this.actions$.pipe(
            ofType(loadPosts),
            withLatestFrom(this.store.select(selectAllPosts)),
            mergeMap(([action, posts]) => {
                if(posts.length>0) {
                    return of(loadPostsSuccess({posts}))
                } else {
                    return this.http.get<any[]>('https://jsonplaceholder.typicode.com/posts')
                    .pipe(
                        map((posts)=> loadPostsSuccess({posts})),
                        catchError((error)=> of(loadPostsError({error})))
                    )
                }
            })
        );
    });
    // loadPostsV2$ = createEffect(() => this.actions$.pipe(
    //     ofType(loadPosts),
    //     mergeMap(() => this.http.get<any[]>('https://jsonplaceholder.typicode.com/posts')
    //         .pipe(
    //             map((posts) => loadPostsSuccess({ posts })),
    //             catchError((error) => of(loadPostsError({ error })))
    //         ))
    // ));

}