import { Routes } from '@angular/router';
import { CompanyRegisterComponent } from './feature/company/company-register/company-register.component';
import { HomeComponent } from './core/home/home.component';
import { AccountLoginComponent } from './feature/account-login/account-login.component';
import { SuperadminDashboardComponent } from './feature/superadmin/superadmin-dashboard/superadmin-dashboard.component';
import { authGuard } from '../app/core/auth/guards/auth.guard';
import { SendtokenComponent } from './core/reset/sendtoken/sendtoken.component';
import { ForgotpasswordComponent } from './core/reset/forgotpassword/forgotpassword.component';
import { SlidebarComponent } from './core/ui/slidebar/slidebar.component';

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
        component: AccountLoginComponent
    },
    {
        path: 'resend-token',
        component:SendtokenComponent
    },
    {
        path: 'superadmin',
        component: SlidebarComponent,
        canActivate: [authGuard],
        data: {
            roles: ['COMPANY_SUPERADMIN']
        },
        children:[

            {
                path: '',
                redirectTo: 'dashboard',
                pathMatch: 'full'
            },        
            {
                path: 'dashboard',
                component: SuperadminDashboardComponent
            },    

        ]


    },

   
    

];

