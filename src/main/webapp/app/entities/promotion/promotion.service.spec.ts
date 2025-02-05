/* tslint:disable max-line-length */
import axios from 'axios';
import sinon from 'sinon';
import dayjs from 'dayjs';

import PromotionService from './promotion.service';
import { DATE_TIME_FORMAT } from '@/shared/composables/date-format';
import { Promotion } from '@/shared/model/promotion.model';

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
  describe('Promotion Service', () => {
    let service: PromotionService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new PromotionService();
      currentDate = new Date();
      elemDefault = new Promotion(123, 'AAAAAAA', 'PERCENTAGE', 0, currentDate, currentDate, 0, false);
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = {
          validFrom: dayjs(currentDate).format(DATE_TIME_FORMAT),
          validUntil: dayjs(currentDate).format(DATE_TIME_FORMAT),
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

      it('should create a Promotion', async () => {
        const returnedFromService = {
          id: 123,
          validFrom: dayjs(currentDate).format(DATE_TIME_FORMAT),
          validUntil: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        const expected = { validFrom: currentDate, validUntil: currentDate, ...returnedFromService };

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a Promotion', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a Promotion', async () => {
        const returnedFromService = {
          code: 'BBBBBB',
          discountType: 'BBBBBB',
          discountValue: 1,
          validFrom: dayjs(currentDate).format(DATE_TIME_FORMAT),
          validUntil: dayjs(currentDate).format(DATE_TIME_FORMAT),
          maxUses: 1,
          isActive: true,
          ...elemDefault,
        };

        const expected = { validFrom: currentDate, validUntil: currentDate, ...returnedFromService };
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a Promotion', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a Promotion', async () => {
        const patchObject = { maxUses: 1, ...new Promotion() };
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = { validFrom: currentDate, validUntil: currentDate, ...returnedFromService };
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a Promotion', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of Promotion', async () => {
        const returnedFromService = {
          code: 'BBBBBB',
          discountType: 'BBBBBB',
          discountValue: 1,
          validFrom: dayjs(currentDate).format(DATE_TIME_FORMAT),
          validUntil: dayjs(currentDate).format(DATE_TIME_FORMAT),
          maxUses: 1,
          isActive: true,
          ...elemDefault,
        };
        const expected = { validFrom: currentDate, validUntil: currentDate, ...returnedFromService };
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve({ sort: {}, page: 0, size: 10 }).then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of Promotion', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a Promotion', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a Promotion', async () => {
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
