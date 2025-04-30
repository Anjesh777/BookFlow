import { Injectable, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { ApiResponse, AuthResponse, LoginRequest, ResendVerificationRequest, userpassword } from '../model/auth';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';
import { Inject, PLATFORM_ID } from '@angular/core';
import { response } from 'express';
import { NotificationDataResponse } from '../model/bookflow';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly public_URL = 'http://localhost:8811/all';
  private readonly public_URL2 = 'http://localhost:8811/api';

  private isAuthenticated = signal<boolean>(false);
  private isBrowser: boolean;

  constructor(
    @Inject(PLATFORM_ID) private platformId: object,
    private http: HttpClient,
    private router: Router
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
    this.checkInitialAuthState();
  }

  
  private checkInitialAuthState(): void {
    if (this.isBrowser) {
      const token = this.getToken();
      this.isAuthenticated.set(!!token && token.length > 0);
    }
  }


  login(loginRequest: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.public_URL}/login`, loginRequest)
      .pipe(
        tap(response => {
          if (response?.accessToken && this.isBrowser) {
            this.setSession(response);
            this.isAuthenticated.set(true);
            
            console.log('Authentication successful', {
              isAuthenticated: true,
              hasAccessToken: !!response.accessToken,
              hasRefreshToken: !!response.refreshToken,
              role: response.role
            });
          } else {
            console.warn('Login response missing token:', response);
          }
        }),
        catchError(error => {
          this.isAuthenticated.set(false);
          console.error('Login error:', error);
          return throwError(() => new Error(error.error?.message || 'Login failed'));
        })
      );
  }


  resendVerificationToken(request: ResendVerificationRequest): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(
      `${this.public_URL2}/verification/resend-token`, request)
      .pipe(
        tap(response => console.log('API Response:', response)),
        catchError(this.handleError)
      );
  }

  resendForgetTokenPassword(request: ResendVerificationRequest): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(
      `${this.public_URL}/forgot`, request)
      .pipe(
        tap(response => console.log('API Response:', response)),
        catchError(this.handleError)
      );
  }

  resetPassword(request: userpassword): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(
      `${this.public_URL}/reset?token=${request.token}`, request)
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
  
  private setSession(authResult: AuthResponse): void {
    if (!this.isBrowser) return;

    try {
      if (!authResult?.accessToken) {
        console.error('Cannot set session: Missing access token');
        return;
      }

      sessionStorage.setItem('accessToken', authResult.accessToken);
      if (authResult.refreshToken) {
        sessionStorage.setItem('refreshToken', authResult.refreshToken);
      }
      if (authResult.role) {
        sessionStorage.setItem('userRole', authResult.role);
      }
    } catch (error) {
      console.error('Error setting session:', error);
      this.isAuthenticated.set(false);
    }
  }


  logout(): void {
    if (!this.isBrowser) return;

    try {
      sessionStorage.removeItem('accessToken');
      sessionStorage.removeItem('refreshToken');
      sessionStorage.removeItem('userRole');
      sessionStorage.removeItem('username');
      this.isAuthenticated.set(false);
      this.router.navigate(['/login']);
    } catch (error) {
      console.error('Error during logout:', error);
    }
  }

  getToken(): string | null {
    if (this.isBrowser) {
      const token = sessionStorage.getItem('accessToken');
      return token;
    }
    return null;
  }


  isLoggedIn(): boolean {
    if (this.isBrowser) {
      const isLoggedIn = !!this.getToken();
      return isLoggedIn;
    }
    return false;
  }

  getUserRole(): string | null {
    if (this.isBrowser) {
      const role = sessionStorage.getItem('userRole');
      return role;
    }
    return null;
  }

  getUserName(): string | null {
    if (this.isBrowser) {
      const userName = sessionStorage.getItem('username');
      return userName;
    }
    return null;
  }



  


}