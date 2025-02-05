import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import AmenityService from './amenity.service';
import { type IAmenity } from '@/shared/model/amenity.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'AmenityDetails',
  setup() {
    const amenityService = inject('amenityService', () => new AmenityService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const amenity: Ref<IAmenity> = ref({});

    const retrieveAmenity = async amenityId => {
      try {
        const res = await amenityService().find(amenityId);
        amenity.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.amenityId) {
      retrieveAmenity(route.params.amenityId);
    }

    return {
      alertService,
      amenity,

      previousState,
      t$: useI18n().t,
    };
  },
});
