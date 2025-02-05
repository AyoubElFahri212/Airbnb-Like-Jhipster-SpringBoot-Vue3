import { type IProperty } from '@/shared/model/property.model';

export interface IAmenity {
  id?: number;
  name?: string;
  iconClass?: string | null;
  properties?: IProperty[] | null;
}

export class Amenity implements IAmenity {
  constructor(
    public id?: number,
    public name?: string,
    public iconClass?: string | null,
    public properties?: IProperty[] | null,
  ) {}
}
