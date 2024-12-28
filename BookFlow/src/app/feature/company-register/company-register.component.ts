import { CommonModule } from '@angular/common';
import { Component, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-company-register',
  standalone: true,
  imports: [CommonModule,RouterModule,FormsModule],
  templateUrl: './company-register.component.html',
  styleUrl: './company-register.component.css'
})
export class CompanyRegisterComponent {

  isDropdownVisible: boolean = false;
  searchQuery: string = '';
  options: string[] = ['Dropdown option', 'Cloth set', 'Sales details', 'Marketing'];
  filteredOptions: string[] = [...this.options];
  selectedOption:string ="Select Option"

  toggleDropdown(): void {
    this.isDropdownVisible = !this.isDropdownVisible;
  }
  selectOption(option:string): void{
    console.log("Select option ", option)
    this.selectedOption=option 
    this.isDropdownVisible=false
  }
  filterOptions(): void {
    this.filteredOptions = this.options.filter(option =>
      option.toLowerCase().includes(this.searchQuery.toLowerCase())
    );
  }
  ngOnChanges():void{
    this.filterOptions()
  }
  






}
