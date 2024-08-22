import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Observable, filter, forkJoin, fromEvent, interval, map, of, pipe } from 'rxjs';
import { MessageService } from '../../services/message.service';
import { Message } from '../../modals/message';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-rxjs',
  templateUrl: './rxjs.component.html',
  styleUrl: './rxjs.component.scss'
})
export class RxjsComponent implements OnInit {

  message$!:Observable<Message>;
  error$!:Observable<any>;
  warning$!:Observable<Message>;
  visible=true;
  @Output() open = new EventEmitter<any>();
  @Output() close = new EventEmitter<any>();


  time = new Observable<string>(observer => {
    setInterval(() => observer.next(new Date().toString()), 1000);
  });


  ngOnInit(): void {
      // this.counterFunction();
      // this.pipFunction();
      forkJoin(this.sources).subscribe(console.log);

  }

  counterFunction(){
    const nums = of(1, 2, 3);
      // Create an Observable that will publish a value on an interval
      const secondsCounter = interval(1000);
      // Subscribe to begin publishing values
      // const counterSubscription = secondsCounter.subscribe(n =>
      //   console.log(`It's been ${n + 1} seconds since subscribing!`));
  }

  pipFunction():void{
    const squareOdd = of(1, 2, 3, 4, 5).pipe(
                            filter(n => n % 2 === 1),
                            map(n => n * n)
                          );

              // Subscribe to get values
              squareOdd.subscribe(x => console.log(x));
              const nums = of(1, 2, 3, 4, 5);

              // Create a function that accepts an Observable.
              const squareOddVals = pipe(
                filter((n: number) => n % 2 === 1),
                map(n => n * n)
              );

              // Create an Observable that will run the filter and map functions
              const squareOdds = squareOddVals(nums);

              // Subscribe to run the combined functions
              squareOdds.subscribe(x => console.log(x));
  }
  constructor(
    private messageService:MessageService,
    private http: HttpClient
  ){
    this.message$ = messageService.messages$;

    this.error$ = this.message$.pipe(
      filter((e)=>e.type==='Error'),
      map(e=>e.text)
    )
  }

  addError(error:string):void{
    this.addError(error);
  }

  addWarning(warning:string):void{
    this.addWarning(warning);
  }



  toggle(){
    this.visible = !this.visible;
    if(this.visible){
      this.open.emit("Open Event");
    }else{
      this.close.emit("Closed Event");
    }
    
  }
  sources = [
    this.http.get('https://api.github.com/users/google'),
    this.http.get('https://api.github.com/users/microsoft'),
    this.http.get('https://api.github.com/users')
  ];


}
