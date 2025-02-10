import { HttpClient } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { catchError, Observable } from 'rxjs';
import { ResourceUsage } from '../../model/resourceusage';
import { Router } from '@angular/router';
import { error } from 'console';

@Injectable({
  providedIn: 'root'
})
export class ResourceUsageService {

  private readonly private_URL = 'http://localhost:8811/api/v1';

  constructor(
          @Inject(PLATFORM_ID) private platformId: object,
          private http: HttpClient,
          private router: Router
    ) {
  
      
     }

     getResourceUsage(): Observable<ResourceUsage> {
      return this.http.get<ResourceUsage>(`${this.private_URL}/bookflow/resource`).pipe(
        catchError((error) =>{
          return new Observable<ResourceUsage>();
        })
      )
    }

   

}
