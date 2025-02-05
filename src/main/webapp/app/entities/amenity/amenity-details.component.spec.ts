/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import AmenityDetails from './amenity-details.vue';
import AmenityService from './amenity.service';
import AlertService from '@/shared/alert/alert.service';

type AmenityDetailsComponentType = InstanceType<typeof AmenityDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const amenitySample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Amenity Management Detail Component', () => {
    let amenityServiceStub: SinonStubbedInstance<AmenityService>;
    let mountOptions: MountingOptions<AmenityDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      amenityServiceStub = sinon.createStubInstance<AmenityService>(AmenityService);

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
          amenityService: () => amenityServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        amenityServiceStub.find.resolves(amenitySample);
        route = {
          params: {
            amenityId: `${123}`,
          },
        };
        const wrapper = shallowMount(AmenityDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.amenity).toMatchObject(amenitySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        amenityServiceStub.find.resolves(amenitySample);
        const wrapper = shallowMount(AmenityDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
