import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Service, serviceFilter } from '../../../../core/auth/model/admin';
import { AddServiceDialogComponent } from '../../../../core/ui/popup/add-service-dialog/add-service-dialog.component';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { UiServiceService } from '../../../../core/ui/ui-service.service';
import { AdminService } from '../../../../core/auth/service/Admin-Service/admin.service';
import { EditServiceDilogComponent } from '../../../../core/ui/popup/edit-service-dilog/edit-service-dilog.component';

@Component({
  selector: 'app-service-management',
  standalone: true,
  imports: [CommonModule,FormsModule],
  templateUrl: './service-management.component.html',
  styleUrl: './service-management.component.css'
})
export class ServiceManagementComponent implements OnInit {
  loading: boolean = false;
  service: Service[] = [];
  error: string | null = null;
  searchQuery: string = '';
  selectedStatus: boolean | null = null;


  constructor(
    private uiService: UiServiceService,
    public dialog: MatDialog,
    private fb: FormBuilder,
    private adminService: AdminService,
  ) {}

  ngOnInit(): void {
    this.getAllServices();
  }

  getAllServices() {
    this.loading = true;
    this.error = null;

    this.adminService.getService().subscribe({
      next: (response) => {
        this.service = response;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error fetching services', error);
        this.error = 'Failed to load services. Please try again.';
        this.loading = false;
      },
    });
  }

  openAddDialog(service?: Service) {
    const dialogRef = this.dialog.open(AddServiceDialogComponent, {
      width: '600px',
      data: service || {}
    });
    
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loading = true;
        this.adminService.addService(result).subscribe({
          next: (response) => {
            console.log('Service added successfully:', response);
            this.uiService.showSuccessDialog('Service added successfully');
            this.getAllServices(); // Refresh the list
            this.loading = false;
          },
          error: (error) => {
            console.error('Error adding service:', error);
            this.uiService.showErrorDialog('Failed to add service');
            this.loading = false;
          },
        });
      }
    });
  }


  openEditDialog(user: Service) {
    const dialogRef = this.dialog.open(EditServiceDilogComponent, {
      width: '600px',
      data: user
    });
    
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.getAllServices(); 
      }
    });
  }

  deleteService(id: string) {
    if (confirm('Are you sure you want to delete this service?')) {
      this.loading = true;
      this.adminService.deleteService(id).subscribe({
        next: (): void => {
          this.uiService.showSuccessDialog('Service deleted successfully');
          this.getAllServices(); // Refresh the list
          this.loading = false;
        },
        error: (error: any): void => {
          console.error('Error deleting service:', error);
          this.uiService.showErrorDialog('Failed to delete service');
          this.loading = false;
        }
      });
     }
  }

  applyFilters() {
    this.loading = true;
    this.error = null;
    
    const filter: serviceFilter = {
      serchService: this.searchQuery.trim(),
      filter: this.selectedStatus ?? null 
    };

    this.adminService.getServiceFilterData(filter).subscribe({
      next: (response) => {
        this.service = response;
        this.loading = false;
        console.log("Filtered data received:", response);
      },
      error: (error) => {
        console.error('Error applying filters:', error);
        this.error = 'Failed to apply filters. Please try again.';
        this.loading = false;
      }
    });
  }


}
