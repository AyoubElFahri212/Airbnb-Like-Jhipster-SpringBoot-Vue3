import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import PromotionService from './promotion.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IPromotion, Promotion } from '@/shared/model/promotion.model';
import { DiscountType } from '@/shared/model/enumerations/discount-type.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'PromotionUpdate',
  setup() {
    const promotionService = inject('promotionService', () => new PromotionService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const promotion: Ref<IPromotion> = ref(new Promotion());
    const discountTypeValues: Ref<string[]> = ref(Object.keys(DiscountType));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrievePromotion = async promotionId => {
      try {
        const res = await promotionService().find(promotionId);
        res.validFrom = new Date(res.validFrom);
        res.validUntil = new Date(res.validUntil);
        promotion.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.promotionId) {
      retrievePromotion(route.params.promotionId);
    }

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      code: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      discountType: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      discountValue: {
        required: validations.required(t$('entity.validation.required').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 0 }).toString(), 0),
      },
      validFrom: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      validUntil: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      maxUses: {},
      isActive: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
    };
    const v$ = useVuelidate(validationRules, promotion as any);
    v$.value.$validate();

    return {
      promotionService,
      alertService,
      promotion,
      previousState,
      discountTypeValues,
      isSaving,
      currentLanguage,
      v$,
      ...useDateFormat({ entityRef: promotion }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.promotion.id) {
        this.promotionService()
          .update(this.promotion)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('jhipsterApp.promotion.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.promotionService()
          .create(this.promotion)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('jhipsterApp.promotion.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
