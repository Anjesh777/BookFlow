import { CommonModule } from '@angular/common';
import { Component, inject, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { last } from 'rxjs';


@Component({
  selector: 'app-company-register',
  standalone: true,
  imports: [CommonModule,RouterModule,FormsModule,ReactiveFormsModule],
  templateUrl: './company-register.component.html',
  styleUrl: './company-register.component.css'
})
export class CompanyRegisterComponent {

  http = inject(HttpClient);

  constructor(){


  }

  companyForm: FormGroup = new FormGroup({
    company_name: new FormControl("",[Validators.required,Validators.minLength(3)]),
    registration_number: new FormControl("",[
      Validators.minLength(5),
      Validators.required
    ]),
    company_email: new FormControl("",[Validators.email]),
    company_phone: new FormControl("",[ 
      Validators.required,
      Validators.minLength(10),
      Validators.maxLength(10),
      Validators.pattern("^[0-9]*$") ]),
    company_address: new FormControl("",[Validators.minLength(3)]),
    company_password: new FormControl("",[Validators.required,Validators.minLength(8)]),
    conform_Password: new FormControl("",[Validators.required, Validators.minLength(8)]),
    company_username: new FormControl("",[Validators.minLength(3)]),
    is_AcceptTerms:new FormControl("",[Validators.required])
  })



  filteredOptions: string[] = [];
  options: string[] = [];

  getListOFAllPlaces(){
    this.http.get<string[]>("http://localhost:8080/all/getListOFDistrict").subscribe(
      (res:string[]) =>{
        console.log("API Work For now")
        this.options=res;
        this.filteredOptions=[...this.options]
      },
      (err) => {
        console.log("Error is ",err)
      }
    )
  }

  


  isDropdownVisible: boolean = false;
  searchQuery: string = '';
  
  selectedOption:string ="Select Option"

  toggleDropdown(): void {
    this.isDropdownVisible = !this.isDropdownVisible;
  }
 
  selectOption(option: string): void {
    console.log("Select option ", option);
    this.selectedOption = option;
    this.companyForm.get('company_address')?.setValue(option); // This sets the selected value in the form
    this.isDropdownVisible = false;
  }

  filterOptions(): void {
    const searchValue = this.companyForm.get('company_address')?.value || '';
    this.filteredOptions = this.options.filter(option =>
        option.toLowerCase().includes(searchValue.toLowerCase())
    );
}

formValue:any;

onSave() {
  console.log("Triger")
  this.formValue = this.companyForm.value
  console.log(this.formValue)
}


  ngOnChanges():void{
    this.filterOptions()
  }
  






}
