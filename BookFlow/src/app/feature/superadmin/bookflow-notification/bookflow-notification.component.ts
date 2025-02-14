import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators,ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { BookflowService } from '../../../core/auth/service/BookFlow-Service/bookflow.service';
import { NotificationData, NotificationDataResponse } from '../../../core/auth/model/bookflow';
import { TimeAgoPipe } from '../../../core/pipe/shared/pipes/time-ago.pipe';
import { CommentEditDialogComponent } from '../../../core/ui/popup/comment-edit-dialog/comment-edit-dialog.component';
import { MatDialog } from '@angular/material/dialog';


@Component({
  selector: 'app-bookflow-notification',
  standalone: true,
  imports: [
    MatIconModule,
    ReactiveFormsModule,
    CommonModule,
    TimeAgoPipe
  ],
  templateUrl: './bookflow-notification.component.html',
  styleUrl: './bookflow-notification.component.css'
})
export class BookflowNotificationComponent implements OnInit  {

  http = inject(HttpClient);
  messageList:NotificationDataResponse[] =[]
  

  notificationForm: FormGroup;

  targetAudienceOptions = [
    { value: 'All User', label: 'All Users' },      
    { value: 'Admin', label: 'Company Admins' },
    { value: 'User', label: 'Company Users' },
  ];

  notificationTypes = [
    { value: 'INFORMATION', label: 'Information', color: 'text-blue-600' },  // Updated values
    { value: 'SUCCESS', label: 'Success', color: 'text-green-600' },
    { value: 'WARNING', label: 'Warning', color: 'text-yellow-600' },
    { value: 'ERROR', label: 'Error', color: 'text-red-600' }
  ];

  constructor(
    private fb: FormBuilder,
    private bookflowService: BookflowService,
    public dialog:MatDialog
  ) {
    this.notificationForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      message: ['', [Validators.required, Validators.minLength(10)]],
      targetAudience: ['ALL_USERS', Validators.required],
      notificationType: ['INFORMATION', Validators.required]
    });
    
  }
  ngOnInit(): void {

    this.getAllComment();
    this.getAllThreeComment();
  }


  openEditDialog(notification: NotificationDataResponse) {
    const dialogRef = this.dialog.open(CommentEditDialogComponent, {
      width: '500px',
      data: notification
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.getAllComment();
      }
    });
  }


  getAllComment(){

    setTimeout(() =>{

      this.bookflowService.getAllNotification()
        .subscribe({
          next:(response) =>{
            this.messageList=response;
            console.log(this.messageList)
          },
          error:(error) =>{
            console.log('Error', error)
          }


        })
    })
    
  }

  getAllThreeComment(){

    setTimeout(() =>{

      this.bookflowService.getThreeNotification()
        .subscribe({
          next:(response) =>{
            this.messageList=response;
            console.log("message is "+this.messageList)
          },
          error:(error) =>{
            console.log('Error', error)
          }


        })
    })
    
  }




  onSubmit() {
    if (this.notificationForm.valid) {
      const formData: NotificationData = {
        title: this.notificationForm.get('title')?.value,
        message: this.notificationForm.get('message')?.value,
        targetAudience: this.notificationForm.get('targetAudience')?.value,
        notificationType: this.notificationForm.get('notificationType')?.value
      };

      this.bookflowService.pushNotification(formData).subscribe({
        next: (response) => {
          console.log('Success:', response);
          this.notificationForm.reset();
          this.getAllComment();
        },
        error: (error) => {
          console.error('Error details:', error);
        }
      });
    } else {
      this.markFormGroupTouched(this.notificationForm);
    }
  }

  deleteNotification(id:number){

    console.log("Meee")
    this.bookflowService.deleteNotification(id).subscribe({
      next: () => {
        console.log('Notification deleted successfully');
        this.getAllComment(); 
      },
      error: (error) => {
        console.error('Error in deletion:', error);
      }
    });

  }

  private markFormGroupTouched(formGroup: FormGroup) {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

    isFieldInvalid(fieldName: string): boolean {
      const field = this.notificationForm.get(fieldName);
      return field ? field.invalid && (field.dirty || field.touched) : false;
    }






}


