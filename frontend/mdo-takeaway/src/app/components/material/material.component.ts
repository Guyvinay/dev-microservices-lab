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
      data: { message: 'This is a message from the parent component' } // Pass data
    });
  
    dialogRef.afterClosed().subscribe((result) => {
      console.log('Dialog result:', result); // Handle data returned from dialog
    });
  }
 
  openTableViewSettings(setting: string) {
    console.log(`Opening table view settings for: ${setting}`);
    // Add your logic to open the table view settings based on the provided setting
  }
  openCompDialog() {
    const myCompDialog = this.matDialog.open(MyDialogComponent, { data: {} });
    myCompDialog.afterClosed().subscribe((res) => {
      // Data back from dialog
      console.log({ res });
    });
  }
}
