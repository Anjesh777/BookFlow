import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { Router } from '@angular/router';
import { userDetails, userDetailsResponse } from '../../model/admin';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

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

    addUsers(user: userDetails): Observable<userDetails> {
      return this.http.post<userDetails>(`${this.private_URL}/admin/add-user`, user).pipe(
        catchError((error) => {
          console.error('API Error:', error); // Add this line
          return new Observable<userDetails>();
        })
      );
    }
    
    getUsers(): Observable<userDetailsResponse[]>{
      return this.http.get<userDetailsResponse[]>(`${this.private_URL}/admin/get-all`)
      .pipe(
        catchError((error) => {
          return new Observable<userDetailsResponse[]>();
        })
      )
    }

    updateUserdetails(userId:string,userData:userDetailsResponse):Observable<userDetailsResponse>{

         return this.http.put<userDetailsResponse>(
                `${this.private_URL}/admin/update/${userId}`,
                userData
            ).pipe(
                tap(response => console.log('Full Response:', response)),
                catchError((error: HttpErrorResponse) => {
                    console.error('Detailed Error:', error);
                    return throwError(() => new Error('Failed to update company'));
                })
            );

    }

    deleteUser(userId:String):Observable<any>{
      return this.http.delete<any>(``)
      .pipe(
        catchError((error) =>{
          return new Observable<userDetailsResponse[]>
        })
      )


    }






  }
