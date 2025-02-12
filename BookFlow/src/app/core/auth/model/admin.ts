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
   username:string


}