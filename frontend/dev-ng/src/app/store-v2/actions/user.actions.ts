import { createAction, props } from "@ngrx/store";
import { User } from "src/app/_models/models";

export const loadUsers = createAction("[User] Load User");

export const loadUserSuccess = createAction(
  "[User] Load users Suceess",
  props<{ users: User[] }>()
);

export const loadUserFailure = createAction(
  "[User] Load users failed",
  props<{ error: string }>()
);
