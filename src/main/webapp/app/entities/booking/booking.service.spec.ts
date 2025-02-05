/* tslint:disable max-line-length */
import axios from 'axios';
import sinon from 'sinon';
import dayjs from 'dayjs';

import BookingService from './booking.service';
import { DATE_TIME_FORMAT } from '@/shared/composables/date-format';
import { Booking } from '@/shared/model/booking.model';

const error = {
  response: {
    status: null,
    data: {
      type: null,
    },
  },
};

const axiosStub = {
  get: sinon.stub(axios, 'get'),
  post: sinon.stub(axios, 'post'),
  put: sinon.stub(axios, 'put'),
  patch: sinon.stub(axios, 'patch'),
  delete: sinon.stub(axios, 'delete'),
};

describe('Service Tests', () => {
  describe('Booking Service', () => {
    let service: BookingService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new BookingService();
      currentDate = new Date();
      elemDefault = new Booking(123, currentDate, currentDate, 0, currentDate, 'PENDING', 'AAAAAAA');
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = {
          checkInDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          checkOutDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          bookingDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        axiosStub.get.resolves({ data: returnedFromService });

        return service.find(123).then(res => {
          expect(res).toMatchObject(elemDefault);
        });
      });

      it('should not find an element', async () => {
        axiosStub.get.rejects(error);
        return service
          .find(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should create a Booking', async () => {
        const returnedFromService = {
          id: 123,
          checkInDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          checkOutDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          bookingDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        const expected = { checkInDate: currentDate, checkOutDate: currentDate, bookingDate: currentDate, ...returnedFromService };

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a Booking', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a Booking', async () => {
        const returnedFromService = {
          checkInDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          checkOutDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          totalPrice: 1,
          bookingDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          status: 'BBBBBB',
          specialRequests: 'BBBBBB',
          ...elemDefault,
        };

        const expected = { checkInDate: currentDate, checkOutDate: currentDate, bookingDate: currentDate, ...returnedFromService };
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a Booking', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a Booking', async () => {
        const patchObject = {
          totalPrice: 1,
          bookingDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          status: 'BBBBBB',
          specialRequests: 'BBBBBB',
          ...new Booking(),
        };
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = { checkInDate: currentDate, checkOutDate: currentDate, bookingDate: currentDate, ...returnedFromService };
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a Booking', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of Booking', async () => {
        const returnedFromService = {
          checkInDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          checkOutDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          totalPrice: 1,
          bookingDate: dayjs(currentDate).format(DATE_TIME_FORMAT),
          status: 'BBBBBB',
          specialRequests: 'BBBBBB',
          ...elemDefault,
        };
        const expected = { checkInDate: currentDate, checkOutDate: currentDate, bookingDate: currentDate, ...returnedFromService };
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve({ sort: {}, page: 0, size: 10 }).then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of Booking', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a Booking', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a Booking', async () => {
        axiosStub.delete.rejects(error);

        return service
          .delete(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });
    });
  });
});
