/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import PropertyImage from './property-image.vue';
import PropertyImageService from './property-image.service';
import AlertService from '@/shared/alert/alert.service';

type PropertyImageComponentType = InstanceType<typeof PropertyImage>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('PropertyImage Management Component', () => {
    let propertyImageServiceStub: SinonStubbedInstance<PropertyImageService>;
    let mountOptions: MountingOptions<PropertyImageComponentType>['global'];

    beforeEach(() => {
      propertyImageServiceStub = sinon.createStubInstance<PropertyImageService>(PropertyImageService);
      propertyImageServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          jhiItemCount: true,
          bPagination: true,
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'jhi-sort-indicator': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          propertyImageService: () => propertyImageServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        propertyImageServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(PropertyImage, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(propertyImageServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.propertyImages[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for an id', async () => {
        // WHEN
        const wrapper = shallowMount(PropertyImage, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(propertyImageServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['id,asc'],
        });
      });
    });
    describe('Handles', () => {
      let comp: PropertyImageComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(PropertyImage, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        propertyImageServiceStub.retrieve.reset();
        propertyImageServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('should load a page', async () => {
        // GIVEN
        propertyImageServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(propertyImageServiceStub.retrieve.called).toBeTruthy();
        expect(comp.propertyImages[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should not load a page if the page is the same as the previous page', () => {
        // WHEN
        comp.page = 1;

        // THEN
        expect(propertyImageServiceStub.retrieve.called).toBeFalsy();
      });

      it('should re-initialize the page', async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        propertyImageServiceStub.retrieve.reset();
        propertyImageServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(propertyImageServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.propertyImages[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for a non-id attribute', async () => {
        // WHEN
        comp.propOrder = 'name';
        await comp.$nextTick();

        // THEN
        expect(propertyImageServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['name,asc', 'id'],
        });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        propertyImageServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removePropertyImage();
        await comp.$nextTick(); // clear components

        // THEN
        expect(propertyImageServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(propertyImageServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
