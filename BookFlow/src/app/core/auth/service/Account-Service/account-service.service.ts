import { HttpClient, HttpErrorResponse, HttpEvent, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CashBook, DashboardSummary, LedgerEntry, LedgerSummary, Page, TransactionSummary } from '../../model/account';
import { catchError, Observable, of, throwError } from 'rxjs';
import { User } from '../../model/user';
import { error } from 'console';

@Injectable({
  providedIn: 'root'
})
export class AccountServiceService {


  private readonly private_URL ="http://localhost:8811/api/v1"


  constructor(private http: HttpClient) { }

  addTransaction(user: CashBook): Observable<CashBook> {
    return this.http.post<CashBook>(`${this.private_URL}/account/transaction`, user)
    .pipe(
      catchError((error) =>{
        console.error('API ERROR',error)
        return new Observable<CashBook>();
      })
     );
  }

  

  getTransactionById(id: number): Observable<CashBook> {
    return this.http.get<CashBook>(`${this.private_URL}/account/transaction/${id}`)
    .pipe(
      catchError((error) =>{
        console.error('API ERROR',error)
        return new Observable<CashBook>();
      })
     );
  }

  updateTransaction(id: number, transaction: CashBook): Observable<CashBook> {
    return this.http.put<CashBook>(`${this.private_URL}/account/transaction/${id}`, transaction)
    .pipe(
      catchError((error) =>{
        console.error('API ERROR',error)
        return new Observable<CashBook>();
      })
     );
  }

  deleteTransaction(id: number): Observable<void> {
    return this.http.delete<void>(`${this.private_URL}/account/transaction/${id}`)
    .pipe(
      catchError((error) =>{
        console.error('API ERROR',error)
        return new Observable<void>();
      })
        
      )
  }


  

 searchTransactions(
    query: string,
    fromDate: Date | null = null,
    toDate: Date | null = null,
    page: number = 0,
    size: number = 50
): Observable<Page<CashBook>> {


  
    let params = new HttpParams()
        .set('query', query)
        .set('page', page.toString())
        .set('size', size.toString());

    if (fromDate) {
        params = params.set('fromDate', fromDate.toISOString().split('T')[0]);
    }
    if (toDate) {
        params = params.set('toDate', toDate.toISOString().split('T')[0]);
    }

    return this.http.get<Page<CashBook>>(`${this.private_URL}/account/search`, { params });
}



  getTransactionByVoucherNumber(voucherNumber: string): Observable<CashBook> {
    return this.http.get<CashBook>(`${this.private_URL}/account/voucher/${voucherNumber}`)
    .pipe(
      catchError((error) =>{
        console.error('API ERROR',error)
        return new Observable<CashBook>();
      })
      )
    
    ;
  }

  getAllTransactions(
    page: number = 0,
    size: number = 10,
    sortBy: string = 'date',
    direction: string = 'desc'
  ): Observable<Page<CashBook>> {
    let params = new HttpParams()
        .set('page', page.toString())
        .set('size', size.toString())
        .set('sortBy', sortBy)
        .set('direction', direction);
    return this.http.get<Page<CashBook>>(`${this.private_URL}/account/transactions`, { params });
  }
  


getTransactionSummary(): Observable<DashboardSummary> {  
  return this.http.get<DashboardSummary>(`${this.private_URL}/account/summary`);
}


private formatDate(date: Date): string {
  return date.toISOString().split('T')[0];
}

getAllUsers():Observable<User[]>{
  return this.http.get<User[]>(`${this.private_URL}/ledger-system/users`).pipe(
    catchError((error)=>{
      return new Observable<User[]>();
    })
  )
}

searchUsers(query: string): Observable<User[]> {
  const params = new HttpParams().set('query', query);
  
  return this.http.get<User[]>(`${this.private_URL}/ledger-system/users/search`, { params }).pipe(
    catchError((error) => {
      console.error('API ERROR', error);
      return of([]); 
    })
  );
}


exportTransactionsToCSV(query?: string, fromDate?: Date | null, toDate?: Date | null): Observable<Blob> {
  let params = new HttpParams();
  
  if (query) {
    params = params.set('query', query);
  }
  if (fromDate) {
    params = params.set('fromDate', fromDate.toISOString().split('T')[0]);
  }
  if (toDate) {
    params = params.set('toDate', toDate.toISOString().split('T')[0]);
  }

  return this.http.get(`${this.private_URL}/account/export/csv`, {
    params,
    responseType: 'blob'
  });
}




importTransactionsFromCsv(file: File) {
  const formData = new FormData();
  formData.append('file', file);
  
  return this.http.post<any>(`${this.private_URL}/account/import/csv`, formData, {
    reportProgress: true,
    observe: 'events'
  });
}


addLedgerData(record: LedgerEntry): Observable<LedgerEntry>{

  return this.http.post<LedgerEntry>(`${this.private_URL}/ledger-system/add/ledger`, record)
      .pipe(
        catchError((error) =>{
          console.error('API ERROR',error)
          return new Observable<LedgerEntry>();
        })
       );
}

getUserLedgerEntries(userId: string): Observable<LedgerEntry[]> {
  return this.http.get<LedgerEntry[]>(`${this.private_URL}/ledger-system/user/${userId}/entries`)
    .pipe(
      catchError((error) => {
        console.error('API ERROR', error);
        return of([]);
      })
    );
}

getUserLedgerEntriesByDateRange(userId: string, startDate: string, endDate: string): Observable<LedgerEntry[]> {
  let params = new HttpParams()
    .set('startDate', startDate)
    .set('endDate', endDate);
    
  return this.http.get<LedgerEntry[]>(`${this.private_URL}/ledger-system/user/${userId}/entries/daterange`, { params })
    .pipe(
      catchError((error) => {
        console.error('API ERROR', error);
        return of([]);
      })
    );
}

getUserLedgerSummary(userId: string): Observable<LedgerSummary> {
  return this.http.get<LedgerSummary>(`${this.private_URL}/ledger-system/user/${userId}/summary`)
    .pipe(
      catchError((error) => {
        console.error('API ERROR', error);
        return new Observable<LedgerSummary>();
      })
    );
}

getLedgerEntryById(entryId: string): Observable<LedgerEntry> {
  return this.http.get<LedgerEntry>(`${this.private_URL}/ledger-system/entry/${entryId}`)
    .pipe(
      catchError((error) => {
        console.error('API ERROR', error);
        return new Observable<LedgerEntry>();
      })
    );
  }


  deleteLedgerEntry(entryId: string): Observable<void> {
    return this.http.delete<void>(`${this.private_URL}/ledger-system/entry/${entryId}`)
      .pipe(
        catchError((error) => {
          console.error('API ERROR', error);
          return new Observable<void>();
        })
      );
  }

  searchLedgerEntries(term: string): Observable<LedgerEntry[]> {
    const params = new HttpParams().set('term', term);
    
    return this.http.get<LedgerEntry[]>(`${this.private_URL}/ledger-system/search`, { params })
      .pipe(
        catchError((error) => {
          console.error('API ERROR', error);
          return of([]);
        })
      );
  }







}
