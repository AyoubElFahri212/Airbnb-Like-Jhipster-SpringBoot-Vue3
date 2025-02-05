import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import PropertyImageService from './property-image.service';
import { type IPropertyImage } from '@/shared/model/property-image.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'PropertyImageDetails',
  setup() {
    const propertyImageService = inject('propertyImageService', () => new PropertyImageService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const propertyImage: Ref<IPropertyImage> = ref({});

    const retrievePropertyImage = async propertyImageId => {
      try {
        const res = await propertyImageService().find(propertyImageId);
        propertyImage.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.propertyImageId) {
      retrievePropertyImage(route.params.propertyImageId);
    }

    return {
      alertService,
      propertyImage,

      previousState,
      t$: useI18n().t,
    };
  },
});
