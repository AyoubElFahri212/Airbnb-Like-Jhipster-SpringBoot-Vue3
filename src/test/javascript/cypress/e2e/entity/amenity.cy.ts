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

describe('Amenity e2e test', () => {
  const amenityPageUrl = '/amenity';
  const amenityPageUrlPattern = new RegExp('/amenity(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const amenitySample = { name: 'delight unit' };

  let amenity;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/amenities+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/amenities').as('postEntityRequest');
    cy.intercept('DELETE', '/api/amenities/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (amenity) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/amenities/${amenity.id}`,
      }).then(() => {
        amenity = undefined;
      });
    }
  });

  it('Amenities menu should load Amenities page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('amenity');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Amenity').should('exist');
    cy.url().should('match', amenityPageUrlPattern);
  });

  describe('Amenity page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(amenityPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Amenity page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/amenity/new$'));
        cy.getEntityCreateUpdateHeading('Amenity');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', amenityPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/amenities',
          body: amenitySample,
        }).then(({ body }) => {
          amenity = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/amenities+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/amenities?page=0&size=20>; rel="last",<http://localhost/api/amenities?page=0&size=20>; rel="first"',
              },
              body: [amenity],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(amenityPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Amenity page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('amenity');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', amenityPageUrlPattern);
      });

      it('edit button click should load edit Amenity page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Amenity');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', amenityPageUrlPattern);
      });

      it.skip('edit button click should load edit Amenity page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Amenity');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', amenityPageUrlPattern);
      });

      it('last delete button click should delete instance of Amenity', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('amenity').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', amenityPageUrlPattern);

        amenity = undefined;
      });
    });
  });

  describe('new Amenity page', () => {
    beforeEach(() => {
      cy.visit(`${amenityPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Amenity');
    });

    it('should create an instance of Amenity', () => {
      cy.get(`[data-cy="name"]`).type('voluntarily');
      cy.get(`[data-cy="name"]`).should('have.value', 'voluntarily');

      cy.get(`[data-cy="iconClass"]`).type('hm hmph ouch');
      cy.get(`[data-cy="iconClass"]`).should('have.value', 'hm hmph ouch');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        amenity = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', amenityPageUrlPattern);
    });
  });
});
