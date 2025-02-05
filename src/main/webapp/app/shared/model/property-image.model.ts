import { type IProperty } from '@/shared/model/property.model';

export interface IPropertyImage {
  id?: number;
  imageUrl?: string;
  isMain?: boolean;
  caption?: string | null;
  property?: IProperty | null;
}

export class PropertyImage implements IPropertyImage {
  constructor(
    public id?: number,
    public imageUrl?: string,
    public isMain?: boolean,
    public caption?: string | null,
    public property?: IProperty | null,
  ) {
    this.isMain = this.isMain ?? false;
  }
}
