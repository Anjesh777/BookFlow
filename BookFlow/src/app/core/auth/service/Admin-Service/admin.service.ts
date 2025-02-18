import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { Router } from '@angular/router';
import { Service, serviceFilter, userDetails, userDetailsResponse, UserFilter } from '../../model/admin';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { NotificationData, NotificationDataResponse } from '../../model/bookflow';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private readonly private_URL ="http://localhost:8811/api/v1"

  constructor(
          @Inject(PLATFORM_ID) private platformId: object,
          private http: HttpClient,
          private router: Router
    ) {}

    searchUsers(filter:UserFilter):Observable<any>{
      return this.http.post<userDetailsResponse[]>(
        `${this.private_URL}/admin/search`,filter
      );
    }

    addUsers(user: userDetails): Observable<userDetails> {
      return this.http.post<userDetails>(`${this.private_URL}/admin/add-user`, user).pipe(
        catchError((error) => {
          console.error('API Error:', error); // Add this line
          return new Observable<userDetails>();
        })
      );
    }

    addService(message:Service):Observable<Service>{
      return this.http.post<Service>(
        `${this.private_URL}/admin/add-service`,message
      )
    }

    
    getUsers(): Observable<userDetailsResponse[]>{
      return this.http.get<userDetailsResponse[]>(`${this.private_URL}/admin/get-all`)
      .pipe(
        catchError((error) => {
          return new Observable<userDetailsResponse[]>();
        })
      )
    }

    updateUserdetails(Id:number,userData:userDetailsResponse):Observable<userDetailsResponse>{

         return this.http.put<userDetailsResponse>(
                `${this.private_URL}/admin/update/${Id}`,
                userData
            ).pipe(
                tap(response => console.log('Full Response:', response)),
                catchError((error: HttpErrorResponse) => {
                    console.error('Detailed Error:', error);
                    return throwError(() => new Error('Failed to update company'));
                })
            );

    }

    updateServicedetails(Id:string,serviceData:Service):Observable<Service>{

      return this.http.put<Service>(
             `${this.private_URL}/admin/service-update/${Id}`,serviceData)
             .pipe(
             tap(response => console.log('Full Response:', response)),
             catchError((error: HttpErrorResponse) => {
                 console.error('Detailed Error:', error);
                 return throwError(() => new Error('Failed to update company'));
             })
         );

 }





    deleteUser(userId: string): Observable<any> {
      return this.http.delete<any>(`${this.private_URL}/admin/delete/${userId}`)
          .pipe(
              catchError((error) => {
                  console.error('Error deleting user:', error);
                  return throwError(() => new Error('Error deleting user'));
              })
          );
    }

    deleteService(serviceId:string):Observable<any>{

      return this.http.delete<any>(`${this.private_URL}/admin/delete-service/${serviceId}`)
      .pipe(
        catchError((error) => {
            console.error('Error deleting user:', error);
            return throwError(() => new Error('Error deleting service'));
        })
    );


    }

    pushNotification(message:NotificationData):Observable<NotificationDataResponse> {
      return this.http.post<NotificationDataResponse>(
        `${this.private_URL}/admin/notification`,message);
    }





    
    deleteNotification(id: number): Observable<any> {
      return this.http.delete<any>(`${this.private_URL}/admin/notification/${id}`)
      .pipe(
        catchError((error) => {
          console.error('Error deleting notification:', error);
          return throwError(() => error);
        })
      );
    }


  
    getAllNotification():Observable<NotificationDataResponse[]>{
      
      return this.http.get<NotificationDataResponse[]>(`${this.private_URL}/admin/get-notification`).pipe(
        catchError((error) =>{
          return new Observable<NotificationDataResponse[]>();
        })

      )
    }

    getService():Observable<Service[]>{
      return this.http.get<Service[]>(`${this.private_URL}/admin/get-services`).pipe(
        catchError((error) =>{
          return new Observable<Service[]>();
        })

      )
    }

    getServiceFilterData(filter: serviceFilter): Observable<Service[]> {
      return this.http.post<Service[]>(
        `${this.private_URL}/admin/get-service-filter`, filter
      ).pipe(
        catchError((error) => {
          console.error("Filter request failed:", error);
          return throwError(() => new Error('Failed to filter services: ' + error.message));
        })
      );
    }


    
    
  }
