import { Injectable, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { ApiResponse, AuthResponse, LoginRequest, ResendVerificationRequest } from '../model/auth';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';
import { Inject, PLATFORM_ID } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly API_URL = 'http://localhost:8811/all';
  private readonly API_URL1 = 'http://localhost:8811/api';


  private isAuthenticated = signal<boolean>(false);
  private isBrowser: boolean;



  constructor(
    @Inject(PLATFORM_ID) private platformId: object,
    private http: HttpClient,
    private router: Router
  ) {

    const token = this.getToken();
    // console.log('Auth Service Init - Token:', token ? 'exists' : 'none');
    this.isBrowser = isPlatformBrowser(this.platformId);

    if (this.isBrowser) {
      this.isAuthenticated.set(!!this.getToken());
    }
  }

  login(loginRequest: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/login`, loginRequest)
      .pipe(
        tap(response => {
          if (response.accessToken && this.isBrowser) {

            this.setSession(response)        
            this.isAuthenticated.set(true);
            
            console.log('Stored tokens and role:', {
              accessToken: !!response.accessToken,
              refreshToken: !!response.refreshToken,
              role: response.role
            });
          }
        }),
        catchError(error => {
          this.isAuthenticated.set(false);
          return throwError(() => new Error(error.error?.message || 'Login failed'));
        })
      );
  }

  resendVerificationToken(request: ResendVerificationRequest): Observable<ApiResponse> {

    return this.http.post<ApiResponse>(
      `${this.API_URL1}/verification/resend-token`,request)
      .pipe(
      tap(response => console.log('API Response:', response)),
      catchError(this.handleError)
    );
  }


  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred';

    if (error.error instanceof ErrorEvent) {
      errorMessage = error.error.message;
    } else {
      if (error.error?.message) {
        errorMessage = error.error.message;
      } else if (error.status === 404) {
        errorMessage = 'User not found';
      } else if (error.status === 400) {
        errorMessage = 'Invalid request. Please check your input.';
      } else if (error.status === 500) {
        errorMessage = 'Server error. Please try again later.';
      }
    }

    console.error('Error:', error);
    return throwError(() => new Error(errorMessage));
  }
  
 


  private setSession(authResult: AuthResponse) {
    if (this.isBrowser) {
      localStorage.setItem('accessToken', authResult.accessToken);
      localStorage.setItem('refreshToken', authResult.refreshToken);
      localStorage.setItem('userRole', authResult.role);
    }
  }


  logout(): void {
    if (this.isBrowser) {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('userRole');
    }
    this.isAuthenticated.set(false);
    this.router.navigate(['/login-cmp']);
  }

  getToken(): string | null {
    if(this.isBrowser){
    const token = localStorage.getItem('accessToken');
    return token;
    }
    return null
  }

  isLoggedIn(): boolean {

    if(this.isBrowser){
      const isLoggedIn = !!this.getToken();
      return isLoggedIn;
    }
    return false;

  }

  getUserRole(): string | null {

    if(this.isBrowser){
      const role = localStorage.getItem('userRole');
      return role;
    }
    else{
      return null;
    } 
  }

  

}
