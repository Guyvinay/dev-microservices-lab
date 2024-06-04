import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';


@Component({
  selector: 'app-my-dialog',
  templateUrl: './my-dialog.component.html',
  styleUrl: './my-dialog.component.scss'
})
export class MyDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<MyDialogComponent>, // Inject MatDialogRef
    @Inject(MAT_DIALOG_DATA) public data: any // Inject data from parent component
  ) {}

  // Add closing functionality (e.g., close button):
  onCloseClick(): void {
    this.dialogRef.close('Closed'); // Close dialog and optionally return data
  }
  closeDialog() { 
    this.dialogRef.close(); 
  }
}
