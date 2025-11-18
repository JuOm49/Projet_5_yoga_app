/// <reference types="cypress" />

// E2E test for session navigation  
describe('Session E2E', () => {

    beforeEach(() => {
        // Ensure we're on the login page before each test
        cy.visit('/login');

        cy.get('input[formControlName="email"]').should('be.visible');
        cy.get('input[formControlName="password"]').should('be.visible');
        cy.get('button[type="submit"]').should('be.visible');

        cy.intercept('POST', '/api/auth/login', {
            statusCode: 200,
            body: {
                id: 1,
                username: 'YogaStudio',
                firstName: 'Yoga',
                lastName: 'Studio',
                admin: true
            }
        }).as('loginRequest');
    });

    it('should login and view session details', () => {
        // Mock sessions list response
        cy.intercept('GET', '/api/session', {
            statusCode: 200,
            body: [
            {
                id: 1,
                name: 'Yoga Class',
                description: 'A relaxing yoga class',
                date: '2023-10-01T10:00:00.000Z',
                teacher_id: 2,
                users: [],
                createdAt: '2025-12-15T00:00:00.000Z',
                updatedAt: '2025-12-15T00:00:00.000Z'
            },
            {
                id: 2,
                name: 'Other yoga session',
                description: 'An energizing yoga class',
                date: '2023-11-01T10:00:00.000Z',
                teacher_id: 2,
                users: [],
                createdAt: '2025-12-16T00:00:00.000Z',
                updatedAt: '2025-12-16T00:00:00.000Z'
            }
        ]
        }).as('sessionsList');

        // Mock session detail response
        cy.intercept('GET', '/api/session/1', {
            statusCode: 200,
            body: {
                id: 1,
                name: 'Yoga Class',
                description: 'A relaxing yoga class',
                date: '2023-10-01T10:00:00.000Z',
                teacher_name: 'Mathilde',
                users: [],
                createdAt: '2025-12-15T00:00:00.000Z',
                updatedAt: '2025-12-15T00:00:00.000Z'
            }
        }).as('sessionDetail');

        // Perform login
        cy.get('input[formControlName=email]').type("yoga@studio.com");
        cy.get('input[formControlName=password]').type("test!1234");
        
        // debug: verify submit button enabled
        cy.get('button[type="submit"]').should('not.be.disabled');
        cy.get('button[type="submit"]').click();

        cy.wait('@loginRequest');
        cy.url().should('include', '/sessions');

        // Navigate to session details
        cy.wait('@sessionsList');

        // Verify sessions are displayed
        cy.get('mat-card').should('be.visible');
        cy.contains('Yoga Class').should('be.visible');

        // Click on the first session to view details
        cy.get('mat-card').first().within(() => {
          cy.get('button').contains('Detail').click();
        });

        cy.wait('@sessionDetail');
        cy.url().should('include', '/sessions/detail/1');

        // Verify session details are displayed
        cy.get('h1').should('contain', 'Yoga Class');
        cy.contains('A relaxing yoga class').should('be.visible');
    });
});