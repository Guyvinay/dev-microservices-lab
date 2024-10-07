import { CollectionViewer, DataSource } from "@angular/cdk/collections";
import { BehaviorSubject, Observable } from "rxjs";
import { User } from "../_models/models";

export class CustomDataSource extends DataSource<User> {

    private dataSubject = new BehaviorSubject<User[]>([]);

    constructor(initialData: User[]) {
        super();
        this.dataSubject.next(initialData);
    }

    connect(): Observable<User[]> {
        return this.dataSubject.asObservable();
    }

    disconnect(collectionViewer: CollectionViewer): void {
        this.dataSubject.complete();
    }

    updateData(data: User[]): void {
        this.dataSubject.next(data);
      }

}