import { HttpClient, HttpParams } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { BookingRequest, BookingResponse, BookingStatus, BookingUpdateFullRequest, BookingUpdateRequest, DateRangeDtoQue, PaymentInformation } from '../../model/booking';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { QueResponse } from '../../model/que';

@Injectable({
  providedIn: 'root'
})
export class QueServiceService {



  private readonly BASE_URL  ="http://localhost:8811/api/v1"
    constructor(
            @Inject(PLATFORM_ID) private platformId: object,
            private http: HttpClient,
            private router: Router
      ) { }


      
      
      getBookingsBetweenDates(dateRange: DateRangeDtoQue): Observable<BookingResponse[]> {
        return this.http.get<BookingResponse[]>(`${this.BASE_URL}/admin/que/date-range`, { 
          params: {
            startDate: dateRange.startDate.toISOString(),
            endDate: dateRange.endDate.toISOString()
          }
        });
      }



      getBookingsByPaymentStatus(status: boolean): Observable<BookingResponse[]> {
        return this.http.get<BookingResponse[]>(`${this.BASE_URL}/admin/que/payment-status/${status}`);
      }
      getImminentBookings(): Observable<QueResponse[]> {
        return this.http.get<QueResponse[]>(`${this.BASE_URL}/admin/que/imminent`);
      }


      
      
      createBooking(bookingRequest: BookingRequest): Observable<BookingResponse> {
        return this.http.post<BookingResponse>(`${this.BASE_URL}/admin/que/create`, bookingRequest);
      }
      
      


      updateBookingPayment(bookingId: string, paymentInfo: PaymentInformation): Observable<BookingResponse> {
        return this.http.patch<BookingResponse>(
          `${this.BASE_URL}/admin/que/${bookingId}/payment`,
          paymentInfo
        );
      }


      getCancelledBookings(): Observable<QueResponse[]> {
        return this.http.get<QueResponse[]>(`${this.BASE_URL}/admin/que/cancelled`);
      }
      getActiveBookings(): Observable<QueResponse[]> {
        return this.http.get<QueResponse[]>(`${this.BASE_URL}/admin/que/active`);
      }
      getCompletedBookings(): Observable<QueResponse[]> {
        return this.http.get<QueResponse[]>(`${this.BASE_URL}/admin/que/completed`);
      }


 getPendingBookings(startDate: Date, endDate: Date): Observable<QueResponse[]> {
    const params = new HttpParams()
      .set('startDate', startDate.toISOString())
      .set('endDate', endDate.toISOString());
    
    return this.http.get<QueResponse[]>(`${this.BASE_URL}/admin/que/pending`, { params });
  }


  getBookingsByDateRange(startDate: string, endDate: string): Observable<QueResponse[]> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
      
    return this.http.get<QueResponse[]>(`${this.BASE_URL}/admin/que/date-filter`, { params });
  }

  updateBookingFull(data: BookingUpdateFullRequest): Observable<BookingResponse> {
    return this.http.put<BookingResponse>(
      `${this.BASE_URL}/admin/que/update`, 
      data
    );
  }

  getRecentCancelledBookings(): Observable<BookingResponse[]> {
    return this.http.get<BookingResponse[]>(`${this.BASE_URL}/admin/cancelled/recent`);
  }

  getCancelledBookingsByDateRange(startDate: Date, endDate: Date): Observable<BookingResponse[]> {
    const params = new HttpParams()
      .set('startDate', startDate.toISOString())
      .set('endDate', endDate.toISOString());
    
    return this.http.get<BookingResponse[]>(`${this.BASE_URL}/admin/cancelled/filter`, { params });
  }

 


}
