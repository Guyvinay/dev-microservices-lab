import { createAction, props } from "@ngrx/store";

export const loadPosts = createAction('[Posts] Load Posts');

export const loadPostsSuccess = createAction(
    '[Posts] Load Posts Success',
    props<{posts: any[]}>()
);

export const loadPostsError = createAction(
    '[Posts] Load Posts Error',
    props<{error: any}>()
);