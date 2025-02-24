export interface CashBook {
    id: number;
    date: Date;
    voucherNumber: string;
    description: string;
    category: string;
    receiptAmount: number;
    paymentAmount: number;
    balance: number;
    reimbursementPending: boolean;

}

export interface TransactionSummary {
    currentBalance: number;
    totalReceiptsToday: number;
    totalPaymentsToday: number;
    pendingReimbursements: number;
}

export interface Page<T> {
    content: T[];
    page: {
      size: number;
      number: number;
      totalElements: number;
      totalPages: number;
    }
  }
export interface DashboardSummary {
    currentBalance: number;
    totalReceiptsToday: number;
    totalPaymentsToday: number;
    pendingReimbursements: number;
  }

  export interface search {
    searchText: string;
    fromDate?: Date | null;  
    toDate?: Date | null;    
  }
