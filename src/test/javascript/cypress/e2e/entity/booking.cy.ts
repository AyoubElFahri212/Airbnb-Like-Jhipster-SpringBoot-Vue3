import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('Booking e2e test', () => {
  const bookingPageUrl = '/booking';
  const bookingPageUrlPattern = new RegExp('/booking(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const bookingSample = {
    checkInDate: '2025-02-05T05:11:19.722Z',
    checkOutDate: '2025-02-05T06:03:57.195Z',
    totalPrice: 8353.94,
    bookingDate: '2025-02-04T12:32:49.273Z',
    status: 'COMPLETED',
  };

  let booking;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/bookings+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/bookings').as('postEntityRequest');
    cy.intercept('DELETE', '/api/bookings/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (booking) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/bookings/${booking.id}`,
      }).then(() => {
        booking = undefined;
      });
    }
  });

  it('Bookings menu should load Bookings page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('booking');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Booking').should('exist');
    cy.url().should('match', bookingPageUrlPattern);
  });

  describe('Booking page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(bookingPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Booking page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/booking/new$'));
        cy.getEntityCreateUpdateHeading('Booking');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', bookingPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/bookings',
          body: bookingSample,
        }).then(({ body }) => {
          booking = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/bookings+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/bookings?page=0&size=20>; rel="last",<http://localhost/api/bookings?page=0&size=20>; rel="first"',
              },
              body: [booking],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(bookingPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Booking page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('booking');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', bookingPageUrlPattern);
      });

      it('edit button click should load edit Booking page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Booking');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', bookingPageUrlPattern);
      });

      it.skip('edit button click should load edit Booking page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Booking');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', bookingPageUrlPattern);
      });

      it('last delete button click should delete instance of Booking', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('booking').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', bookingPageUrlPattern);

        booking = undefined;
      });
    });
  });

  describe('new Booking page', () => {
    beforeEach(() => {
      cy.visit(`${bookingPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Booking');
    });

    it('should create an instance of Booking', () => {
      cy.get(`[data-cy="checkInDate"]`).type('2025-02-05T09:36');
      cy.get(`[data-cy="checkInDate"]`).blur();
      cy.get(`[data-cy="checkInDate"]`).should('have.value', '2025-02-05T09:36');

      cy.get(`[data-cy="checkOutDate"]`).type('2025-02-04T17:53');
      cy.get(`[data-cy="checkOutDate"]`).blur();
      cy.get(`[data-cy="checkOutDate"]`).should('have.value', '2025-02-04T17:53');

      cy.get(`[data-cy="totalPrice"]`).type('8831.48');
      cy.get(`[data-cy="totalPrice"]`).should('have.value', '8831.48');

      cy.get(`[data-cy="bookingDate"]`).type('2025-02-05T10:46');
      cy.get(`[data-cy="bookingDate"]`).blur();
      cy.get(`[data-cy="bookingDate"]`).should('have.value', '2025-02-05T10:46');

      cy.get(`[data-cy="status"]`).select('COMPLETED');

      cy.get(`[data-cy="specialRequests"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="specialRequests"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        booking = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', bookingPageUrlPattern);
    });
  });
});
