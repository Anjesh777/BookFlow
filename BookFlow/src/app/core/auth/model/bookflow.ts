import exp from "constants";

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
  enabled:Boolean;
  verified:Boolean;

}

export interface CompanyFilter{

  search?: string;
  verified?: boolean;
  status?: boolean;
  dateRange?: {
    fromDate: string;
    toDate: string;
  };


}

export interface NotificationData {
  title: string;
  message: string;
  targetAudience: string;
  notificationType: string;
}

export interface NotificationDataResponse extends NotificationData {

  id: number;         
  createdAt: string;
}
