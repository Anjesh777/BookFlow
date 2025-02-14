// slidebar.component.ts
import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../auth/service/auth.service';
import { SuperadminDashboardComponent } from "../../../feature/superadmin/superadmin-dashboard/superadmin-dashboard.component";
import { MatExpansionModule } from '@angular/material/expansion';


interface ExpandedItems {
  [key: string]: boolean;
}

@Component({
  selector: 'app-slidebar',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatToolbarModule,
    MatButtonModule,
    MatExpansionModule
    // SuperadminDashboardComponent
],
  templateUrl: './slidebar.component.html',
  styleUrls: ['./slidebar.component.css']
})
export class SlidebarComponent implements OnInit{

  userRole: string = '';
  baseRoute: string = '';

  public authService = inject(AuthService);
  public router = inject(Router);
  

  ngOnInit(): void {
    this.userRole = this.authService.getUserRole() ?? '';
    this.baseRoute = this.userRole === 'COMPANY_SUPERADMIN' ? '/superadmin' : '/admin';
  }


  expandedItems: ExpandedItems = {
    profile: false,
    settings: false,
    general: false,
    company: false
  };





 

  getPageTitle(): string {
    const route = this.router.url.split('/')[2];
    return route.charAt(0).toUpperCase() + route.slice(1) || 'Dashboard';
  }
  
  




  
  toggleSubmenu(key: string) {
    if (key === 'settings') {
      this.expandedItems['general'] = false;
    }
    this.expandedItems[key] = !this.expandedItems[key];
  }
}