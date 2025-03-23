import { createFeatureSelector, createSelector } from '@ngrx/store';
import { UserState } from '../reducers/user.reducer';

// Feature Selector (Get the entire user state)
export const selectUserState = createFeatureSelector<UserState>('users');

// Select User List
export const selectAllUsers = createSelector(
  selectUserState,
  (state) => state.users
);

// Select Loading State
export const selectLoading = createSelector(
  selectUserState,
  (state) => state.loading
);

// Select Error State
export const selectError = createSelector(
  selectUserState,
  (state) => state.error
);
