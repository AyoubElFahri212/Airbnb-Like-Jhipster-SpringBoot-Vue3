/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import PropertyDetails from './property-details.vue';
import PropertyService from './property.service';
import AlertService from '@/shared/alert/alert.service';

type PropertyDetailsComponentType = InstanceType<typeof PropertyDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const propertySample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Property Management Detail Component', () => {
    let propertyServiceStub: SinonStubbedInstance<PropertyService>;
    let mountOptions: MountingOptions<PropertyDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      propertyServiceStub = sinon.createStubInstance<PropertyService>(PropertyService);

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
          propertyService: () => propertyServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        propertyServiceStub.find.resolves(propertySample);
        route = {
          params: {
            propertyId: `${123}`,
          },
        };
        const wrapper = shallowMount(PropertyDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.property).toMatchObject(propertySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        propertyServiceStub.find.resolves(propertySample);
        const wrapper = shallowMount(PropertyDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
