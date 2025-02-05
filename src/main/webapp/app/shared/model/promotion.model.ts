import { type DiscountType } from '@/shared/model/enumerations/discount-type.model';
export interface IPromotion {
  id?: number;
  code?: string;
  discountType?: keyof typeof DiscountType;
  discountValue?: number;
  validFrom?: Date;
  validUntil?: Date;
  maxUses?: number | null;
  isActive?: boolean;
}

export class Promotion implements IPromotion {
  constructor(
    public id?: number,
    public code?: string,
    public discountType?: keyof typeof DiscountType,
    public discountValue?: number,
    public validFrom?: Date,
    public validUntil?: Date,
    public maxUses?: number | null,
    public isActive?: boolean,
  ) {
    this.isActive = this.isActive ?? false;
  }
}
