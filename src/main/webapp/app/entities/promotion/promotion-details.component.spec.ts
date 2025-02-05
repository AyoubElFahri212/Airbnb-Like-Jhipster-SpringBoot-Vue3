/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import PromotionDetails from './promotion-details.vue';
import PromotionService from './promotion.service';
import AlertService from '@/shared/alert/alert.service';

type PromotionDetailsComponentType = InstanceType<typeof PromotionDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const promotionSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Promotion Management Detail Component', () => {
    let promotionServiceStub: SinonStubbedInstance<PromotionService>;
    let mountOptions: MountingOptions<PromotionDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      promotionServiceStub = sinon.createStubInstance<PromotionService>(PromotionService);

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
          promotionService: () => promotionServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        promotionServiceStub.find.resolves(promotionSample);
        route = {
          params: {
            promotionId: `${123}`,
          },
        };
        const wrapper = shallowMount(PromotionDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.promotion).toMatchObject(promotionSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        promotionServiceStub.find.resolves(promotionSample);
        const wrapper = shallowMount(PromotionDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
