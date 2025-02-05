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

describe('Promotion e2e test', () => {
  const promotionPageUrl = '/promotion';
  const promotionPageUrlPattern = new RegExp('/promotion(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const promotionSample = {
    code: 'laughter bah at',
    discountType: 'FIXED_AMOUNT',
    discountValue: 16289.29,
    validFrom: '2025-02-05T07:47:32.425Z',
    validUntil: '2025-02-05T04:57:38.756Z',
    isActive: true,
  };

  let promotion;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/promotions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/promotions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/promotions/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (promotion) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/promotions/${promotion.id}`,
      }).then(() => {
        promotion = undefined;
      });
    }
  });

  it('Promotions menu should load Promotions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('promotion');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Promotion').should('exist');
    cy.url().should('match', promotionPageUrlPattern);
  });

  describe('Promotion page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(promotionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Promotion page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/promotion/new$'));
        cy.getEntityCreateUpdateHeading('Promotion');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', promotionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/promotions',
          body: promotionSample,
        }).then(({ body }) => {
          promotion = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/promotions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/promotions?page=0&size=20>; rel="last",<http://localhost/api/promotions?page=0&size=20>; rel="first"',
              },
              body: [promotion],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(promotionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Promotion page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('promotion');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', promotionPageUrlPattern);
      });

      it('edit button click should load edit Promotion page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Promotion');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', promotionPageUrlPattern);
      });

      it.skip('edit button click should load edit Promotion page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Promotion');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', promotionPageUrlPattern);
      });

      it('last delete button click should delete instance of Promotion', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('promotion').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', promotionPageUrlPattern);

        promotion = undefined;
      });
    });
  });

  describe('new Promotion page', () => {
    beforeEach(() => {
      cy.visit(`${promotionPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Promotion');
    });

    it('should create an instance of Promotion', () => {
      cy.get(`[data-cy="code"]`).type('whoever');
      cy.get(`[data-cy="code"]`).should('have.value', 'whoever');

      cy.get(`[data-cy="discountType"]`).select('PERCENTAGE');

      cy.get(`[data-cy="discountValue"]`).type('30248.92');
      cy.get(`[data-cy="discountValue"]`).should('have.value', '30248.92');

      cy.get(`[data-cy="validFrom"]`).type('2025-02-04T21:55');
      cy.get(`[data-cy="validFrom"]`).blur();
      cy.get(`[data-cy="validFrom"]`).should('have.value', '2025-02-04T21:55');

      cy.get(`[data-cy="validUntil"]`).type('2025-02-05T06:21');
      cy.get(`[data-cy="validUntil"]`).blur();
      cy.get(`[data-cy="validUntil"]`).should('have.value', '2025-02-05T06:21');

      cy.get(`[data-cy="maxUses"]`).type('2763');
      cy.get(`[data-cy="maxUses"]`).should('have.value', '2763');

      cy.get(`[data-cy="isActive"]`).should('not.be.checked');
      cy.get(`[data-cy="isActive"]`).click();
      cy.get(`[data-cy="isActive"]`).should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        promotion = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', promotionPageUrlPattern);
    });
  });
});
