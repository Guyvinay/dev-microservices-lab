import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MyDialogComponent } from '../my-dialog/my-dialog.component';

@Component({
  selector: 'app-material',
  templateUrl: './material.component.html',
  styleUrl: './material.component.scss'
})
export class MaterialComponent implements OnInit {
  constructor(
    private matDialog : MatDialog , 
  ){}
  ngOnInit(): void {
    // this.openDialog();
  }
  openNewConnectionFlow() {
    console.log('Opening new connection flow');
    // Add your logic to open the new connection flow
  }

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
 
  openTableViewSettings(setting: string) {
    console.log(`Opening table view settings for: ${setting}`);
  }

  openCompDialog() {

    const myCompDialog = this.matDialog.open(MyDialogComponent, {
      height: '200px',
      width: '400px',
      data: {message:'Sending dialog message'} 
    });

    myCompDialog.afterClosed().subscribe((res) => {
      console.log(res);
    });

  }
}
