import { Routes } from '@angular/router';
import { CompanyRegisterComponent } from './feature/company-register/company-register.component';
import { HomeComponent } from './core/home/home.component';
import { CompanyLoginComponent } from './feature/company-login/company-login.component';

export const routes: Routes = [

    {
        path:'register-cmp',
        component:CompanyRegisterComponent,
    },
    {
        path:'home',
        component:HomeComponent,
    },
    {
        path:'login-cmp',
        component:CompanyLoginComponent
    }
    

];

