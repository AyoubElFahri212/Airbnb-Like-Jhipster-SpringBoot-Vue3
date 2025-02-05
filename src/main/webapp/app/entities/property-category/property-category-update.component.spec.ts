/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import PropertyCategoryUpdate from './property-category-update.vue';
import PropertyCategoryService from './property-category.service';
import AlertService from '@/shared/alert/alert.service';

import PropertyService from '@/entities/property/property.service';

type PropertyCategoryUpdateComponentType = InstanceType<typeof PropertyCategoryUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const propertyCategorySample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<PropertyCategoryUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('PropertyCategory Management Update Component', () => {
    let comp: PropertyCategoryUpdateComponentType;
    let propertyCategoryServiceStub: SinonStubbedInstance<PropertyCategoryService>;

    beforeEach(() => {
      route = {};
      propertyCategoryServiceStub = sinon.createStubInstance<PropertyCategoryService>(PropertyCategoryService);
      propertyCategoryServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          propertyCategoryService: () => propertyCategoryServiceStub,
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

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(PropertyCategoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.propertyCategory = propertyCategorySample;
        propertyCategoryServiceStub.update.resolves(propertyCategorySample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(propertyCategoryServiceStub.update.calledWith(propertyCategorySample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        propertyCategoryServiceStub.create.resolves(entity);
        const wrapper = shallowMount(PropertyCategoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.propertyCategory = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(propertyCategoryServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        propertyCategoryServiceStub.find.resolves(propertyCategorySample);
        propertyCategoryServiceStub.retrieve.resolves([propertyCategorySample]);

        // WHEN
        route = {
          params: {
            propertyCategoryId: `${propertyCategorySample.id}`,
          },
        };
        const wrapper = shallowMount(PropertyCategoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.propertyCategory).toMatchObject(propertyCategorySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        propertyCategoryServiceStub.find.resolves(propertyCategorySample);
        const wrapper = shallowMount(PropertyCategoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
