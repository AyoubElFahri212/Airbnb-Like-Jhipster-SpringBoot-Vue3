/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import PropertyUpdate from './property-update.vue';
import PropertyService from './property.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import CityService from '@/entities/city/city.service';
import AmenityService from '@/entities/amenity/amenity.service';
import PropertyCategoryService from '@/entities/property-category/property-category.service';

type PropertyUpdateComponentType = InstanceType<typeof PropertyUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const propertySample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<PropertyUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Property Management Update Component', () => {
    let comp: PropertyUpdateComponentType;
    let propertyServiceStub: SinonStubbedInstance<PropertyService>;

    beforeEach(() => {
      route = {};
      propertyServiceStub = sinon.createStubInstance<PropertyService>(PropertyService);
      propertyServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          propertyService: () => propertyServiceStub,

          userService: () =>
            sinon.createStubInstance<UserService>(UserService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          cityService: () =>
            sinon.createStubInstance<CityService>(CityService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          amenityService: () =>
            sinon.createStubInstance<AmenityService>(AmenityService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          propertyCategoryService: () =>
            sinon.createStubInstance<PropertyCategoryService>(PropertyCategoryService, {
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
        const wrapper = shallowMount(PropertyUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(PropertyUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.property = propertySample;
        propertyServiceStub.update.resolves(propertySample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(propertyServiceStub.update.calledWith(propertySample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        propertyServiceStub.create.resolves(entity);
        const wrapper = shallowMount(PropertyUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.property = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(propertyServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        propertyServiceStub.find.resolves(propertySample);
        propertyServiceStub.retrieve.resolves([propertySample]);

        // WHEN
        route = {
          params: {
            propertyId: `${propertySample.id}`,
          },
        };
        const wrapper = shallowMount(PropertyUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.property).toMatchObject(propertySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        propertyServiceStub.find.resolves(propertySample);
        const wrapper = shallowMount(PropertyUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
