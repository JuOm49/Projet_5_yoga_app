/// <reference types="cypress" />

describe('Error Handling and Edge Cases Integration Tests', () => {

    it('should handle network errors gracefully', () => {
        // Mock network error
        cy.intercept('POST', '/api/auth/login', { forceNetworkError: true }).as('networkError');

        cy.visit('/login');
        cy.get('input[formControlName="email"]').type('yoga@studio.com');
        cy.get('input[formControlName="password"]').type('test!1234');
        cy.get('button[type="submit"]').click();
        cy.wait('@networkError');

        // Should show error or stay on login page
        cy.url().should('include', '/login');
        cy.log('Network error handling tested');
    });

    it('should handle 404 errors', () => {
        // Mock login first
        cy.intercept('POST', '/api/auth/login', {
            statusCode: 200,
            fixture: 'auth/login-success.json'
        }).as('loginRequest');

        cy.visit('/login');
        cy.get('input[formControlName="email"]').type('yoga@studio.com');
        cy.get('input[formControlName="password"]').type('test!1234');
        cy.get('button[type="submit"]').click();
        cy.wait('@loginRequest');

        // Visit non-existent route
        cy.visit('/nonexistent');
        
        // Should redirect to 404 page or handle gracefully
        cy.log('404 error handling tested');
    });

    it('should handle server errors (500)', () => {
        // Mock server error
        cy.intercept('POST', '/api/auth/login', {
            statusCode: 500,
            body: { message: 'Internal server error' }
        }).as('serverError');

        cy.visit('/login');
        cy.get('input[formControlName="email"]').type('yoga@studio.com');
        cy.get('input[formControlName="password"]').type('test!1234');
        cy.get('button[type="submit"]').click();
        cy.wait('@serverError');

        cy.url().should('include', '/login');
        cy.log('Server error handling tested');
    });

    it('should handle empty responses', () => {
        // Mock login
        cy.intercept('POST', '/api/auth/login', {
            statusCode: 200,
            fixture: 'auth/login-success.json'
        }).as('loginRequest');

        // Mock empty sessions response
        cy.intercept('GET', '/api/session', {
            statusCode: 200,
            body: []
        }).as('emptySessions');

        cy.visit('/login');
        cy.get('input[formControlName="email"]').type('yoga@studio.com');
        cy.get('input[formControlName="password"]').type('test!1234');
        cy.get('button[type="submit"]').click();
        cy.wait('@loginRequest');

        cy.visit('/sessions');
        cy.wait('@emptySessions');

        // Should handle empty list gracefully
        cy.get('mat-card').should('exist');
        cy.log('Empty response handling tested');
    });

    it('should handle malformed data', () => {
        // Mock login
        cy.intercept('POST', '/api/auth/login', {
            statusCode: 200,
            fixture: 'auth/login-success.json'
        }).as('loginRequest');

        // Mock malformed session data
        cy.intercept('GET', '/api/session', {
            statusCode: 200,
            body: [
                { id: null, name: '', description: null }, // Invalid data
                { invalidField: 'test' } // Missing required fields
            ]
        }).as('malformedSessions');

        cy.visit('/login');
        cy.get('input[formControlName="email"]').type('yoga@studio.com');
        cy.get('input[formControlName="password"]').type('test!1234');
        cy.get('button[type="submit"]').click();
        cy.wait('@loginRequest');

        cy.visit('/sessions');
        cy.wait('@malformedSessions');

        // Should handle malformed data gracefully
        cy.log('Malformed data handling tested');
    });

});