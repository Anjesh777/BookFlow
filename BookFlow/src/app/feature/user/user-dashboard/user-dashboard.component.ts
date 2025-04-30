import { Component, ElementRef, ViewChild, AfterViewInit, OnInit } from '@angular/core';
import { UserserviceService } from '../../../core/auth/service/User-Service/userservice.service';
import { NotificationDataResponse } from '../../../core/auth/model/bookflow';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { TimeAgoPipe } from '../../../core/pipe/shared/pipes/time-ago.pipe';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { BookingSummary } from '../../../core/auth/model/booking';
import { FormsModule } from '@angular/forms';
import { 
  Chart, 
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
import { UserService2Service } from '../../../core/auth/service/User-Service/user-service2.service';

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
    MatButtonModule,
    MatDividerModule,
    MatIconModule,
    MatListModule,
    MatSidenavModule,
    MatToolbarModule,
    TimeAgoPipe,
    CommonModule,
    RouterModule,
    FormsModule
  ],
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.css']
})
export class UserDashboardComponent implements OnInit, AfterViewInit {
  @ViewChild('transactionChart', { static: false }) transactionChartRef!: ElementRef;

  combinedNotifications: NotificationDataResponse[] = [];


  viewtoggle : boolean = false;

  viewAllNotifications(): void {
    this.viewtoggle = !this.viewtoggle; // Toggle the view
    if (this.viewtoggle) {
      // Combine all notifications when viewing all
      this.combinedNotifications = [
        ...this.messageList,
        ...this.messageList2,
        ...this.messageList3
      ].sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
    }
  }
  

  bookingSummary: BookingSummary | null = null;
  ledgerData: { [key: string]: number } = {};
  ledgerChart!: Chart;
  
  selectedLedgerFilter = 'month';
  selectedLedgerMonth: string;
  last12Months: { value: string; label: string }[] = [];

  messageList: NotificationDataResponse[] = [];
  messageList2: NotificationDataResponse[] = [];
  messageList3: NotificationDataResponse[] = [];

