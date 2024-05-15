import { Component, Input, OnInit, input } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';

@Component({
  selector: 'app-popup',
  templateUrl: './popup.component.html',
  styleUrl: './popup.component.scss'
})
export class PopupComponent implements OnInit {

  @Input() message:string="Message shows";
  constructor(private snackBar: MatSnackBar) {
    
   }
  ngOnInit(): void {
    this.showMessage()
  }

  showMessage(): void {
    const config: MatSnackBarConfig = {
      duration: 2000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
      panelClass: ['custom-snackbar'] // Custom CSS class for styling
    };
    this.snackBar.open(this.message, 'Close', config);

    // this.snackBar.open(this.message, 'Close', {
    //   duration: 3000, 
    //   horizontalPosition: 'left',
    //   verticalPosition: 'bottom'
    // });
  }

  showMessagePopup(message: string): void {
    const messagePopupRef = this.snackBar.open(message, 'Close', {
      duration: 2000, // Duration in milliseconds
      horizontalPosition: 'center',
      verticalPosition: 'bottom'
    });
  }

}
