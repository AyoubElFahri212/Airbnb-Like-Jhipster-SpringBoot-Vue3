import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import PropertyCategoryService from './property-category.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import PropertyService from '@/entities/property/property.service';
import { type IProperty } from '@/shared/model/property.model';
import { type IPropertyCategory, PropertyCategory } from '@/shared/model/property-category.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'PropertyCategoryUpdate',
  setup() {
    const propertyCategoryService = inject('propertyCategoryService', () => new PropertyCategoryService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const propertyCategory: Ref<IPropertyCategory> = ref(new PropertyCategory());

    const propertyService = inject('propertyService', () => new PropertyService());

    const properties: Ref<IProperty[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

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

    const initRelationships = () => {
      propertyService()
        .retrieve()
        .then(res => {
          properties.value = res.data;
        });
    };

    initRelationships();

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      description: {},
      properties: {},
    };
    const v$ = useVuelidate(validationRules, propertyCategory as any);
    v$.value.$validate();

    return {
      propertyCategoryService,
      alertService,
      propertyCategory,
      previousState,
      isSaving,
      currentLanguage,
      properties,
      ...dataUtils,
      v$,
      t$,
    };
  },
  created(): void {
    this.propertyCategory.properties = [];
  },
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.propertyCategory.id) {
        this.propertyCategoryService()
          .update(this.propertyCategory)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('jhipsterApp.propertyCategory.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.propertyCategoryService()
          .create(this.propertyCategory)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('jhipsterApp.propertyCategory.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },

    getSelected(selectedVals, option, pkField = 'id'): any {
      if (selectedVals) {
        return selectedVals.find(value => option[pkField] === value[pkField]) ?? option;
      }
      return option;
    },
  },
});
