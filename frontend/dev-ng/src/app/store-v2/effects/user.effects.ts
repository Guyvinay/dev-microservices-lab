import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import * as UserActions from "../actions/user.actions";
import { map, mergeMap } from "rxjs";
import { HttpService } from "src/app/_services/http.service";

@Injectable()
export class UserEffects {
  constructor(private action$: Actions, private httpService: HttpService) {}

  loadUsers$ = createEffect(() => {
    return this.action$.pipe(
      ofType(UserActions.loadUsers),
      mergeMap(() => {
        return this.httpService
          .getUsers()
          .pipe(map((users) => UserActions.loadUserSuccess({ users })));
      })
    );
  });
}
