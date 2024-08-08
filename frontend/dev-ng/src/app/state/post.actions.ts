import { createAction, props } from "@ngrx/store";

export const loadPosts = createAction('[Posts] Load Posts');

export const loadPostsSuccess = createAction(
    '[Posts] Loads Posts Success',
    props<{posts: any[]}>()
)
// export const load