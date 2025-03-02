import { Component } from '@angular/core';
import { UiServiceService } from '../../../../core/ui/ui-service.service'; 
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { Router, RouterModule } from '@angular/router';
import { TimeAgoPipe } from '../../../../core/pipe/shared/pipes/time-ago.pipe'; 
import { MatMenuModule } from '@angular/material/menu';
import { ChartData, ChartOptions } from 'chart.js';
import { AccountServiceService } from '../../../../core/auth/service/Account-Service/account-service.service';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatButtonModule,
    MatDividerModule,
    RouterModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatToolbarModule,
    MatButtonModule,
    TimeAgoPipe,
    MatMenuModule,
    FormsModule


  ],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent {
  
  transactionData: { [key: string]: number } = {};
  dailyTransactionData!: ChartData<'line'>;
  selectedMonth: string;
  selectedFilter = 'month';
  last12Months: { value: string; label: string }[] = [];  


  chartOptions: ChartOptions = {
    responsive: true,
    plugins: {
      legend: { display: true },
      tooltip: { enabled: true }
    }
  };

  constructor(private accountservice: AccountServiceService) {
    const now = new Date();
    this.selectedMonth = `${now.getFullYear()}-${(now.getMonth() + 1)
      .toString()
      .padStart(2, '0')}-01`;

      this.last12Months = this.getLast12Months();  

  }

  ngOnInit(): void {
    this.fetchTransactionData();
    this.fetchDailyTransactionData();
  }

  fetchTransactionData(): void {
    if (this.selectedFilter === 'month') {
      this.accountservice.getMonthlyTransactions().subscribe((data) => {
        this.transactionData = data;
      });
    } else if (this.selectedFilter === 'day' && this.selectedMonth) {
      this.accountservice.getDailyTransactions(this.selectedMonth).subscribe((data) => {
        this.transactionData = data;
      });
    }
  }

  fetchDailyTransactionData(): void {
    this.accountservice.getDailyTransactions(this.selectedMonth).subscribe((data) => {
      const days = Object.keys(data);
      const values = Object.values(data);

      this.dailyTransactionData = {
        labels: days,
        datasets: [
          {
            label: 'Daily Transactions',
            data: values,
            borderColor: '#E24A90',
            backgroundColor: 'rgba(226, 74, 144, 0.2)',
            fill: true
          }
        ]
      };
    });
  }

  
  

  getLast12Months(): { value: string; label: string }[] {
    const months = [];
    const now = new Date();
  
    for (let i = 0; i < 12; i++) {
      const date = new Date(now.getFullYear(), now.getMonth() - i, 1);
      const value = `${date.getFullYear()}-${(date.getMonth() + 1)
        .toString()
        .padStart(2, '0')}-01`;  
  
      const label = date.toLocaleString('default', { month: 'long', year: 'numeric' });
      months.push({ value, label });
    }
  
    return months;
  }
  


}
