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

describe('City e2e test', () => {
  const cityPageUrl = '/city';
  const cityPageUrlPattern = new RegExp('/city(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const citySample = { name: 'quaintly trustworthy', latitude: 1274.63, longitude: 12241.15 };

  let city;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/cities+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/cities').as('postEntityRequest');
    cy.intercept('DELETE', '/api/cities/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (city) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/cities/${city.id}`,
      }).then(() => {
        city = undefined;
      });
    }
  });

  it('Cities menu should load Cities page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('city');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('City').should('exist');
    cy.url().should('match', cityPageUrlPattern);
  });

  describe('City page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(cityPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create City page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/city/new$'));
        cy.getEntityCreateUpdateHeading('City');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', cityPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/cities',
          body: citySample,
        }).then(({ body }) => {
          city = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/cities+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/cities?page=0&size=20>; rel="last",<http://localhost/api/cities?page=0&size=20>; rel="first"',
              },
              body: [city],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(cityPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details City page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('city');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', cityPageUrlPattern);
      });

      it('edit button click should load edit City page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('City');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', cityPageUrlPattern);
      });

      it.skip('edit button click should load edit City page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('City');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', cityPageUrlPattern);
      });

      it('last delete button click should delete instance of City', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('city').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', cityPageUrlPattern);

        city = undefined;
      });
    });
  });

  describe('new City page', () => {
    beforeEach(() => {
      cy.visit(`${cityPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('City');
    });

    it('should create an instance of City', () => {
      cy.get(`[data-cy="name"]`).type('dependent colon');
      cy.get(`[data-cy="name"]`).should('have.value', 'dependent colon');

      cy.get(`[data-cy="postalCode"]`).type('so');
      cy.get(`[data-cy="postalCode"]`).should('have.value', 'so');

      cy.get(`[data-cy="latitude"]`).type('30159.09');
      cy.get(`[data-cy="latitude"]`).should('have.value', '30159.09');

      cy.get(`[data-cy="longitude"]`).type('5103.06');
      cy.get(`[data-cy="longitude"]`).should('have.value', '5103.06');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        city = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', cityPageUrlPattern);
    });
  });
});
