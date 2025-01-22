import { Component, ViewChild } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIcon, MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenav, MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterModule, RouterOutlet } from '@angular/router';


@Component({
  selector: 'app-slidebar',
  standalone: true,
  imports: [RouterModule,MatDialogModule,
    MatButtonModule,MatDialogModule,MatIconModule,MatSidenavModule,
    MatButtonModule,MatToolbarModule,MatListModule],
  templateUrl: './slidebar.component.html',
  styleUrl: './slidebar.component.css'
})
export class SlidebarComponent {
  @ViewChild('sidenav') sidenav!: MatSidenav;
  title = 'BookFlow';

  toggleSidenav() {
    this.sidenav.toggle();
  }



}
