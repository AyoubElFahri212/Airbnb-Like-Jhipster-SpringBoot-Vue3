/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import PropertyImageDetails from './property-image-details.vue';
import PropertyImageService from './property-image.service';
import AlertService from '@/shared/alert/alert.service';

type PropertyImageDetailsComponentType = InstanceType<typeof PropertyImageDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const propertyImageSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('PropertyImage Management Detail Component', () => {
    let propertyImageServiceStub: SinonStubbedInstance<PropertyImageService>;
    let mountOptions: MountingOptions<PropertyImageDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      propertyImageServiceStub = sinon.createStubInstance<PropertyImageService>(PropertyImageService);

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
          propertyImageService: () => propertyImageServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        propertyImageServiceStub.find.resolves(propertyImageSample);
        route = {
          params: {
            propertyImageId: `${123}`,
          },
        };
        const wrapper = shallowMount(PropertyImageDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.propertyImage).toMatchObject(propertyImageSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        propertyImageServiceStub.find.resolves(propertyImageSample);
        const wrapper = shallowMount(PropertyImageDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
