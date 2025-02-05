import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import ReviewService from './review.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { type IReview } from '@/shared/model/review.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ReviewDetails',
  setup() {
    const dateFormat = useDateFormat();
    const reviewService = inject('reviewService', () => new ReviewService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const review: Ref<IReview> = ref({});

    const retrieveReview = async reviewId => {
      try {
        const res = await reviewService().find(reviewId);
        review.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.reviewId) {
      retrieveReview(route.params.reviewId);
    }

    return {
      ...dateFormat,
      alertService,
      review,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
