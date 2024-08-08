import { Component, ComponentRef } from "@angular/core";
import { Store } from "@ngrx/store";
import { Observable } from "rxjs";
import { selectAllPosts } from "../store/state/posts.selector";
import { loadPosts } from "../store/state/post.actions";

@Component({
    selector : 'post-component',
    templateUrl : './post.component.html'
})
export class PostComponent {

    posts$: Observable<any[]>;

    constructor(private store: Store){
        this.posts$ = store.select(selectAllPosts);
        console.log("posts ",this.posts$)
    }
    load() {
        this.store.dispatch(loadPosts());
    }
}

