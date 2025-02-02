import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { inject, Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { Router } from '@angular/router';  
import { Observable, catchError, throwError } from 'rxjs';
import { companyDetails, CompanyResponse } from '../../model/bookflow';
// import { CompanyCountResponse, UserCountResponse } from '../../model/bookflow';

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

  getAllDashboardDetails(): Observable<CompanyResponse> {
    return this.http.get<CompanyResponse>(`${this.private_URL}/bookflow/company-details`).pipe(
      catchError((error) => {
          return new Observable<CompanyResponse>(); 
        
      })
    );
  }

  getRecentThreeCompanyDetails(): Observable<companyDetails[]> {
    return this.http.get<companyDetails[]>(`${this.private_URL}/bookflow/recent-3`).pipe(
      catchError((error) => {
        return new Observable<companyDetails[]>();  
      })
    );
  }

  getRecentAllCompanyDetails(): Observable<companyDetails[]> {
    return this.http.get<companyDetails[]>(`${this.private_URL}/bookflow/recent-all`).pipe(
      catchError((error) => {
        return new Observable<companyDetails[]>();  
      })
    );
  }



  // getUserGrowth(): Observable<any> {
  //   return this.http.get<any>(`${this.private_URL}/bookflow/growth`).pipe(
  //     catchError((error) => {
  //         return new Observable<any>();   
  //     })
  //   );
  // }







}
