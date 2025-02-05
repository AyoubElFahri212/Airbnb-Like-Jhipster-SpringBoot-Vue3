/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import PromotionUpdate from './promotion-update.vue';
import PromotionService from './promotion.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

type PromotionUpdateComponentType = InstanceType<typeof PromotionUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const promotionSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<PromotionUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Promotion Management Update Component', () => {
    let comp: PromotionUpdateComponentType;
    let promotionServiceStub: SinonStubbedInstance<PromotionService>;

    beforeEach(() => {
      route = {};
      promotionServiceStub = sinon.createStubInstance<PromotionService>(PromotionService);
      promotionServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          promotionService: () => promotionServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(PromotionUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(PromotionUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.promotion = promotionSample;
        promotionServiceStub.update.resolves(promotionSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(promotionServiceStub.update.calledWith(promotionSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        promotionServiceStub.create.resolves(entity);
        const wrapper = shallowMount(PromotionUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.promotion = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(promotionServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        promotionServiceStub.find.resolves(promotionSample);
        promotionServiceStub.retrieve.resolves([promotionSample]);

        // WHEN
        route = {
          params: {
            promotionId: `${promotionSample.id}`,
          },
        };
        const wrapper = shallowMount(PromotionUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.promotion).toMatchObject(promotionSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        promotionServiceStub.find.resolves(promotionSample);
        const wrapper = shallowMount(PromotionUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
