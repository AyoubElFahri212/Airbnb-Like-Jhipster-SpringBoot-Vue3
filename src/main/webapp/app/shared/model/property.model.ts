import { type IUser } from '@/shared/model/user.model';
import { type ICity } from '@/shared/model/city.model';
import { type IAmenity } from '@/shared/model/amenity.model';
import { type IPropertyCategory } from '@/shared/model/property-category.model';

export interface IProperty {
  id?: number;
  title?: string;
  description?: string;
  pricePerNight?: number;
  address?: string;
  latitude?: number | null;
  longitude?: number | null;
  numberOfRooms?: number;
  numberOfBathrooms?: number | null;
  maxGuests?: number | null;
  propertySize?: number | null;
  availabilityStart?: Date | null;
  availabilityEnd?: Date | null;
  instantBook?: boolean;
  minimumStay?: number | null;
  cancellationPolicy?: string;
  houseRules?: string | null;
  isActive?: boolean;
  host?: IUser | null;
  city?: ICity | null;
  amenities?: IAmenity[] | null;
  categories?: IPropertyCategory[] | null;
}

export class Property implements IProperty {
  constructor(
    public id?: number,
    public title?: string,
    public description?: string,
    public pricePerNight?: number,
    public address?: string,
    public latitude?: number | null,
    public longitude?: number | null,
    public numberOfRooms?: number,
    public numberOfBathrooms?: number | null,
    public maxGuests?: number | null,
    public propertySize?: number | null,
    public availabilityStart?: Date | null,
    public availabilityEnd?: Date | null,
    public instantBook?: boolean,
    public minimumStay?: number | null,
    public cancellationPolicy?: string,
    public houseRules?: string | null,
    public isActive?: boolean,
    public host?: IUser | null,
    public city?: ICity | null,
    public amenities?: IAmenity[] | null,
    public categories?: IPropertyCategory[] | null,
  ) {
    this.instantBook = this.instantBook ?? false;
    this.isActive = this.isActive ?? false;
  }
}
