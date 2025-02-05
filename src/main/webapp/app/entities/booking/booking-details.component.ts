import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import BookingService from './booking.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { type IBooking } from '@/shared/model/booking.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'BookingDetails',
  setup() {
    const dateFormat = useDateFormat();
    const bookingService = inject('bookingService', () => new BookingService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const booking: Ref<IBooking> = ref({});

    const retrieveBooking = async bookingId => {
      try {
        const res = await bookingService().find(bookingId);
        booking.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.bookingId) {
      retrieveBooking(route.params.bookingId);
    }

    return {
      ...dateFormat,
      alertService,
      booking,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
