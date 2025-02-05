import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import PropertyService from './property.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { type IProperty } from '@/shared/model/property.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'PropertyDetails',
  setup() {
    const dateFormat = useDateFormat();
    const propertyService = inject('propertyService', () => new PropertyService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const property: Ref<IProperty> = ref({});

    const retrieveProperty = async propertyId => {
      try {
        const res = await propertyService().find(propertyId);
        property.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.propertyId) {
      retrieveProperty(route.params.propertyId);
    }

    return {
      ...dateFormat,
      alertService,
      property,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
