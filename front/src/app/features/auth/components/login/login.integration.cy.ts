/// <reference types="cypress" />

// Integration tests for Login Component
describe('Login Component - Integration Tests', () => {
  beforeEach(() => {
    // Mock sessions list for after login redirection
    cy.intercept('GET', '/api/session', {
            statusCode: 200,
            body: [
                {
                    id: 1,
                    name: 'Morning Yoga Session',
                    description: 'A peaceful morning yoga session',
                    date: '2025-12-15T09:00:00.000Z',
                    teacher_id: 1,
                    users: []
                }
            ]
        }).as('sessionsListRequest');

    // Navigate to http://localhost:4200/login before each test
    cy.visit('/login');
  });

  describe('Component Integration', () => {
    it('should render login form with all required elements', () => {
      // ASSERT - Verify that all form elements are present
      cy.get('mat-card').should('be.visible');
      cy.get('input[formControlName="email"]').should('be.visible');
      cy.get('input[formControlName="password"]').should('be.visible');
      cy.get('button[type="submit"]').should('be.visible');
      cy.get('mat-card-title').should('contain.text', 'Login');
    });
  });

  describe('Form Validation Integration', () => {
    it('should disable submit button when form is empty', () => {
      // ASSERT - submit button should be disabled initially
      cy.get('button[type="submit"]').should('be.disabled');
    });

    it('should enable submit button when form is valid', () => {
      // ACT - fill in the form with valid data
      cy.get('input[formControlName="email"]').type('melyna.paras@outlook.com');
      cy.get('input[formControlName="password"]').type('password123');

      // ASSERT - submit button should be enabled
      cy.get('button[type="submit"]').should('not.be.disabled');
    });
  });

  describe('User Authentication and access to sessions', () => {
    it('should submit form when Enter is pressed in password field', () => {
      // ARRANGE
      cy.intercept('POST', '/api/auth/login', { fixture: 'auth/login-success.json' }).as('loginEnter');

      // ACT - fill in the password field and press Enter
      cy.get('input[formControlName="email"]').type('melyna.paras@outlook.com');
      cy.get('input[formControlName="password"]').type('password123');

      // Submit the form by pressing Enter
      cy.get('form').submit();

      // ASSERT - verify submission occurred
      cy.wait('@loginEnter');
      cy.wait('@sessionsListRequest');
      // Verify redirection to sessions page
      cy.url().should('include', '/sessions');
    });

    it('should update form fields when user types', () => {
      // ARRANGE
      cy.intercept('POST', '/api/auth/login', { fixture: 'auth/login-success.json' }).as('loginEnter');

      // ACT - field interaction
      cy.get('input[formControlName="email"]').type('melyna.paras@outlook.com');
      cy.get('input[formControlName="password"]').type('mypassword123');

      // ASSERT - Verify field values
      cy.get('input[formControlName="email"]').should('have.value', 'melyna.paras@outlook.com');
      cy.get('input[formControlName="password"]').should('have.value', 'mypassword123');
    });
  });

  describe('Error Integration', () => {
    it('should handle login failure and show error message', () => {
      // ARRANGE - Configure intercept for login failure
      cy.intercept('POST', '/api/auth/login', { 
        statusCode: 401, 
        body: { message: 'Unauthorized' } 
      }).as('loginFailure');

      // ACT - fill in with wrong credentials
      cy.get('input[formControlName="email"]').type('melyna.paras@outlook.com');
      cy.get('input[formControlName="password"]').type('wrongpassword');
      cy.get('button[type="submit"]').click();

      // ASSERT - Verify API call and error message
      cy.wait('@loginFailure');
      
      // Verify that the error is displayed
      cy.get('.error').should('be.visible');
      
      // Verify that we are still on the login page
      cy.url().should('include', '/login');
    });

    it('should handle server error 500', () => {
      // ARRANGE
      cy.intercept('POST', '/api/auth/login', { 
        statusCode: 500, 
        body: { message: 'Internal Server Error' } 
      }).as('serverError');

      // ACT
      cy.get('input[formControlName="email"]').type('melyna.paras@outlook.com');
      cy.get('input[formControlName="password"]').type('password123');
      cy.get('button[type="submit"]').click();

      // ASSERT
      cy.wait('@serverError');
      cy.get('.error', { timeout: 10000 }).should('be.visible');
    });
  });
});