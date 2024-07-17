import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { UserData } from './UserData';
import { HtmlTagDefinition } from '@angular/compiler';
import { EMPTY, Subject, Subscription, debounceTime, distinctUntilChanged, of, switchMap } from 'rxjs';
import { UtilsComponent } from './components/utils/utils.component';
import { HttpClient } from '@angular/common/http';
import * as moment from 'moment';



@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  alertType:string='alert-success';
  @ViewChild(UtilsComponent) utilsComponent!:UtilsComponent;

  constructor(private http: HttpClient){}

  async fetchCurrentUTCMillis() {
    const tz = Intl.DateTimeFormat().resolvedOptions().timeZone;
    // try {
    //   const response = await fetch(`https://worldtimeapi.org/api/timezone/${tz}`);
    //   const data = await response.json();
    //   return moment.utc(data.utc_datetime).valueOf();
    // } catch (error) {
    //   console.error('Failed to fetch the current UTC time:', error);
    //   throw new Error('Failed to fetch the current UTC time:')
    // }
    this.http.get(`https://worldtimeapi.org/api/timezone/${tz}`).subscribe({
        next: (res: any) => {
          console.log(res);
          console.log(moment.utc(res.utc_datetime).valueOf());
          // Handle response data here
        },
        error: (error: any) => {
          console.log(error);
          // Handle error here
        }
      });
  }

  /*
  ngOnInit(): void {
    this.subscription = this.searchFieldSub.pipe(
      debounceTime(1300),
      distinctUntilChanged(),
      switchMap((searchStr)=>{
        console.log(searchStr);
        this.text = searchStr;
        return EMPTY;
      })
    ).subscribe(() => {});
  }
  */
  ngOnInit(): void {
    this.subscription = this.searchFieldSub.pipe(
      debounceTime(1300),
      distinctUntilChanged(),
      switchMap((searchStr)=> of(searchStr))
    ).subscribe((searchStr) => {
      console.log(searchStr);
      this.text = searchStr;
    });

    // this.fetchCurrentUTCMillis();
    this.do();
  }
  async do() {
    const time = await this.fetchCurrentUTCMillis();
    console.log(time)
  }
  
  subscription!: Subscription;
  title = 'mdo-takeaway';
  items = Array.from({length: 100000}).map((_, i) => `Item---> ${i}`);
  searchFieldSub: Subject<string> = new Subject();

  displayedColumns: string[] = ['name', 'age'];
  dataSource = new MatTableDataSource<UserData>([
    { name: 'Alice', age: 25 },
    { name: 'Bob', age: 30 },
  ]);

  

  text:string='';

  // search(event: string){
  //   this.text=event;
  // }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }


  callChildMethod():void{
    this.utilsComponent.inputValue="FROM parent component";
  }

  //EventEmitter

  openEvent(event:Event){
    console.log(event);
  }
  closeEvent(event:Event){
    console.log(event);
    
  }

  onCloseDrawer(){
    console.log("hii")
  }

  drawerOpened: boolean = false;

  toggleDrawer() {
    this.drawerOpened = !this.drawerOpened;
  }

  onDrawerClosed() {
    this.drawerOpened = false;
  }
  

}
