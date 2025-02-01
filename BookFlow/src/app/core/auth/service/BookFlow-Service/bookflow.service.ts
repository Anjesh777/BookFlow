import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { inject, Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { Router } from '@angular/router';  
import { Observable, catchError, throwError } from 'rxjs';
import { CompanyCountResponse, UserCountResponse } from '../../model/bookflow';

@Injectable({
  providedIn: 'root'
})
export class BookflowService {

  private readonly private_URL = 'http://localhost:8811/api/v1';

  

  constructor(
        @Inject(PLATFORM_ID) private platformId: object,
        private http: HttpClient,
        private router: Router
  ) {

    
   }

   getAllUsers(): Observable<UserCountResponse> {
    return this.http.get<UserCountResponse>(`${this.private_URL}/bookflow/count-users`).pipe(
      catchError((error) => {
          return new Observable<UserCountResponse>(); 
        
      })
    );
  }

  getAllCompany(): Observable<CompanyCountResponse> {
    return this.http.get<CompanyCountResponse>(`${this.private_URL}/bookflow/count-company`).pipe(
      catchError((error) => {
          return new Observable<CompanyCountResponse>(); 
        
      })
    );
  }







}
