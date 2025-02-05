import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import BookingService from './booking.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import PropertyService from '@/entities/property/property.service';
import { type IProperty } from '@/shared/model/property.model';
import { Booking, type IBooking } from '@/shared/model/booking.model';
import { BookingStatus } from '@/shared/model/enumerations/booking-status.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'BookingUpdate',
  setup() {
    const bookingService = inject('bookingService', () => new BookingService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const booking: Ref<IBooking> = ref(new Booking());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);

    const propertyService = inject('propertyService', () => new PropertyService());

    const properties: Ref<IProperty[]> = ref([]);
    const bookingStatusValues: Ref<string[]> = ref(Object.keys(BookingStatus));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveBooking = async bookingId => {
      try {
        const res = await bookingService().find(bookingId);
        res.checkInDate = new Date(res.checkInDate);
        res.checkOutDate = new Date(res.checkOutDate);
        res.bookingDate = new Date(res.bookingDate);
        booking.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.bookingId) {
      retrieveBooking(route.params.bookingId);
    }

    const initRelationships = () => {
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
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
      checkInDate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      checkOutDate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      totalPrice: {
        required: validations.required(t$('entity.validation.required').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 0 }).toString(), 0),
      },
      bookingDate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      status: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      specialRequests: {},
      guest: {},
      property: {},
    };
    const v$ = useVuelidate(validationRules, booking as any);
    v$.value.$validate();

    return {
      bookingService,
      alertService,
      booking,
      previousState,
      bookingStatusValues,
      isSaving,
      currentLanguage,
      users,
      properties,
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: booking }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.booking.id) {
        this.bookingService()
          .update(this.booking)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('jhipsterApp.booking.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.bookingService()
          .create(this.booking)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('jhipsterApp.booking.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
