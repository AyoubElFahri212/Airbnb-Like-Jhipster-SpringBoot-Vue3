import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import PromotionService from './promotion.service';
import { useDateFormat } from '@/shared/composables';
import { type IPromotion } from '@/shared/model/promotion.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'PromotionDetails',
  setup() {
    const dateFormat = useDateFormat();
    const promotionService = inject('promotionService', () => new PromotionService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const promotion: Ref<IPromotion> = ref({});

    const retrievePromotion = async promotionId => {
      try {
        const res = await promotionService().find(promotionId);
        promotion.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.promotionId) {
      retrievePromotion(route.params.promotionId);
    }

    return {
      ...dateFormat,
      alertService,
      promotion,

      previousState,
      t$: useI18n().t,
    };
  },
});
