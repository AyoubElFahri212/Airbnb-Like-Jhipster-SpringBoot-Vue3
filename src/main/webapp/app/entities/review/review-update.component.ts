import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ReviewService from './review.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import PropertyService from '@/entities/property/property.service';
import { type IProperty } from '@/shared/model/property.model';
import { type IReview, Review } from '@/shared/model/review.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ReviewUpdate',
  setup() {
    const reviewService = inject('reviewService', () => new ReviewService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const review: Ref<IReview> = ref(new Review());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);

    const propertyService = inject('propertyService', () => new PropertyService());

    const properties: Ref<IProperty[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveReview = async reviewId => {
      try {
        const res = await reviewService().find(reviewId);
        res.reviewDate = new Date(res.reviewDate);
        review.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.reviewId) {
      retrieveReview(route.params.reviewId);
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
      rating: {
        required: validations.required(t$('entity.validation.required').toString()),
        integer: validations.integer(t$('entity.validation.number').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 1 }).toString(), 1),
        max: validations.maxValue(t$('entity.validation.max', { max: 5 }).toString(), 5),
      },
      comment: {},
      reviewDate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      author: {},
      property: {},
    };
    const v$ = useVuelidate(validationRules, review as any);
    v$.value.$validate();

    return {
      reviewService,
      alertService,
      review,
      previousState,
      isSaving,
      currentLanguage,
      users,
      properties,
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: review }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.review.id) {
        this.reviewService()
          .update(this.review)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('jhipsterApp.review.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.reviewService()
          .create(this.review)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('jhipsterApp.review.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
