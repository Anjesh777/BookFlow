import { Routes } from '@angular/router';
import { CompanyRegisterComponent } from './feature/company-register/company-register.component';
import { HomeComponent } from './core/home/home.component';
import { CompanyLoginComponent } from './feature/company-login/company-login.component';
import { SuperadminDashboardComponent } from './feature/superadmin/superadmin-dashboard/superadmin-dashboard.component';
import { authGuard } from '../app/core/auth/guards/auth.guard';

export const routes: Routes = [

    {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full'
    },
    
    {
        path: 'register-cmp',
        component: CompanyRegisterComponent,
    },
    
    {
        path: 'home',
        component: HomeComponent,
    },
    
    {
        path: 'login-cmp',
        component: CompanyLoginComponent
    },
    
    {
        path: 'superadmin-dashboard',
        component: SuperadminDashboardComponent,
        canActivate: [authGuard],
        data: {
            roles: ['COMPANY_SUPERADMIN']
        }
    },

    {
        path: '**',
        redirectTo: 'home'
    }
    
    

];

