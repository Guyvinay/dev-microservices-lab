import { Component } from "@angular/core";
import { Store } from "@ngrx/store";
import { Observable } from "rxjs";
import { decrement, increment, incrementedBy, reset } from "../state/counter.actions";
import { counterSelect } from "../state/counter.state";

interface AppStore {
    count: number,
    statue : string
}

@Component({
    selector: 'counter-component',
    templateUrl: './counter.component.html'
})
export class CounterComponent {

    count$: Observable<number>

    constructor(private store: Store<AppStore>) {
        // console.log(store.select('statue'));
        this.count$ = store.select((sel)=>{
            // console.log("select ",sel);
            return sel.count;
        });
        const slctCounter = this.store.select(counterSelect);
        console.log("selectCounter ", slctCounter);

        // store.select('count').subscribe(val=>console.log("count cosntructor ",val))
        // console.log(store.select('count'));
    }
    increment() {
        const inc = increment();
        this.store.dispatch(inc);
        // console.log("increament",inc);
        // console.log("count ", this.count$);
    }
    incrementedBy() {
        this.store.dispatch(incrementedBy({amount:10}));
    }

    decrement() {
        this.store.dispatch(decrement());
    }

    reset() {
        this.store.dispatch(reset());
    }
}