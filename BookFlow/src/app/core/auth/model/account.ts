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

  export interface LedgerEntry {
    entryId: string;
    date: string;
    particulars: string;
    amount: number;
    type: 'debit' | 'credit';
    userId: string;
    note: string;
    referenceNumber: string;
    
  }

  export interface LedgerSummary {
    totalCredits: number;
    totalDebits: number;
    balance: number;
    outstandingBalance?: number; 


  }
  