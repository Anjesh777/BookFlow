import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { NotificationData, NotificationDataResponse } from '../../../../core/auth/model/bookflow';
import { TimeAgoPipe } from '../../../../core/pipe/shared/pipes/time-ago.pipe';
import { MatDialog } from '@angular/material/dialog';
import { AdminService } from '../../../../core/auth/service/Admin-Service/admin.service';
import { AdminNotficationEditDilogComponent } from '../../../../core/ui/popup/admin-notfication-edit-dilog/admin-notfication-edit-dilog.component';


@Component({
  selector: 'app-bookflow-notification',
  standalone: true,
  imports: [
    MatIconModule,
    ReactiveFormsModule,
    CommonModule,
    TimeAgoPipe,
  ],
  templateUrl: './admin-notification.component.html',
  styleUrls: ['./admin-notification.component.css']  // Corrected here
})
export class AdminNotificationComponent implements OnInit {
  http = inject(HttpClient);
  messageList: NotificationDataResponse[] = [];
  

  adminNotificationService = inject(AdminService);

  notificationForm: FormGroup;

  targetAudienceOptions = [
    { value: 'Users', label: 'All Users' },     
    { value: 'Admins', label: 'Company Admins' }, 
    { value: 'Users', label: 'Company Users' }
];
  
  notificationTypes = [
    { value: 'INFORMATION', label: 'Information', color: 'text-blue-600' },  
    { value: 'SUCCESS', label: 'Success', color: 'text-green-600' },
    { value: 'WARNING', label: 'Warning', color: 'text-yellow-600' },
    { value: 'ERROR', label: 'Error', color: 'text-red-600' }
  ];


  constructor(
    private fb: FormBuilder,
    public dialog: MatDialog
  ) {
    this.notificationForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      message: ['', [Validators.required, Validators.minLength(10)]],
      targetAudience: ['Users', Validators.required],
      notificationType: ['INFORMATION', Validators.required]
    });
  }
  
  ngOnInit(): void {

    this.getAllComment();

  }

  openEditDialog(notification: NotificationDataResponse) {
    const dialogRef = this.dialog.open(AdminNotficationEditDilogComponent, {
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

      this.adminNotificationService.getNotificationAdmin()
      .subscribe({
        next:(response) =>{
          this.messageList = response
          console.log(this.messageList)
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

      console.log(this.notificationForm.value);
      
      this.adminNotificationService.pushNotification(formData).subscribe({
        next: (response) => {
          console.log('Notification Sent:', response);

          this.notificationForm.reset();
          this.getAllComment()

        },
        error: (error) => {
          console.error('Error:', error);
        }
      });
    } else {
      this.markFormGroupTouched(this.notificationForm);
    }
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

  deleteNotification(id:number){

    this.adminNotificationService.deleteNotification(id).subscribe({
      next: () => {
        console.log('Notification deleted successfully');
        this.getAllComment(); 
      },
      error: (error) => {
        console.error('Error in deletion:', error);
      }
    });

  }

  


}
