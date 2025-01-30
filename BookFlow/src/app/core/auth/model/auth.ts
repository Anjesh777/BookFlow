export interface User {
  username: string;
  email?: string;

}

export interface LoginRequest {
    user_name: string;      
    user_password: string;  
  }
  
  export interface AuthResponse {
    role: string;           
    accessToken: string;    
    refreshToken: string;
    status: string;
    message: string;
    
  }
  export interface ResendVerificationRequest {
    username: string
  }

  export interface userpassword{

    token:string,
    newPassword:string

  }

  export interface ApiResponse   {
    status: string;
    message: string;
    
  }