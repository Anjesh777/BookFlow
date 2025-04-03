import { BookingStatus } from "./booking";

export interface QueResponse {
    serviceId: string;
    appointmentDate: string;
    bookingDate: string;
    paymentStatus: string;
    bookingNotes: string;
    bookingStatus: BookingStatus;
    fixedDatedTime: string;
    serviceName: string;
    serviceDescription: string;
    servicePrice: number;
    serviceCategory: string;
    
    duration: string;
    endTime: string;
    bookingPrice: string;
    
    expectedAmount: number;
    bookingId: string;
    userName: string;
  }