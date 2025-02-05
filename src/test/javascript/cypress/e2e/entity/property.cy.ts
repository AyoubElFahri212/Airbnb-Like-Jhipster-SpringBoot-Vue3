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

describe('Property e2e test', () => {
  const propertyPageUrl = '/property';
  const propertyPageUrlPattern = new RegExp('/property(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const propertySample = {
    title: 'gown sore ugly',
    description: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
    pricePerNight: 16096.97,
    address: 'whoever per pantyhose',
    numberOfRooms: 16719,
    instantBook: true,
    cancellationPolicy: 'forenenst',
    isActive: false,
  };

  let property;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/properties+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/properties').as('postEntityRequest');
    cy.intercept('DELETE', '/api/properties/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (property) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/properties/${property.id}`,
      }).then(() => {
        property = undefined;
      });
    }
  });

  it('Properties menu should load Properties page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('property');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Property').should('exist');
    cy.url().should('match', propertyPageUrlPattern);
  });

  describe('Property page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(propertyPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Property page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/property/new$'));
        cy.getEntityCreateUpdateHeading('Property');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', propertyPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/properties',
          body: propertySample,
        }).then(({ body }) => {
          property = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/properties+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/properties?page=0&size=20>; rel="last",<http://localhost/api/properties?page=0&size=20>; rel="first"',
              },
              body: [property],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(propertyPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Property page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('property');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', propertyPageUrlPattern);
      });

      it('edit button click should load edit Property page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Property');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', propertyPageUrlPattern);
      });

      it.skip('edit button click should load edit Property page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Property');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', propertyPageUrlPattern);
      });

      it('last delete button click should delete instance of Property', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('property').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', propertyPageUrlPattern);

        property = undefined;
      });
    });
  });

  describe('new Property page', () => {
    beforeEach(() => {
      cy.visit(`${propertyPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Property');
    });

    it('should create an instance of Property', () => {
      cy.get(`[data-cy="title"]`).type('cleave er though');
      cy.get(`[data-cy="title"]`).should('have.value', 'cleave er though');

      cy.get(`[data-cy="description"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="description"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="pricePerNight"]`).type('10633.49');
      cy.get(`[data-cy="pricePerNight"]`).should('have.value', '10633.49');

      cy.get(`[data-cy="address"]`).type('bleakly boohoo');
      cy.get(`[data-cy="address"]`).should('have.value', 'bleakly boohoo');

      cy.get(`[data-cy="latitude"]`).type('4218.48');
      cy.get(`[data-cy="latitude"]`).should('have.value', '4218.48');

      cy.get(`[data-cy="longitude"]`).type('21644.2');
      cy.get(`[data-cy="longitude"]`).should('have.value', '21644.2');

      cy.get(`[data-cy="numberOfRooms"]`).type('18905');
      cy.get(`[data-cy="numberOfRooms"]`).should('have.value', '18905');

      cy.get(`[data-cy="numberOfBathrooms"]`).type('30814');
      cy.get(`[data-cy="numberOfBathrooms"]`).should('have.value', '30814');

      cy.get(`[data-cy="maxGuests"]`).type('15317');
      cy.get(`[data-cy="maxGuests"]`).should('have.value', '15317');

      cy.get(`[data-cy="propertySize"]`).type('21394');
      cy.get(`[data-cy="propertySize"]`).should('have.value', '21394');

      cy.get(`[data-cy="availabilityStart"]`).type('2025-02-05T01:19');
      cy.get(`[data-cy="availabilityStart"]`).blur();
      cy.get(`[data-cy="availabilityStart"]`).should('have.value', '2025-02-05T01:19');

      cy.get(`[data-cy="availabilityEnd"]`).type('2025-02-04T22:16');
      cy.get(`[data-cy="availabilityEnd"]`).blur();
      cy.get(`[data-cy="availabilityEnd"]`).should('have.value', '2025-02-04T22:16');

      cy.get(`[data-cy="instantBook"]`).should('not.be.checked');
      cy.get(`[data-cy="instantBook"]`).click();
      cy.get(`[data-cy="instantBook"]`).should('be.checked');

      cy.get(`[data-cy="minimumStay"]`).type('29004');
      cy.get(`[data-cy="minimumStay"]`).should('have.value', '29004');

      cy.get(`[data-cy="cancellationPolicy"]`).type('tabletop for longingly');
      cy.get(`[data-cy="cancellationPolicy"]`).should('have.value', 'tabletop for longingly');

      cy.get(`[data-cy="houseRules"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="houseRules"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="isActive"]`).should('not.be.checked');
      cy.get(`[data-cy="isActive"]`).click();
      cy.get(`[data-cy="isActive"]`).should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        property = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', propertyPageUrlPattern);
    });
  });
});
