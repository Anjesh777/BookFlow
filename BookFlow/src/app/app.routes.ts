import { Routes } from '@angular/router';
import { CompanyRegisterComponent } from './feature/company-register/company-register.component';
import { HomeComponent } from './core/home/home.component';
import { CompanyLoginComponent } from './feature/company-login/company-login.component';
import { SuperadminDashboardComponent } from './feature/superadmin/superadmin-dashboard/superadmin-dashboard.component';
import { authGuard } from '../app/core/auth/guards/auth.guard';
import { SendtokenComponent } from './core/reset/sendtoken/sendtoken.component';
import { ForgotpasswordComponent } from './core/reset/forgotpassword/forgotpassword.component';

export const routes: Routes = [

    {
        path: 'set-password',
        component:ForgotpasswordComponent,
    },
    
    {
        path: 'register',
        component: CompanyRegisterComponent,
    },
    
    {
        path: 'home',
        component: HomeComponent,
    },
    
    {
        path: 'login',
        component: CompanyLoginComponent
    },
    {
        path: 'resend-token',
        component:SendtokenComponent
    },
    {
        path: 'superadmin-dashboard',
        component: SuperadminDashboardComponent,
        canActivate: [authGuard],
        data: {
            roles: ['COMPANY_SUPERADMIN']
        }
    },

   
    

];

