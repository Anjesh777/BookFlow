import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { PopupComponent } from './popup/popup.component';

@Injectable({
  providedIn: 'root'
})
export class UiServiceService {

  constructor(private dialog: MatDialog) {}

  showPassword = false;
  showConfirmPassword = false;

  toggleShowPassword(): void {
    this.showPassword = !this.showPassword;
  }

  toggleShowConfirmPassword(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  showSuccessDialog(message: string) {
    const dialogRef = this.dialog.open(PopupComponent, {
      width: '300px',
      disableClose: false,
      hasBackdrop: true,
      data: { 
        title: 'Successfully accepted!',
        message: message,
        type: 'success'
      }
    });

    return dialogRef.afterClosed();
  }

  showErrorDialog(message: string) {
    const dialogRef = this.dialog.open(PopupComponent, {
      width: '300px',
      data: { 
        title: 'Error',
        message: message,
        type: 'error'
      }
    });

    return dialogRef.afterClosed();
  }
}