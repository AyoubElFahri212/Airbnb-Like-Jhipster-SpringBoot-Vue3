import { type IProperty } from '@/shared/model/property.model';

export interface IPropertyCategory {
  id?: number;
  name?: string;
  description?: string | null;
  properties?: IProperty[] | null;
}

export class PropertyCategory implements IPropertyCategory {
  constructor(
    public id?: number,
    public name?: string,
    public description?: string | null,
    public properties?: IProperty[] | null,
  ) {}
}
