import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthResponse, LoginRequest } from '../model/auth';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';
import { Inject, PLATFORM_ID } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly API_URL = 'http://localhost:8811/all';
  private isAuthenticated = signal<boolean>(false);
  private isBrowser: boolean;



  constructor(
    @Inject(PLATFORM_ID) private platformId: object,
    private http: HttpClient,
    private router: Router
  ) {

    const token = this.getToken();
    console.log('Auth Service Init - Token:', token ? 'exists' : 'none');
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
          console.error('Login error:', error);
          this.isAuthenticated.set(false);
          return throwError(() => new Error(error.error?.message || 'Login failed'));
        })
      );
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
    console.log('Auth Service - getToken:', token ? 'exists' : 'none');
    return token;
    }
    return null
  }

  isLoggedIn(): boolean {

    if(this.isBrowser){
      const isLoggedIn = !!this.getToken();
      console.log('Auth Service - isLoggedIn:', isLoggedIn);
      return isLoggedIn;

    }
    return false;

  }

  getUserRole(): string | null {

    if(this.isBrowser){
      const role = localStorage.getItem('userRole');
      console.log('Auth Service - getUserRole:', role);
      return role;
    }
    else{
      return null;
    }

    
  }
 

}
