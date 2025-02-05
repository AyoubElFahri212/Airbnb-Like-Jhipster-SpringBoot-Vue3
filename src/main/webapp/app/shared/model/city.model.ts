import { type ICountry } from '@/shared/model/country.model';

export interface ICity {
  id?: number;
  name?: string;
  postalCode?: string | null;
  latitude?: number;
  longitude?: number;
  country?: ICountry | null;
}

export class City implements ICity {
  constructor(
    public id?: number,
    public name?: string,
    public postalCode?: string | null,
    public latitude?: number,
    public longitude?: number,
    public country?: ICountry | null,
  ) {}
}
