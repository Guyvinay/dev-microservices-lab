import { createReducer, on } from "@ngrx/store";
import { User } from "src/app/_models/models";
import { loadUserFailure, loadUsers, loadUserSuccess } from "../actions/user.actions";

// Define User State
export interface UserState {
  users: User[];
  loading: boolean;
  error: string | null;
}

// Initial State
export const initialState: UserState = {
  users: [],
  loading: false,
  error: null,
};

export const userReducer = createReducer(
  initialState,
  on(loadUsers, (state) => ({
    ...state,
    loading: true,
    error: null,
  })),
  on(loadUserSuccess, (state, { users }) => ({
    ...state,
    users,
    loading: false,
    error: null,
  })),
  on(loadUserFailure, (state, {error})=> ({
    ...state,
    loading: false,
    error
  }))
);
