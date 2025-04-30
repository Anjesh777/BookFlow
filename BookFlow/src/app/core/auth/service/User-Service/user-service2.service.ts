import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { Service } from '../../model/admin';
import { Router } from '@angular/router';
import { LedgerEntry } from '../../model/account';
import { BookingRequest, BookingResponse } from '../../model/booking';

@Injectable({
  providedIn: 'root'
})
export class UserService2Service {

    private readonly private_URL ="http://localhost:8811/api/v1"
    constructor(
              @Inject(PLATFORM_ID) private platformId: object,
              private http: HttpClient,
              private router: Router
    ) {}

      getService():Observable<Service[]>{
          return this.http.get<Service[]>(`${this.private_URL}/user/get-services`).pipe(
            catchError((error) =>{
              return new Observable<Service[]>();
            })
    
          )
      }

      getUserLedgerEntries(): Observable<LedgerEntry[]> {
        return this.http.get<LedgerEntry[]>(`${this.private_URL}/user/entries`);
      }

     
      exportUserLedgerToCSV(): Observable<Blob> {
        let params = new HttpParams();
        return this.http.get(`${this.private_URL}/user/export`, {
          params,
          responseType: 'blob'
        });
      }

      createBooking(bookingData: BookingRequest): Observable<any> {
        return this.http.post<any>(`${this.private_URL}/user/bookings`, bookingData).pipe(
          catchError((error) => {
            console.error('Error creating booking:', error);
            return new Observable<any>();
          })
        );
      }

      getPastBookings(): Observable<BookingResponse[]> {
        return this.http.get<BookingResponse[]>(`${this.private_URL}/user/past-bookings`).pipe(
          catchError((error: HttpErrorResponse) => {
            console.error('Error loading past bookings:', error);
            return throwError(() => error);
          })
        );
      }

      getUpcomingBookings(): Observable<BookingResponse[]> {
        return this.http.get<BookingResponse[]>(`${this.private_URL}/user/upcoming-bookings`).pipe(
          catchError((error: HttpErrorResponse) => {
            console.error('Error loading upcoming bookings:', error);
            return throwError(() => error);
          })
        );
      }


      getActiveServices(): Observable<Service[]> {
        return this.http.get<Service[]>(`${this.private_URL}/user/services/active`).pipe(
          catchError((error: HttpErrorResponse) => {
            console.error('Error loading active services:', error);
            return throwError(() => error);
          })
        );
      }



      cancelBooking(bookingId: string): Observable<any> {
        return this.http.delete(`${this.private_URL}/user/bookings/${bookingId}`).pipe(
          catchError((error: HttpErrorResponse) => {
            console.error('Error canceling booking:', error);
            return throwError(() => error);
          })
        );
      }

      getMonthlyLedgerSummary(): Observable<Map<string, number>> {
        return this.http.get<Map<string, number>>(`${this.private_URL}/user/ledger/monthly`).pipe(
          catchError((error: HttpErrorResponse) => {
            console.error('Error loading monthly ledger summary:', error);
            return throwError(() => error);
          })
        );
      }
      
      getDailyLedgerSummary(month: string): Observable<Map<string, number>> {
        const params = new HttpParams().set('month', month);
        return this.http.get<Map<string, number>>(`${this.private_URL}/user/ledger/daily`, { params }).pipe(
          catchError((error: HttpErrorResponse) => {
            console.error('Error loading daily ledger summary:', error);
            return throwError(() => error);
          })
        );
      }

}
