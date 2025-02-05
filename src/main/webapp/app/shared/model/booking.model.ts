import { type IUser } from '@/shared/model/user.model';
import { type IProperty } from '@/shared/model/property.model';

import { type BookingStatus } from '@/shared/model/enumerations/booking-status.model';
export interface IBooking {
  id?: number;
  checkInDate?: Date;
  checkOutDate?: Date;
  totalPrice?: number;
  bookingDate?: Date;
  status?: keyof typeof BookingStatus;
  specialRequests?: string | null;
  guest?: IUser | null;
  property?: IProperty | null;
}

export class Booking implements IBooking {
  constructor(
    public id?: number,
    public checkInDate?: Date,
    public checkOutDate?: Date,
    public totalPrice?: number,
    public bookingDate?: Date,
    public status?: keyof typeof BookingStatus,
    public specialRequests?: string | null,
    public guest?: IUser | null,
    public property?: IProperty | null,
  ) {}
}
