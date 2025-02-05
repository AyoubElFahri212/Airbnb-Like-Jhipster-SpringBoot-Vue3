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

describe('PropertyCategory e2e test', () => {
  const propertyCategoryPageUrl = '/property-category';
  const propertyCategoryPageUrlPattern = new RegExp('/property-category(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const propertyCategorySample = { name: 'rough ah' };

  let propertyCategory;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/property-categories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/property-categories').as('postEntityRequest');
    cy.intercept('DELETE', '/api/property-categories/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (propertyCategory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/property-categories/${propertyCategory.id}`,
      }).then(() => {
        propertyCategory = undefined;
      });
    }
  });

  it('PropertyCategories menu should load PropertyCategories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('property-category');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('PropertyCategory').should('exist');
    cy.url().should('match', propertyCategoryPageUrlPattern);
  });

  describe('PropertyCategory page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(propertyCategoryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create PropertyCategory page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/property-category/new$'));
        cy.getEntityCreateUpdateHeading('PropertyCategory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', propertyCategoryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/property-categories',
          body: propertyCategorySample,
        }).then(({ body }) => {
          propertyCategory = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/property-categories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/property-categories?page=0&size=20>; rel="last",<http://localhost/api/property-categories?page=0&size=20>; rel="first"',
              },
              body: [propertyCategory],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(propertyCategoryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details PropertyCategory page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('propertyCategory');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', propertyCategoryPageUrlPattern);
      });

      it('edit button click should load edit PropertyCategory page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PropertyCategory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', propertyCategoryPageUrlPattern);
      });

      it.skip('edit button click should load edit PropertyCategory page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PropertyCategory');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', propertyCategoryPageUrlPattern);
      });

      it('last delete button click should delete instance of PropertyCategory', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('propertyCategory').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', propertyCategoryPageUrlPattern);

        propertyCategory = undefined;
      });
    });
  });

  describe('new PropertyCategory page', () => {
    beforeEach(() => {
      cy.visit(`${propertyCategoryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('PropertyCategory');
    });

    it('should create an instance of PropertyCategory', () => {
      cy.get(`[data-cy="name"]`).type('jet wonderfully silently');
      cy.get(`[data-cy="name"]`).should('have.value', 'jet wonderfully silently');

      cy.get(`[data-cy="description"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="description"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        propertyCategory = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', propertyCategoryPageUrlPattern);
    });
  });
});
