import { type IUser } from '@/shared/model/user.model';
import { type IProperty } from '@/shared/model/property.model';

export interface IReview {
  id?: number;
  rating?: number;
  comment?: string | null;
  reviewDate?: Date;
  author?: IUser | null;
  property?: IProperty | null;
}

export class Review implements IReview {
  constructor(
    public id?: number,
    public rating?: number,
    public comment?: string | null,
    public reviewDate?: Date,
    public author?: IUser | null,
    public property?: IProperty | null,
  ) {}
}
