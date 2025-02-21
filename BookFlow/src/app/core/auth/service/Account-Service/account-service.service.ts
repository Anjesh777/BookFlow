import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CashBook, DashboardSummary, Page, TransactionSummary } from '../../model/account';
import { catchError, Observable, throwError } from 'rxjs';

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
    page: number = 0,
    size: number = 10
): Observable<Page<CashBook>> {
    let params = new HttpParams()
        .set('query', query)
        .set('page', page.toString())
        .set('size', size.toString());
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
  


getTransactionSummary(): Observable<DashboardSummary> {  // Changed return type
  return this.http.get<DashboardSummary>(`${this.private_URL}/account/summary`);
}


private formatDate(date: Date): string {
  return date.toISOString().split('T')[0];
}







}
