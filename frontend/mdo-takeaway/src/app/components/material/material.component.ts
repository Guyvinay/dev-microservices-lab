import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MyDialogComponent } from '../my-dialog/my-dialog.component';

export interface PeriodicElement {
  name: string;
  position: number;
  weight: number;
  symbol: string;
}

const ELEMENT_DATA: PeriodicElement[] = [
  {position: 1, name: 'Hydrogen', weight: 1.0079, symbol: 'H'},
  {position: 2, name: 'Helium', weight: 4.0026, symbol: 'He'},
  {position: 3, name: 'Lithium', weight: 6.941, symbol: 'Li'},
  {position: 4, name: 'Beryllium', weight: 9.0122, symbol: 'Be'},
  {position: 5, name: 'Boron', weight: 10.811, symbol: 'B'},
  {position: 6, name: 'Carbon', weight: 12.0107, symbol: 'C'},
  {position: 7, name: 'Nitrogen', weight: 14.0067, symbol: 'N'},
  {position: 8, name: 'Oxygen', weight: 15.9994, symbol: 'O'},
  {position: 9, name: 'Fluorine', weight: 18.9984, symbol: 'F'},
  {position: 10, name: 'Neon', weight: 20.1797, symbol: 'Ne'},
];


@Component({
  selector: 'app-material',
  templateUrl: './material.component.html',
  styleUrl: './material.component.scss'
})
export class MaterialComponent implements OnInit {

  constructor(
    private matDialog: MatDialog,
  ) { }
  
  ngOnInit(): void {
    // this.openDialog();
  }
  
  //Material Menu 
  openNewConnectionFlow() {
    console.log('Opening new connection flow');
  }
  openTableViewSettings(setting: string) {
    console.log(`Opening table view settings for: ${setting}`);
  }

  //Open Dialog 
  openDialog() {

    const dialogRef = this.matDialog.open(MyDialogComponent, {
      height: '200px',
      width: '400px',
      data: { message: 'Sending dialog messgae' },
    });

    dialogRef.afterClosed().subscribe((result) => {
      console.log('Dialog result:', result); // Handle data returned from dialog
    });
  }

  //Material Table
  displayedColumns: string[] = ['position', 'name', 'weight', 'symbol'];
  dataSource = ELEMENT_DATA;

  

}
