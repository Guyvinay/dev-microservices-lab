import { createReducer, on } from '@ngrx/store';
import { increment, decrement, reset, incrementedBy } from 'src/app/store/state/counter.actions';
import { CounterState, initialState } from 'src/app/store/state/counter.state';
const currentState = {
    count: 0,
    anotherProperty: 'someValue'
};
const iniState = 0;
export const counterReducer = createReducer(

    iniState,
    on(increment, (s) => {
        // console.log("state ", s);
        // const s = {...state, count: state.count+1};
        // console.log("returning state ", s);
        return s+1;
    }),
    on(decrement, (s) => {
        // console.log("state ", s);
        // const s = {...state, count: state.count-1};
        // console.log("returning state ", s);
        return s-1;
    }),
    on(reset, (state) => {
        // console.log("state ", state);
        // const s = {...state, count: 0};
        // console.log("returning state ", s);
        return 0;
    }),
    on(incrementedBy, (state, { amount }) => {
        return state+amount;
    })
);