import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { RouterLink, RouterModule, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule,RouterModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})

export class HeaderComponent {

  
    isLoading: boolean = false;
    http = inject(HttpClient);
    constructor(public dialog: MatDialog) {}
    openDialog():void{}




 
  onClick(){

    this.http.get<string>("http://localhost:8811/api/v1/superadmin").subscribe(
      (res: string) => {
        console.log(res);
      }
    );
  }

}
