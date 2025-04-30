import { HttpClient } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { Router } from '@angular/router';
import { NotificationDataResponse } from '../../model/bookflow';
import { catchError, Observable } from 'rxjs';
import { BookingSummary } from '../../model/booking';

@Injectable({
  providedIn: 'root'
})
export class UserserviceService {

  private readonly private_URL ="http://localhost:8811/api/v1"
  constructor(
            @Inject(PLATFORM_ID) private platformId: object,
            private http: HttpClient,
            private router: Router
  ) {}
  
  getNotificationUser(){
    return this.http.get<NotificationDataResponse[]>(`${this.private_URL}/user/notification`)
        .pipe(
          catchError((error) =>{
            console.error('API ERROR',error)
            return new Observable<NotificationDataResponse[]>();
          })
        );
  }

  getoneNotificationFromCompany(){
    return this.http.get<NotificationDataResponse[]>(`${this.private_URL}/user/get-notification`)
    .pipe(
      catchError((error) =>{
        console.error('API ERROR',error)
        return new Observable<NotificationDataResponse[]>();
      })
    );
  }

  getAllUserNotification(){
     
    return this.http.get<NotificationDataResponse[]>(`${this.private_URL}/user/get-all-notification`)
    .pipe(
      catchError((error) =>{
        console.error('API ERROR',error)
          return new Observable<NotificationDataResponse[]>();
        })
      );
        
  }

  getBookingSummary(): Observable<BookingSummary> {
    return this.http.get<BookingSummary>(`${this.private_URL}/user/booking-summary`)
      .pipe(
        catchError((error) => {
          console.error('API ERROR', error);
          return new Observable<BookingSummary>();
        })
      );
  }
  




}
