import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import PropertyService from './property.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import CityService from '@/entities/city/city.service';
import { type ICity } from '@/shared/model/city.model';
import AmenityService from '@/entities/amenity/amenity.service';
import { type IAmenity } from '@/shared/model/amenity.model';
import PropertyCategoryService from '@/entities/property-category/property-category.service';
import { type IPropertyCategory } from '@/shared/model/property-category.model';
import { type IProperty, Property } from '@/shared/model/property.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'PropertyUpdate',
  setup() {
    const propertyService = inject('propertyService', () => new PropertyService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const property: Ref<IProperty> = ref(new Property());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);

    const cityService = inject('cityService', () => new CityService());

    const cities: Ref<ICity[]> = ref([]);

    const amenityService = inject('amenityService', () => new AmenityService());

    const amenities: Ref<IAmenity[]> = ref([]);

    const propertyCategoryService = inject('propertyCategoryService', () => new PropertyCategoryService());

    const propertyCategories: Ref<IPropertyCategory[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveProperty = async propertyId => {
      try {
        const res = await propertyService().find(propertyId);
        res.availabilityStart = new Date(res.availabilityStart);
        res.availabilityEnd = new Date(res.availabilityEnd);
        property.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.propertyId) {
      retrieveProperty(route.params.propertyId);
    }

    const initRelationships = () => {
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
      cityService()
        .retrieve()
        .then(res => {
          cities.value = res.data;
        });
      amenityService()
        .retrieve()
        .then(res => {
          amenities.value = res.data;
        });
      propertyCategoryService()
        .retrieve()
        .then(res => {
          propertyCategories.value = res.data;
        });
    };

    initRelationships();

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      title: {
        required: validations.required(t$('entity.validation.required').toString()),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 100 }).toString(), 100),
      },
      description: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      pricePerNight: {
        required: validations.required(t$('entity.validation.required').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 0 }).toString(), 0),
      },
      address: {
        required: validations.required(t$('entity.validation.required').toString()),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 255 }).toString(), 255),
      },
      latitude: {},
      longitude: {},
      numberOfRooms: {
        required: validations.required(t$('entity.validation.required').toString()),
        integer: validations.integer(t$('entity.validation.number').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 1 }).toString(), 1),
      },
      numberOfBathrooms: {
        integer: validations.integer(t$('entity.validation.number').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 1 }).toString(), 1),
      },
      maxGuests: {
        integer: validations.integer(t$('entity.validation.number').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 1 }).toString(), 1),
      },
      propertySize: {
        integer: validations.integer(t$('entity.validation.number').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 0 }).toString(), 0),
      },
      availabilityStart: {},
      availabilityEnd: {},
      instantBook: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      minimumStay: {
        integer: validations.integer(t$('entity.validation.number').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 1 }).toString(), 1),
      },
      cancellationPolicy: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      houseRules: {},
      isActive: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      host: {},
      city: {},
      amenities: {},
      categories: {},
    };
    const v$ = useVuelidate(validationRules, property as any);
    v$.value.$validate();

    return {
      propertyService,
      alertService,
      property,
      previousState,
      isSaving,
      currentLanguage,
      users,
      cities,
      amenities,
      propertyCategories,
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: property }),
      t$,
    };
  },
  created(): void {
    this.property.amenities = [];
    this.property.categories = [];
  },
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.property.id) {
        this.propertyService()
          .update(this.property)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('jhipsterApp.property.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.propertyService()
          .create(this.property)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('jhipsterApp.property.created', { param: param.id }).toString());
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
