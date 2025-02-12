import { Routes } from '@angular/router';
import { CompanyRegisterComponent } from './feature/company/company-register/company-register.component';
import { HomeComponent } from './core/home/home.component';
import { AccountLoginComponent } from './feature/account-login/account-login.component';
import { SuperadminDashboardComponent } from './feature/superadmin/superadmin-dashboard/superadmin-dashboard.component';
import { authGuard } from '../app/core/auth/guards/auth.guard';
import { SendtokenComponent } from './core/reset/sendtoken/sendtoken.component';
import { ForgotpasswordComponent } from './core/reset/forgotpassword/forgotpassword.component';
import { SlidebarComponent } from './core/ui/slidebar/slidebar.component';
import { ProfileComponent } from './core/ui/profile/profile.component';
import { UsermanagementComponent } from './feature/superadmin/usermanagement/usermanagement.component';
import { CompanymanagementComponent } from './feature/superadmin/companymanagement/companymanagement.component';
import { NotificationComponent } from './feature/superadmin/notification/notification.component';
import { AdminDashboardComponent } from './feature/company/admin/admin-dashboard/admin-dashboard.component';

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
            {
                path: 'profile',
                component:ProfileComponent
            } ,  
            {
                path: 'users',
                component:UsermanagementComponent
            },
            {
                path: 'companies',
                component:CompanymanagementComponent
            },
            {
                path:'notifications',
                component:NotificationComponent
            } 

        ]


    },
    {
        path: 'admin',
        component: SlidebarComponent,
        canActivate: [authGuard],
        children:[
            {
                path: '',
                redirectTo: 'dashboard',
                pathMatch: 'full'
            },        
            {
                path: 'dashboard',
                component: AdminDashboardComponent
            }, 
            {
                path: 'profile',
                component:ProfileComponent
            } ,  
            {
                path: 'users',
                component:UsermanagementComponent
            },
            {
                path: 'companies',
                component:CompanymanagementComponent
            },
            {
                path:'notifications',
                component:NotificationComponent
            } 

        ]


    },


    {
        path: '**',
        
        redirectTo: 'home'
    }
   
    

];

