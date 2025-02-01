export interface UserCountResponse {
    status: string;
    count: number;
    message: string;
  }

  export interface CompanyCountResponse extends UserCountResponse {}

