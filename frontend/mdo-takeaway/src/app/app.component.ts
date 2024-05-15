import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { UserData } from './UserData';
import { HtmlTagDefinition } from '@angular/compiler';
import { EMPTY, Subject, Subscription, debounceTime, distinctUntilChanged, of, switchMap } from 'rxjs';
import { UtilsComponent } from './components/utils/utils.component';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  alertType:string='alert-success';
  @ViewChild(UtilsComponent) utilsComponent!:UtilsComponent;


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
