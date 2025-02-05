/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ReviewDetails from './review-details.vue';
import ReviewService from './review.service';
import AlertService from '@/shared/alert/alert.service';

type ReviewDetailsComponentType = InstanceType<typeof ReviewDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const reviewSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Review Management Detail Component', () => {
    let reviewServiceStub: SinonStubbedInstance<ReviewService>;
    let mountOptions: MountingOptions<ReviewDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      reviewServiceStub = sinon.createStubInstance<ReviewService>(ReviewService);

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          reviewService: () => reviewServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        reviewServiceStub.find.resolves(reviewSample);
        route = {
          params: {
            reviewId: `${123}`,
          },
        };
        const wrapper = shallowMount(ReviewDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.review).toMatchObject(reviewSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        reviewServiceStub.find.resolves(reviewSample);
        const wrapper = shallowMount(ReviewDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