  chartOptions: ChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { 
        display: true,
        position: 'top'
      },
      tooltip: { 
        enabled: true,
        callbacks: {
          label: (context) => {
            let label = context.dataset.label || '';
            if (label) label += ': ';
            if (context.parsed.y !== null) {
              // Remove grouping separator (comma)
              label += new Intl.NumberFormat('en-IN', { 
                style: 'currency', 
                currency: 'INR',
                maximumFractionDigits: 0,
                useGrouping: false  // This disables the thousands separator
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
          callback: (value) => {
            return new Intl.NumberFormat('en-IN', { 
              style: 'currency', 
              currency: 'INR',
              maximumFractionDigits: 0,
              useGrouping: false  // This disables the thousands separator
            }).format(value as number);
          }
        }
      }
    }
  }

  constructor(
    private userService: UserserviceService,
    private userService2: UserService2Service
  ) {
    const now = new Date();
    this.selectedLedgerMonth = `${now.getFullYear()}-${(now.getMonth() + 1).toString().padStart(2, '0')}-01`;
    this.last12Months = this.getLast12Months();
  }

  ngOnInit(): void {
    this.getNotificationUser();
    this.getNotificatiByCompany();
    this.getAllUserNotification();
    this.getBookingSummaryData();
  }

  ngAfterViewInit(): void {
    this.initializeChartAfterDataLoad();
  }

  private initializeChartAfterDataLoad(): void {
    this.loadLedgerData();
  }

  private loadLedgerData(): void {
    console.log(`Loading ledger data: ${this.selectedLedgerFilter} mode, month: ${this.selectedLedgerMonth}`);
    
    const dataObservable = this.selectedLedgerFilter === 'month' 
      ? this.userService2.getMonthlyLedgerSummary() 
      : this.userService2.getDailyLedgerSummary(this.selectedLedgerMonth);
  
    dataObservable.subscribe({
      next: (data) => {
        console.log('Raw ledger data received:', data);
        
        if (data instanceof Map) {
          this.ledgerData = Object.fromEntries(data);
        } else if (typeof data === 'object' && data !== null) {
          this.ledgerData = data;
        } else {
          console.error('Unexpected data format:', data);
          this.ledgerData = {};
        }
        
        console.log('Processed ledger data:', this.ledgerData);
        
        if (!this.ledgerChart && this.transactionChartRef?.nativeElement) {
          this.initializeLedgerChart();
        } else if (this.ledgerChart) {
          this.updateLedgerChart(this.ledgerData);
        } else {
          console.error('Cannot update chart: chart or canvas element not available');
        }
      },
      error: (error) => {
        console.error('Error loading ledger data:', error);
      }
    });
  }

  private initializeLedgerChart(): void {
    if (!this.transactionChartRef?.nativeElement) {
      console.error('Chart canvas not found');
      return;
    }
  
    if (this.ledgerChart) {
      this.ledgerChart.destroy();
    }
  
    const ctx = this.transactionChartRef.nativeElement.getContext('2d');
    
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
  
    this.updateLedgerChart(this.ledgerData);
  }


  private updateLedgerChart(data: { [key: string]: number }): void {
    if (!this.ledgerChart) {
      return;
    }
    
    const keys = Object.keys(data).sort((a, b) => {
      return this.selectedLedgerFilter === 'month' 
        ? a.localeCompare(b) 
        : parseInt(a) - parseInt(b);
    });
    
    const labels = [];
    const debitValues = [];
    const creditValues = [];
    const netValues = [];
    
    for (const key of keys) {
      // Format labels in a safer way
      if (this.selectedLedgerFilter === 'month') {
        try {
          const [year, month] = key.split('-');
          if (!isNaN(parseInt(year)) && !isNaN(parseInt(month))) {
            labels.push(`${new Date(parseInt(year), parseInt(month) - 1).toLocaleString('default', { month: 'short' })} ${year}`);
          } else {
            labels.push(key);
          }
        } catch (e) {
          labels.push(key);
          console.error('Error formatting date for key:', key, e);
        }
      } else {
        try {
          if (!isNaN(parseInt(key))) {
            const day = parseInt(key);
            const date = new Date(this.selectedLedgerMonth);
            date.setDate(day);
            labels.push(`${day} ${date.toLocaleString('default', { month: 'short' })}`);
          } else {
            labels.push(key);
          }
        } catch (e) {
          labels.push(key);
          console.error('Error formatting date for key:', key, e);
        }
      }
      
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


  onLedgerFilterChange(event: any): void {
    this.selectedLedgerFilter = event.target.value;
    this.loadLedgerData();
  }
  
  onLedgerMonthChange(event: any): void {
    this.selectedLedgerMonth = event.target.value;
    
    if (this.selectedLedgerFilter !== 'day') {
      this.selectedLedgerFilter = 'day';
      console.log(`Switching to daily view for month: ${this.selectedLedgerMonth}`);
    }
    
    this.loadLedgerData();
  }
  

  getBookingSummaryData() {
    this.userService.getBookingSummary().subscribe({
      next: (data) => {
        this.bookingSummary = data;
      },
      error: (error) => {
        console.error('Error fetching booking summary:', error);
      }
    });
  }

  getNotificationUser() {
    this.userService.getNotificationUser().subscribe({
      next: (data) => {
        this.messageList2 = data;
        this.updateCombinedNotifications();
      },
      error: (error) => {
        console.error('Error:', error);
      }
    });
  }

  getNotificatiByCompany() {
    this.userService.getoneNotificationFromCompany().subscribe({
      next: (data) => {
        this.messageList = data;
        this.updateCombinedNotifications();
      },
      error: (error) => {
        console.error('Error:', error);
      }
    });
  }

  getAllUserNotification() {
    this.userService.getAllUserNotification().subscribe({
      next: (data) => {
        this.messageList3 = data;
        this.updateCombinedNotifications();
      },
      error: (error) => {
        console.error('Error:', error);
      }
    });
  }

  private updateCombinedNotifications(): void {
    this.combinedNotifications = [
      ...this.messageList,
      ...this.messageList2,
      ...this.messageList3
    ].sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
  }

  getLast12Months(): { value: string; label: string }[] {
    const months = [];
    const now = new Date();
  
    for (let i = 0; i < 12; i++) {
      const date = new Date(now.getFullYear(), now.getMonth() - i, 1);
      const value = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-01`;  
      const label = date.toLocaleString('default', { month: 'long', year: 'numeric' });
      months.push({ value, label });
    }
  
    return months;
  }
}