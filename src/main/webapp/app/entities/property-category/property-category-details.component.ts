import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import PropertyCategoryService from './property-category.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { type IPropertyCategory } from '@/shared/model/property-category.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'PropertyCategoryDetails',
  setup() {
    const propertyCategoryService = inject('propertyCategoryService', () => new PropertyCategoryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const propertyCategory: Ref<IPropertyCategory> = ref({});

    const retrievePropertyCategory = async propertyCategoryId => {
      try {
        const res = await propertyCategoryService().find(propertyCategoryId);
        propertyCategory.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.propertyCategoryId) {
      retrievePropertyCategory(route.params.propertyCategoryId);
    }

    return {
      alertService,
      propertyCategory,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
