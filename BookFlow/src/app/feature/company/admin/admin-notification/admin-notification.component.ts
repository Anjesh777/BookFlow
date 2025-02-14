import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators,ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { BookflowService } from '../../../../core/auth/service/BookFlow-Service/bookflow.service';
import { NotificationData,NotificationDataResponse } from '../../../../core/auth/model/bookflow';
import { TimeAgoPipe } from '../../../../core/pipe/shared/pipes/time-ago.pipe';
import { CommentEditDialogComponent } from '../../../../core/ui/popup/comment-edit-dialog/comment-edit-dialog.component';
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
  templateUrl: './admin-notification.component.html',
  styleUrl: './admin-notification.component.css'
})
export class AdminNotificationComponent implements OnInit{

  http = inject(HttpClient);
  messageList: NotificationDataResponse[] = [];

  notificationForm: FormGroup;

  targetAudienceOptions = [
    { value: 'All Users', label: 'All Users' },
    { value: 'Admins', label: 'Company Admins' },
    { value: 'Users', label: 'Company Users' },
    { value: 'Manager', label: 'Company Manager'}
  ];

  constructor(
    private fb: FormBuilder,
    //private adminNotificationService: AdminNotificationService,
    public dialog: MatDialog
  ) {
    this.notificationForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      message: ['', [Validators.required, Validators.minLength(10)]],
      targetAudience: ['All Users', Validators.required],
      notificationType: ['INFORMATION', Validators.required]
    });
  }
  ngOnInit(): void {

  }




  openEditDialog(notification: NotificationDataResponse) {
    const dialogRef = this.dialog.open(CommentEditDialogComponent, {
      width: '500px',
      data: notification
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
      //  this.getAllNotifications();
      }
    });
  
  }

  onSubmit() {
    if (this.notificationForm.valid) {
      const formData: NotificationData = {
        title: this.notificationForm.get('title')?.value,
        message: this.notificationForm.get('message')?.value,
        targetAudience: this.notificationForm.get('targetAudience')?.value,
        notificationType: this.notificationForm.get('notificationType')?.value
      };

      console.log(this.notificationForm.value)
      
    //   this.adminNotificationService.pushNotification(formData).subscribe({
    //     next: (response) => {
    //       console.log('Notification Sent:', response);
    //       this.notificationForm.reset();
    //       this.getAllNotifications();
    //     },
    //     error: (error) => {
    //       console.error('Error:', error);
    //     }
    //   });
    // } else {
    //   this.markFormGroupTouched(this.notificationForm);
    // }
       }
  }









}
