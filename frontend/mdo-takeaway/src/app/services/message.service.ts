import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { Message } from '../modals/message';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  private messageSubject = new Subject<Message>();
  messages$ = this.messageSubject.asObservable();
  constructor() { }

  addError(text:string):void {
    this.messageSubject.next({text:text,type:"Error"});
  }

  addWarning(text: string):void {
    this.messageSubject.next({ type: 'Warning', text });
  }

}
