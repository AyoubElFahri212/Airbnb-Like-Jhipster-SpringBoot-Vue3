/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import BookingDetails from './booking-details.vue';
import BookingService from './booking.service';
import AlertService from '@/shared/alert/alert.service';

type BookingDetailsComponentType = InstanceType<typeof BookingDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const bookingSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Booking Management Detail Component', () => {
    let bookingServiceStub: SinonStubbedInstance<BookingService>;
    let mountOptions: MountingOptions<BookingDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      bookingServiceStub = sinon.createStubInstance<BookingService>(BookingService);

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
          bookingService: () => bookingServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        bookingServiceStub.find.resolves(bookingSample);
        route = {
          params: {
            bookingId: `${123}`,
          },
        };
        const wrapper = shallowMount(BookingDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.booking).toMatchObject(bookingSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        bookingServiceStub.find.resolves(bookingSample);
        const wrapper = shallowMount(BookingDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
