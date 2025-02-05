/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import AmenityUpdate from './amenity-update.vue';
import AmenityService from './amenity.service';
import AlertService from '@/shared/alert/alert.service';

import PropertyService from '@/entities/property/property.service';

type AmenityUpdateComponentType = InstanceType<typeof AmenityUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const amenitySample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<AmenityUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Amenity Management Update Component', () => {
    let comp: AmenityUpdateComponentType;
    let amenityServiceStub: SinonStubbedInstance<AmenityService>;

    beforeEach(() => {
      route = {};
      amenityServiceStub = sinon.createStubInstance<AmenityService>(AmenityService);
      amenityServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          amenityService: () => amenityServiceStub,
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
        const wrapper = shallowMount(AmenityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.amenity = amenitySample;
        amenityServiceStub.update.resolves(amenitySample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(amenityServiceStub.update.calledWith(amenitySample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        amenityServiceStub.create.resolves(entity);
        const wrapper = shallowMount(AmenityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.amenity = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(amenityServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        amenityServiceStub.find.resolves(amenitySample);
        amenityServiceStub.retrieve.resolves([amenitySample]);

        // WHEN
        route = {
          params: {
            amenityId: `${amenitySample.id}`,
          },
        };
        const wrapper = shallowMount(AmenityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.amenity).toMatchObject(amenitySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        amenityServiceStub.find.resolves(amenitySample);
        const wrapper = shallowMount(AmenityUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
