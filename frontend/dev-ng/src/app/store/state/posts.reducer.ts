import { createReducer, on } from "@ngrx/store";
import { loadPostsError, loadPostsSuccess } from "./post.actions";

export interface PostsState {
    posts: any[]
}

export const initialPosts: PostsState = {
    posts : []
}

const _postReducer = createReducer(
    initialPosts,
    on(
        loadPostsSuccess, (state, {posts}) => ({
            ...state,
            posts
        })
    ),
    on(
        loadPostsError,
        (state, {error}) => ({...state, error})
    )
);

export function postsReducer(state, action) {
    return _postReducer(state, action);
}
