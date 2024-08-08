import { createFeatureSelector, createSelector } from "@ngrx/store";

export interface CounterState {
    count: number;
}

export const initialState: CounterState = {
    count: 0,
}

export const selectCounterState = createFeatureSelector<CounterState>('counterSelector');
export const counterSelect = createSelector(
    selectCounterState,
    (state: CounterState) => state.count
)