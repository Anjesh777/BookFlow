import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-completed-que',
  standalone: true,
  imports: [RouterModule, FormsModule, MatIconModule, MatProgressSpinnerModule],
  templateUrl: './completed-que.component.html',
  styleUrl: './completed-que.component.css'
})
export class CompletedQueComponent {

}
