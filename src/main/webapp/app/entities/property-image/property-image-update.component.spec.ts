/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import PropertyImageUpdate from './property-image-update.vue';
import PropertyImageService from './property-image.service';
import AlertService from '@/shared/alert/alert.service';

import PropertyService from '@/entities/property/property.service';

type PropertyImageUpdateComponentType = InstanceType<typeof PropertyImageUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const propertyImageSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<PropertyImageUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('PropertyImage Management Update Component', () => {
    let comp: PropertyImageUpdateComponentType;
    let propertyImageServiceStub: SinonStubbedInstance<PropertyImageService>;

    beforeEach(() => {
      route = {};
      propertyImageServiceStub = sinon.createStubInstance<PropertyImageService>(PropertyImageService);
      propertyImageServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          propertyImageService: () => propertyImageServiceStub,
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
        const wrapper = shallowMount(PropertyImageUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.propertyImage = propertyImageSample;
        propertyImageServiceStub.update.resolves(propertyImageSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(propertyImageServiceStub.update.calledWith(propertyImageSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        propertyImageServiceStub.create.resolves(entity);
        const wrapper = shallowMount(PropertyImageUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.propertyImage = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(propertyImageServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        propertyImageServiceStub.find.resolves(propertyImageSample);
        propertyImageServiceStub.retrieve.resolves([propertyImageSample]);

        // WHEN
        route = {
          params: {
            propertyImageId: `${propertyImageSample.id}`,
          },
        };
        const wrapper = shallowMount(PropertyImageUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.propertyImage).toMatchObject(propertyImageSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        propertyImageServiceStub.find.resolves(propertyImageSample);
        const wrapper = shallowMount(PropertyImageUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
