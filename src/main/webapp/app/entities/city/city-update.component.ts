import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import CityService from './city.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import CountryService from '@/entities/country/country.service';
import { type ICountry } from '@/shared/model/country.model';
import { City, type ICity } from '@/shared/model/city.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CityUpdate',
  setup() {
    const cityService = inject('cityService', () => new CityService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const city: Ref<ICity> = ref(new City());

    const countryService = inject('countryService', () => new CountryService());

    const countries: Ref<ICountry[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveCity = async cityId => {
      try {
        const res = await cityService().find(cityId);
        city.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.cityId) {
      retrieveCity(route.params.cityId);
    }

    const initRelationships = () => {
      countryService()
        .retrieve()
        .then(res => {
          countries.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      postalCode: {
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 10 }).toString(), 10),
      },
      latitude: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      longitude: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      country: {},
    };
    const v$ = useVuelidate(validationRules, city as any);
    v$.value.$validate();

    return {
      cityService,
      alertService,
      city,
      previousState,
      isSaving,
      currentLanguage,
      countries,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.city.id) {
        this.cityService()
          .update(this.city)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('jhipsterApp.city.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.cityService()
          .create(this.city)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('jhipsterApp.city.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
