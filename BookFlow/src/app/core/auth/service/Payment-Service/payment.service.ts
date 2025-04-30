import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Service } from '../../model/admin';
import { BookingResponse } from '../../model/booking';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  
  constructor(private http: HttpClient) { }

  private baseUrl = 'http://localhost:8811/api/v1/user';
  private paymentUrl = 'http://localhost:8811/api/v1/payment'

  getService(): Observable<Service[]> {
    return this.http.get<Service[]>(`${this.baseUrl}/get-services`);
  }

  getUpcomingBookings(): Observable<BookingResponse[]> {
    return this.http.get<BookingResponse[]>(`${this.baseUrl}/upcoming-bookings`);
  }

  getPastBookings(): Observable<BookingResponse[]> {
    return this.http.get<BookingResponse[]>(`${this.baseUrl}/past-bookings`);
  }

  cancelBooking(bookingId: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/bookings/${bookingId}`);
  }

  initiatePayment(bookingId: string): Observable<any> {
    return this.http.post(`${this.paymentUrl}/initiate/${bookingId}`, {});
  }

  verifyPayment(paymentData: any): Observable<any> {
    return this.http.post(`${this.paymentUrl}/verify`, paymentData);
  }

}