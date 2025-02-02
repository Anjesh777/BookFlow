export interface CompanyResponse {
  status: string; 
  company_count: number; 
  user_growth_percentage: string; 
  user_count: number; 
  company_growth_percentage: string;
  message: string; 
}
export interface companyDetails{

  company_id:String;
  company_name:String;
  registration_number:String;
  company_email:String;
  company_phone:String;
  company_address:String;
  company_createdAt:string;
  company_updatedAt:String;
  _verified:Boolean;
  _enabled:Boolean;


}