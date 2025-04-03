import { Service } from "./admin";

export interface BookingRequest {
    serviceId: string;
    appointmentDate: string;  
    bookingDate: string;      
    paymentStatus: boolean;
    bookingNotes: string;
    duration: string;
  }

  export interface BookingResponse extends BookingRequest {
    fixedDateTime: string;
    bookingId: string;
    service: Service;
    servicePrice: number;
    endTime: string;
    serviceName:string;
    bookingPrice:number;
    bookingStatus: BookingStatus;
    userName: string;
    sheduleVerified:Boolean;
    paymentMethod:string;
  }


  
  
  export enum BookingStatus {
    PENDING = 'PENDING',
    CONFIRMED = 'CONFIRMED',
    CANCELLED = 'CANCELLED',
    COMPLETED = 'COMPLETED',
    RESCHEDULED = 'RESCHEDULED',
    NO_SHOW = 'NO_SHOW'
  }
  
  
export interface BookingUpdateRequest {
  bookingId: string;
  serviceId?: string;
  appointmentDate?: string;
  bookingNotes?: string;
  bookingStatus?: BookingStatus;
}

export interface PaymentInformation {
  bookingId: string;
  amount: number;
  paymentMethod: PaymentMethod;
  paymentStatus: PaymentStatus;
  transactionId?: string;
  paymentDate: string;
}

export enum PaymentMethod {
  CASH = 'CASH',
  ONLINE = 'ONLINE',
  NOPAY = 'NOPAY'
}

export enum PaymentStatus {
  PENDING = 'PENDING',
  SUCCESS = 'SUCCESS',
  FAILED = 'FAILED'
}

export interface DateRangeDtoQue {
  startDate: Date;
  endDate: Date;
}

export interface BookingUpdateFullRequest {
  bookingId: string;
  serviceId: string;
  appointmentDate: string;
  bookingNotes: string;
  bookingStatus: BookingStatus;
  paymentStatus: PaymentStatus;
  paymentMethod: PaymentMethod;
}

