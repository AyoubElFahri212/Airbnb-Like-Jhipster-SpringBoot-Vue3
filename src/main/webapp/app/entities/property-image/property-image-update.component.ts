import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import PropertyImageService from './property-image.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import PropertyService from '@/entities/property/property.service';
import { type IProperty } from '@/shared/model/property.model';
import { type IPropertyImage, PropertyImage } from '@/shared/model/property-image.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'PropertyImageUpdate',
  setup() {
    const propertyImageService = inject('propertyImageService', () => new PropertyImageService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const propertyImage: Ref<IPropertyImage> = ref(new PropertyImage());

    const propertyService = inject('propertyService', () => new PropertyService());

    const properties: Ref<IProperty[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

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

    const initRelationships = () => {
      propertyService()
        .retrieve()
        .then(res => {
          properties.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      imageUrl: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      isMain: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      caption: {
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 100 }).toString(), 100),
      },
      property: {},
    };
    const v$ = useVuelidate(validationRules, propertyImage as any);
    v$.value.$validate();

    return {
      propertyImageService,
      alertService,
      propertyImage,
      previousState,
      isSaving,
      currentLanguage,
      properties,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.propertyImage.id) {
        this.propertyImageService()
          .update(this.propertyImage)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('jhipsterApp.propertyImage.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.propertyImageService()
          .create(this.propertyImage)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('jhipsterApp.propertyImage.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
