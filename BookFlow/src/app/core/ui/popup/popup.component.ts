import { Component,Inject,inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { MatDialogModule } from '@angular/material/dialog';

interface DialogData {
  title: string;
  message: string;
  type: 'success' | 'error';
}

@Component({
  selector: 'app-popup',
  standalone: true,
  imports: [CommonModule,MatDialogModule],
  templateUrl: './popup.component.html',
  styleUrl: './popup.component.css'
})




export class PopupComponent {

  constructor(
    public dialogRef: MatDialogRef<PopupComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData
  ) {}

  closeDialog(): void {
    this.dialogRef.close();
  }

}
