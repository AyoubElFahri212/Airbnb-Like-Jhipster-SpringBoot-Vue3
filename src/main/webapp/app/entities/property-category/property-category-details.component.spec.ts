/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import PropertyCategoryDetails from './property-category-details.vue';
import PropertyCategoryService from './property-category.service';
import AlertService from '@/shared/alert/alert.service';

type PropertyCategoryDetailsComponentType = InstanceType<typeof PropertyCategoryDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const propertyCategorySample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('PropertyCategory Management Detail Component', () => {
    let propertyCategoryServiceStub: SinonStubbedInstance<PropertyCategoryService>;
    let mountOptions: MountingOptions<PropertyCategoryDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      propertyCategoryServiceStub = sinon.createStubInstance<PropertyCategoryService>(PropertyCategoryService);

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
          propertyCategoryService: () => propertyCategoryServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        propertyCategoryServiceStub.find.resolves(propertyCategorySample);
        route = {
          params: {
            propertyCategoryId: `${123}`,
          },
        };
        const wrapper = shallowMount(PropertyCategoryDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.propertyCategory).toMatchObject(propertyCategorySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        propertyCategoryServiceStub.find.resolves(propertyCategorySample);
        const wrapper = shallowMount(PropertyCategoryDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
