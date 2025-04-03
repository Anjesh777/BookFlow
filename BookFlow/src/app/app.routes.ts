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
import { UsermanagementComponent } from './feature/company/admin/usermanagement/usermanagement.component';
import { CompanymanagementComponent } from './feature/superadmin/companymanagement/companymanagement.component';
import { BookflowNotificationComponent } from './feature/superadmin/bookflow-notification/bookflow-notification.component';
import { AdminDashboardComponent } from './feature/company/admin/admin-dashboard/admin-dashboard.component';
import { AdminNotificationComponent } from './feature/company/admin/admin-notification/admin-notification.component';
import { ServiceManagementComponent } from './feature/company/admin/service-management/service-management.component';
import { DaybookEntriesComponent } from './feature/company/account/daybook-entries/daybook-entries.component';
import { LedgerSystemComponent } from './feature/company/account/ledger-system/ledger-system.component';
import { UserDashboardComponent } from './feature/user/user-dashboard/user-dashboard.component';
import { BookingPortelComponent } from './feature/user/booking-portel/booking-portel.component';
import { UserLedgerComponent } from './feature/user/user-ledger/user-ledger.component';
import { QueManagementComponent } from './feature/company/admin/que-management/que-management.component';
import { CompletedQueComponent } from './feature/company/admin/que-management/completed-que/completed-que.component';

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
        data: { roles: ['COMPANY_SUPERADMIN'] }, 
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
                path: 'notifications',
                component: BookflowNotificationComponent,
                data: { roles: ['COMPANY_SUPERADMIN', 'COMPANY_ADMIN'] } 
            }

        ]


    },
    

    {
        path: 'admin',
        component: SlidebarComponent,
        canActivate: [authGuard],
        data: { roles: ['COMPANY_ADMIN'] },
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
                component:AdminNotificationComponent
            },
            {
                path:'servicemanagement',
                component:ServiceManagementComponent
            },
            {
                path:'account',
                component:DaybookEntriesComponent
            },
            {
                path:'ledger',
                component:LedgerSystemComponent                
            },
            {
                path:'que',
                component:QueManagementComponent
            },
            {
                path:'completed',
                component:CompletedQueComponent
            }

        ]


    },

    {
        path: 'user',
        component: SlidebarComponent,
        canActivate: [authGuard],
        data: { roles: ['COMPANY_USER'] },
        children:[
            {
                path: '',
                redirectTo: 'dashboard',
                pathMatch: 'full'
            },        
            {
                path: 'dashboard',
                component: UserDashboardComponent
            }, 
            {
                path: 'booking',
                component: BookingPortelComponent
            }, 
            {
                path:'ledger',
                component:UserLedgerComponent                
            }

        ]


    },


    {
        path: '**',
        
        redirectTo: 'home'
    }
   
    

];

