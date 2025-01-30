// slidebar.component.ts
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../auth/service/auth.service';

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
    MatButtonModule
  ],
  templateUrl: './slidebar.component.html',
  styleUrls: ['./slidebar.component.css']
})
export class SlidebarComponent {
  expandedItems: ExpandedItems = {
    profile: false,
    settings: false,
    general: false,
    company: false
  };

  public authService = inject(AuthService);
  public router = inject(Router);
  

  toggleSubmenu(key: string) {
    // If toggling a parent menu, close all child menus
    if (key === 'settings') {
      this.expandedItems['general'] = false;
    }
    this.expandedItems[key] = !this.expandedItems[key];
  }
}