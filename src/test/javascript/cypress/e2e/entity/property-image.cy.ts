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

describe('PropertyImage e2e test', () => {
  const propertyImagePageUrl = '/property-image';
  const propertyImagePageUrlPattern = new RegExp('/property-image(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const propertyImageSample = { imageUrl: 'regal', isMain: true };

  let propertyImage;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/property-images+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/property-images').as('postEntityRequest');
    cy.intercept('DELETE', '/api/property-images/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (propertyImage) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/property-images/${propertyImage.id}`,
      }).then(() => {
        propertyImage = undefined;
      });
    }
  });

  it('PropertyImages menu should load PropertyImages page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('property-image');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('PropertyImage').should('exist');
    cy.url().should('match', propertyImagePageUrlPattern);
  });

  describe('PropertyImage page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(propertyImagePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create PropertyImage page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/property-image/new$'));
        cy.getEntityCreateUpdateHeading('PropertyImage');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', propertyImagePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/property-images',
          body: propertyImageSample,
        }).then(({ body }) => {
          propertyImage = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/property-images+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/property-images?page=0&size=20>; rel="last",<http://localhost/api/property-images?page=0&size=20>; rel="first"',
              },
              body: [propertyImage],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(propertyImagePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details PropertyImage page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('propertyImage');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', propertyImagePageUrlPattern);
      });

      it('edit button click should load edit PropertyImage page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PropertyImage');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', propertyImagePageUrlPattern);
      });

      it.skip('edit button click should load edit PropertyImage page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PropertyImage');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', propertyImagePageUrlPattern);
      });

      it('last delete button click should delete instance of PropertyImage', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('propertyImage').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', propertyImagePageUrlPattern);

        propertyImage = undefined;
      });
    });
  });

  describe('new PropertyImage page', () => {
    beforeEach(() => {
      cy.visit(`${propertyImagePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('PropertyImage');
    });

    it('should create an instance of PropertyImage', () => {
      cy.get(`[data-cy="imageUrl"]`).type('even');
      cy.get(`[data-cy="imageUrl"]`).should('have.value', 'even');

      cy.get(`[data-cy="isMain"]`).should('not.be.checked');
      cy.get(`[data-cy="isMain"]`).click();
      cy.get(`[data-cy="isMain"]`).should('be.checked');

      cy.get(`[data-cy="caption"]`).type('an');
      cy.get(`[data-cy="caption"]`).should('have.value', 'an');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        propertyImage = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', propertyImagePageUrlPattern);
    });
  });
});
