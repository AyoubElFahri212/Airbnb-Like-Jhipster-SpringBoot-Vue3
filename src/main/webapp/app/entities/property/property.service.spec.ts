/* tslint:disable max-line-length */
import axios from 'axios';
import sinon from 'sinon';
import dayjs from 'dayjs';

import PropertyService from './property.service';
import { DATE_TIME_FORMAT } from '@/shared/composables/date-format';
import { Property } from '@/shared/model/property.model';

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
  describe('Property Service', () => {
    let service: PropertyService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new PropertyService();
      currentDate = new Date();
      elemDefault = new Property(
        123,
        'AAAAAAA',
        'AAAAAAA',
        0,
        'AAAAAAA',
        0,
        0,
        0,
        0,
        0,
        0,
        currentDate,
        currentDate,
        false,
        0,
        'AAAAAAA',
        'AAAAAAA',
        false,
      );
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = {
          availabilityStart: dayjs(currentDate).format(DATE_TIME_FORMAT),
          availabilityEnd: dayjs(currentDate).format(DATE_TIME_FORMAT),
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

      it('should create a Property', async () => {
        const returnedFromService = {
          id: 123,
          availabilityStart: dayjs(currentDate).format(DATE_TIME_FORMAT),
          availabilityEnd: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        const expected = { availabilityStart: currentDate, availabilityEnd: currentDate, ...returnedFromService };

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a Property', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a Property', async () => {
        const returnedFromService = {
          title: 'BBBBBB',
          description: 'BBBBBB',
          pricePerNight: 1,
          address: 'BBBBBB',
          latitude: 1,
          longitude: 1,
          numberOfRooms: 1,
          numberOfBathrooms: 1,
          maxGuests: 1,
          propertySize: 1,
          availabilityStart: dayjs(currentDate).format(DATE_TIME_FORMAT),
          availabilityEnd: dayjs(currentDate).format(DATE_TIME_FORMAT),
          instantBook: true,
          minimumStay: 1,
          cancellationPolicy: 'BBBBBB',
          houseRules: 'BBBBBB',
          isActive: true,
          ...elemDefault,
        };

        const expected = { availabilityStart: currentDate, availabilityEnd: currentDate, ...returnedFromService };
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a Property', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a Property', async () => {
        const patchObject = {
          title: 'BBBBBB',
          address: 'BBBBBB',
          latitude: 1,
          numberOfRooms: 1,
          maxGuests: 1,
          availabilityEnd: dayjs(currentDate).format(DATE_TIME_FORMAT),
          instantBook: true,
          cancellationPolicy: 'BBBBBB',
          isActive: true,
          ...new Property(),
        };
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = { availabilityStart: currentDate, availabilityEnd: currentDate, ...returnedFromService };
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a Property', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of Property', async () => {
        const returnedFromService = {
          title: 'BBBBBB',
          description: 'BBBBBB',
          pricePerNight: 1,
          address: 'BBBBBB',
          latitude: 1,
          longitude: 1,
          numberOfRooms: 1,
          numberOfBathrooms: 1,
          maxGuests: 1,
          propertySize: 1,
          availabilityStart: dayjs(currentDate).format(DATE_TIME_FORMAT),
          availabilityEnd: dayjs(currentDate).format(DATE_TIME_FORMAT),
          instantBook: true,
          minimumStay: 1,
          cancellationPolicy: 'BBBBBB',
          houseRules: 'BBBBBB',
          isActive: true,
          ...elemDefault,
        };
        const expected = { availabilityStart: currentDate, availabilityEnd: currentDate, ...returnedFromService };
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve({ sort: {}, page: 0, size: 10 }).then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of Property', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a Property', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a Property', async () => {
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
