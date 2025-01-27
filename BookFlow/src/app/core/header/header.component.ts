import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router, RouterLink, RouterModule, RouterOutlet } from '@angular/router';
import { AuthService } from '../auth/service/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule,RouterModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})

export class HeaderComponent implements OnInit{

  public authService = inject(AuthService);
  public router = inject(Router); 

  ngOnInit(): void {

    if(!this.authService.isLoggedIn()){
      this.router.navigate(['/login-cmp']);
    }
    
  }

  checkLoginStatus() {
    return this.authService.isLoggedIn();
  }


 



  
    




 

}
