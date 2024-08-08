import { createFeatureSelector, createSelector } from "@ngrx/store";
import { PostsState } from "./posts.reducer";

export const createPostsState = createFeatureSelector<PostsState>('posts');

export const selectAllPosts = createSelector(
    createPostsState,
    (state: PostsState) => state.posts
)