import { CompanyFilter } from "./bookflow"

export interface userDetails{

    fullname:String,
    email:String,
    phone:String,
    account:String,
    role:String,
    status:boolean
    createdby:String
    
}
export interface userDetailsResponse extends userDetails{

   user_id:string,
   created_at:Date
   _main_user:boolean
   


}
export interface UserFilter extends CompanyFilter{

    role?:string
}
export interface Service{

    serviceId:string,
    serviceName:string,
    category:string,
    price: Number,
    duration: String
    status:boolean

}