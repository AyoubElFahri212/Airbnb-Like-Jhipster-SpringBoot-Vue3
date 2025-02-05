/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import ReviewUpdate from './review-update.vue';
import ReviewService from './review.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import PropertyService from '@/entities/property/property.service';

type ReviewUpdateComponentType = InstanceType<typeof ReviewUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const reviewSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ReviewUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Review Management Update Component', () => {
    let comp: ReviewUpdateComponentType;
    let reviewServiceStub: SinonStubbedInstance<ReviewService>;

    beforeEach(() => {
      route = {};
      reviewServiceStub = sinon.createStubInstance<ReviewService>(ReviewService);
      reviewServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          reviewService: () => reviewServiceStub,

          userService: () =>
            sinon.createStubInstance<UserService>(UserService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          propertyService: () =>
            sinon.createStubInstance<PropertyService>(PropertyService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(ReviewUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(ReviewUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.review = reviewSample;
        reviewServiceStub.update.resolves(reviewSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(reviewServiceStub.update.calledWith(reviewSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        reviewServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ReviewUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.review = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(reviewServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        reviewServiceStub.find.resolves(reviewSample);
        reviewServiceStub.retrieve.resolves([reviewSample]);

        // WHEN
        route = {
          params: {
            reviewId: `${reviewSample.id}`,
          },
        };
        const wrapper = shallowMount(ReviewUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.review).toMatchObject(reviewSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        reviewServiceStub.find.resolves(reviewSample);
        const wrapper = shallowMount(ReviewUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
