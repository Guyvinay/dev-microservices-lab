import { Component } from "@angular/core";
import { Store } from "@ngrx/store";
import {
  selectAllUsers,
  selectLoading,
  selectError,
} from "../selectors/user.selectors";
import { loadUsers } from "../actions/user.actions";
import { filter, take, withLatestFrom } from "rxjs";

@Component({
  selector: "store-usage",
  templateUrl: "./store.usage.component.html",
  styleUrls: ["./store.usage.component.scss"],
})
export class StoreUsageComponent {
  users$ = this.store.select(selectAllUsers);
  loading$ = this.store.select(selectLoading);
  error$ = this.store.select(selectError);
  constructor(private store: Store) {}

  fetchUsers() {
    this.users$
      .pipe(
        take(1),
        withLatestFrom(this.loading$),
        filter(([users, loading]) => {
          return users.length === 0 && !loading;
        })
      )
      .subscribe(() => {
        return this.store.dispatch(loadUsers());
      });
  }
}
