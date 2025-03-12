import { Component, ElementRef, ViewChild, AfterViewInit, OnInit } from '@angular/core';
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
import { 
  Chart, 
  ChartData, 
  ChartOptions,
  LinearScale,
  CategoryScale,
  PointElement,
  LineElement,
  LineController,
  Legend,
  Tooltip,
  Filler
} from 'chart.js';
import { AccountServiceService } from '../../../../core/auth/service/Account-Service/account-service.service';
import { FormsModule } from '@angular/forms';
import { HomeDashboard } from '../../../../core/auth/model/account';
import { NotificationDataResponse } from '../../../../core/auth/model/bookflow';
import { AdminService } from '../../../../core/auth/service/Admin-Service/admin.service';

Chart.register(
  LinearScale,
  CategoryScale,
  PointElement,
  LineElement,
  LineController,  
  Legend,
  Tooltip,
  Filler
);

@Component({
  selector: 'app-user-dashboard',
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
export class AdminDashboardComponent implements OnInit, AfterViewInit {
  
  @ViewChild('transactionChart') transactionChartRef!: ElementRef;
  @ViewChild('ledgerChart') ledgerChartRef!: ElementRef;

  summaryData: HomeDashboard;
  isLoading: boolean = false;

  notificationList: NotificationDataResponse[] = []; 
  notificationList2: NotificationDataResponse[] = [];


  transactionData: { [key: string]: number } = {};
  ledgerData: { [key: string]: number } = {};


  transactionChart!: Chart;
  ledgerChart!: Chart;


  dailyTransactionData!: ChartData<'line'>;
  monthlyTransactionData!: ChartData<'line'>;

  selectedMonth: string;
  selectedFilter = 'month';
  
  selectedLedgerFilter = 'month';
  selectedLedgerMonth: string;


  last12Months: { value: string; label: string }[] = [];  

  chartOptions: ChartOptions = {
    responsive: true,
    plugins: {
      legend: { display: true },
      tooltip: { 
        enabled: true,
        callbacks: {
          label: function(context) {
            let label = context.dataset.label || '';
            if (label) {
              label += ': ';
            }
            if (context.parsed.y !== null) {
              label += new Intl.NumberFormat('en-IN', { 
                style: 'currency', 
                currency: 'INR',
                maximumFractionDigits: 0
              }).format(context.parsed.y);
            }
            return label;
          }
        }
      }
    },
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          callback: function(value) {
            return '₹ ' + value;
          }
        }
      }
    }
  };




  constructor(private accountservice: AccountServiceService, private adminService: AdminService) {
    

    this.summaryData = {
      TotalCashbook: 0,
      TotalLedger: 0,
      ServiceBooked: 0,
      TotalUsers: 0
    };


    const now = new Date();
    this.selectedMonth = `${now.getFullYear()}-${(now.getMonth() + 1)
      .toString()
      .padStart(2, '0')}-01`;


     
    this.selectedLedgerMonth = this.selectedMonth;
    this.last12Months = this.getLast12Months();
  }

  
  getAllThreeComment(){
  
    setTimeout(() =>{

      this.adminService.getThreeNotification()
        .subscribe({
          next:(response) =>{
            this.notificationList=response;
            console.log("message is "+this.notificationList)
          },
          error:(error) =>{
            console.log('Error', error)
          }


        })
    })
    
}

  ngOnInit(): void {
    this.fetchTransactionData();
    this.fetchDailyTransactionData();
    this.fetchLedgerData();
    this.fetchRecentSummaryData();
    this.getAllThreeComment();
    this.getNotificatiByCompany();

  }

  ngAfterViewInit() {
    this.initializeChart();
    this.initializeLedgerChart();
  }

  initializeChart(): void {
    const ctx = this.transactionChartRef.nativeElement.getContext('2d');
    
    this.transactionChart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: [],
        datasets: [
          {
            label: 'Receipts (Income)',
            data: [],
            borderColor: '#4CAF50',
            backgroundColor: 'rgba(76, 175, 80, 0.2)',
            fill: true,
            pointBackgroundColor: '#4CAF50'
          },
          {
            label: 'Payables (Expenses)',
            data: [],
            borderColor: '#F44336',
            backgroundColor: 'rgba(244, 67, 54, 0.2)',
            fill: true,
            pointBackgroundColor: '#F44336'
          },
          {
            label: 'Net Cash Flow',
            data: [],
            borderColor: '#2196F3',
            backgroundColor: 'rgba(33, 150, 243, 0.1)',
            borderDash: [5, 5],
            pointBackgroundColor: '#2196F3'
          }
        ]
      },
      options: this.chartOptions
    });

    if (this.selectedFilter === 'month') {
      this.accountservice.getMonthlyTransactions().subscribe((data) => {
        this.updateChart(data);
      });
    } else {
      this.accountservice.getDailyTransactions(this.selectedMonth).subscribe((data) => {
        this.updateChart(data);
      });
    }
  }

  initializeLedgerChart(): void {
    const ctx = this.ledgerChartRef.nativeElement.getContext('2d');
    
    this.ledgerChart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: [],
        datasets: [
          {
            label: 'Debit Entries',
            data: [],
            borderColor: '#F44336',  
            backgroundColor: 'rgba(244, 67, 54, 0.2)',
            fill: true,
            pointBackgroundColor: '#F44336'
          },
          {
            label: 'Credit Entries',
            data: [],
            borderColor: '#4CAF50',  
            backgroundColor: 'rgba(76, 175, 80, 0.2)',
            fill: true,
            pointBackgroundColor: '#4CAF50'
          },
          {
            label: 'Net Balance',
            data: [],
            borderColor: '#2196F3',
            backgroundColor: 'rgba(33, 150, 243, 0.1)',
            borderDash: [5, 5],
            pointBackgroundColor: '#2196F3'
          }
        ]
      },
      options: this.chartOptions
    });

    if (this.selectedLedgerFilter === 'month') {
      this.accountservice.getLedgerMonthlyTransaction().subscribe((data) => {
        this.updateLedgerChart(data);
      });
    } else {
      this.accountservice.getLedgerDailyTransaction(this.selectedLedgerMonth).subscribe((data) => {
        this.updateLedgerChart(data);
      });
    }
  }

  updateLedgerChart(data: { [key: string]: number }): void {
    if (!this.ledgerChart) {
      return;
    }
    
    const labels = Object.keys(data);
    const debitValues = [];
    const creditValues = [];
    const netValues = [];
    
    for (const key of labels) {
      const value = data[key];
      netValues.push(value);
      
      if (value < 0) { 
        debitValues.push(Math.abs(value));
        creditValues.push(0);
      } else {
        debitValues.push(0);
        creditValues.push(value); 
      }
    }
    
    this.ledgerChart.data.labels = labels;
    this.ledgerChart.data.datasets[0].data = debitValues;
    this.ledgerChart.data.datasets[1].data = creditValues;
    this.ledgerChart.data.datasets[2].data = netValues;
    
    this.ledgerChart.update();
  }

  





  fetchTransactionData(): void {
    if (this.selectedFilter === 'month') {
      this.accountservice.getMonthlyTransactions().subscribe((data) => {
        this.transactionData = data;
        if (this.transactionChart) {
          this.updateChart(data);
        }
      });
    } else if (this.selectedFilter === 'day' && this.selectedMonth) {
      this.accountservice.getDailyTransactions(this.selectedMonth).subscribe((data) => {
        this.transactionData = data;
        if (this.transactionChart) {
          this.updateChart(data);
        }
      });
    }
  }

  fetchLedgerData(): void {
    if (this.selectedLedgerFilter === 'month') {
      this.accountservice.getLedgerMonthlyTransaction().subscribe((data) => {
        this.ledgerData = data;
        if (this.ledgerChart) {
          this.updateLedgerChart(data);
        }
      });
    } else if (this.selectedLedgerFilter === 'day' && this.selectedLedgerMonth) {
      this.accountservice.getLedgerDailyTransaction(this.selectedLedgerMonth).subscribe((data) => {
        this.ledgerData = data;
        if (this.ledgerChart) {
          this.updateLedgerChart(data);
        }
      });
    }
  }


  updateChart(data: { [key: string]: number }): void {
    if (!this.transactionChart) {
      return;
    }
    
    const labels = Object.keys(data);
    const receiptValues = [];
    const payableValues = [];
    const netValues = [];
    
    for (const key of labels) {
      const value = data[key];
      netValues.push(value);
      
      if (value >= 0) {
        receiptValues.push(value);
        payableValues.push(0);
      } else {
        receiptValues.push(0);
        payableValues.push(Math.abs(value)); 
      }
    }
    
    this.transactionChart.data.labels = labels;
    this.transactionChart.data.datasets[0].data = receiptValues;
    this.transactionChart.data.datasets[1].data = payableValues;
    this.transactionChart.data.datasets[2].data = netValues;
    
    this.transactionChart.update();
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
  
  onFilterChange(event: any): void {
    this.selectedFilter = event.target.value;
    this.fetchTransactionData();
  }
  
  onMonthChange(event: any): void {
    this.selectedMonth = event.target.value;
    this.fetchTransactionData();
    this.fetchDailyTransactionData();
  }

  onLedgerFilterChange(event: any): void {
    this.selectedLedgerFilter = event.target.value;
    this.fetchLedgerData();
  }

  onLedgerMonthChange(event: any): void {
    this.selectedLedgerMonth = event.target.value;
    this.fetchLedgerData();
  }

  fetchRecentSummaryData() {
    this.isLoading = true;
    this.accountservice.getSummaryDailyTransaction()
      .subscribe({
        next: (response: any) => {
          this.summaryData = {
            TotalCashbook: response.totalCashbook || 0,
            TotalLedger: response.totalLedger || 0,
            ServiceBooked: response.serviceBooked || 0,
            TotalUsers: response.totalUsers || 0
          };
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error fetching summary data:', error);
          this.isLoading = false;
        }
      });
  }

  getNotificatiByCompany() {
    this.adminService.getAllUserNotification().subscribe({
      next: (data) => {
        this.notificationList2 = data;
        console.log('Notification Data:', data);
      },
      error: (error) => {
        console.error('Error:', error);
      }
    });
  }

  



}