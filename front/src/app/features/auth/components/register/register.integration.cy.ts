/// <reference types="cypress" />

describe('Register Component Integration Tests', () => {

  beforeEach(() => {
    cy.visit('/register');
  });

  it('should display all form fields and submit button', () => {
    // Verify all form elements are present
    cy.get('input[formControlName="firstName"]').should('be.visible');
    cy.get('input[formControlName="lastName"]').should('be.visible');
    cy.get('input[formControlName="email"]').should('be.visible');
    cy.get('input[formControlName="password"]').should('be.visible');
    cy.get('button[type="submit"]').should('be.visible');
    cy.get('button[type="submit"]').should('contain.text', 'Submit');
    cy.get('mat-card-title').should('contain.text', 'Register');
  });

  it('should have submit button disabled when form is empty', () => {
    cy.get('button[type="submit"]').should('be.disabled');
  });

  it('should validate required fields progressively', () => {
    // Try to submit empty form (button should be disabled)
    cy.get('button[type="submit"]').should('be.disabled');

    // Fill only firstName
    cy.get('input[formControlName="firstName"]').type('Mathilde');
    cy.get('button[type="submit"]').should('be.disabled');

    // Fill firstName and lastName
    cy.get('input[formControlName="lastName"]').type('Kara');
    cy.get('button[type="submit"]').should('be.disabled');

    // Fill email too
    cy.get('input[formControlName="email"]').type('mathilde.kara@outlook.com');
    cy.get('button[type="submit"]').should('be.disabled');

    // Fill password - now form should be valid
    cy.get('input[formControlName="password"]').type('valid-password12345');
    cy.get('button[type="submit"]').should('not.be.disabled');
  });

  it('should validate email format', () => {
    // Fill all fields except email with invalid format
    cy.get('input[formControlName="firstName"]').type('Mathilde');
    cy.get('input[formControlName="lastName"]').type('Kara');
    cy.get('input[formControlName="email"]').type('invalidEmail');
    cy.get('input[formControlName="password"]').type('valid-password12345');

    // Submit button should be disabled due to invalid email
    cy.get('button[type="submit"]').should('be.disabled');

    // Fix email format
    cy.get('input[formControlName="email"]').clear().type('mathilde.kara@outlook.com');
    cy.get('button[type="submit"]').should('not.be.disabled');
  });

  it('should complete successful registration and redirect to login', () => {
    // Mock the register API call
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200,
      body: {}
    }).as('registerApi');

    // Fill the registration form
    cy.get('input[formControlName="firstName"]').type('Mathilde');
    cy.get('input[formControlName="lastName"]').type('Kara');
    cy.get('input[formControlName="email"]').type('mathilde.kara@outlook.com');
    cy.get('input[formControlName="password"]').type('valid-password12345');

    // Submit the form
    cy.get('button[type="submit"]').click();

    // Verify API call was made
    cy.wait('@registerApi').then((interception) => {
      expect(interception.request.body).to.deep.equal({
        firstName: 'Mathilde',
        lastName: 'Kara',
        email: 'mathilde.kara@outlook.com',
        password: 'valid-password12345'
      });
    });

    // Verify redirection to login page
    cy.url().should('include', '/login');
  });

  it('should handle registration error (400 - Bad Request)', () => {
    // Mock API with error
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: { message: 'Email already exists' }
    }).as('registerError');

    // Fill and submit form
    cy.get('input[formControlName="firstName"]').type('Mathilde');
    cy.get('input[formControlName="lastName"]').type('Kara');
    cy.get('input[formControlName="email"]').type('existing@email.com');
    cy.get('input[formControlName="password"]').type('valid-password12345');

    cy.get('button[type="submit"]').click();

    // Verify error handling
    cy.wait('@registerError');
    cy.get('.error').should('be.visible').should('contain.text', 'An error occurred');
    cy.url().should('include', '/register');
  });

  it('should handle server error (500)', () => {
    // Mock server error
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 500,
      body: { message: 'Internal server error' }
    }).as('registerServerError');

    // Fill and submit form
    cy.get('input[formControlName="firstName"]').type('Mathilde');
    cy.get('input[formControlName="lastName"]').type('Kara');
    cy.get('input[formControlName="email"]').type('mathilde.kara@outlook.com');
    cy.get('input[formControlName="password"]').type('valid-password12345');

    cy.get('button[type="submit"]').click();

    // Verify error handling
    cy.wait('@registerServerError');
    cy.get('.error').should('be.visible').should('contain.text', 'An error occurred');
    cy.url().should('include', '/register');
  });

  it('should handle network timeout error', () => {
    // Mock network error
    cy.intercept('POST', '/api/auth/register', { forceNetworkError: true }).as('networkError');

    // Fill and submit form
    cy.get('input[formControlName="firstName"]').type('Mathilde');
    cy.get('input[formControlName="lastName"]').type('Kara');
    cy.get('input[formControlName="email"]').type('mathilde.kara@outlook.com');
    cy.get('input[formControlName="password"]').type('valid-password12345');

    cy.get('button[type="submit"]').click();

    // Verify error handling
    cy.wait('@networkError');
    cy.get('.error').should('be.visible').should('contain.text', 'An error occurred');
  });

  it('should reset error state when form is resubmitted after error', () => {
    // Mock error first, then success
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: { message: 'bad request' }
    }).as('registerError');

    // Fill and submit to get error
    cy.get('input[formControlName="firstName"]').type('Mathilde');
    cy.get('input[formControlName="lastName"]').type('Kara');
    cy.get('input[formControlName="email"]').type('mathilde.kara@outlook.com');
    cy.get('input[formControlName="password"]').type('valid-password12345');
    cy.get('button[type="submit"]').click();

    cy.wait('@registerError');
    cy.get('.error').should('be.visible');

    // Mock success for retry
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200,
      body: {}
    }).as('registerSuccess');

    // Change email and retry
    cy.get('input[formControlName="email"]').clear().type('different@email.com');
    cy.get('button[type="submit"]').click();

    cy.wait('@registerSuccess');
    cy.url().should('include', '/login');
  });

  it('should handle empty string inputs correctly', () => {
    // Fill form, then clear all fields
    cy.get('input[formControlName="firstName"]').type('Mathilde').clear();
    cy.get('input[formControlName="lastName"]').type('Kara').clear();
    cy.get('input[formControlName="email"]').type('mathilde.kara@outlook.com').clear();
    cy.get('input[formControlName="password"]').type('valid-password12345').clear();

    // Button should be disabled
    cy.get('button[type="submit"]').should('be.disabled');
  });

  it('should test form value extraction and submission flow', () => {
    // Mock successful registration
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200,
      body: {}
    }).as('registerSuccess');

    // Fill form with specific values to test form.value extraction
    cy.get('input[formControlName="firstName"]').type('Test');
    cy.get('input[formControlName="lastName"]').type('User');
    cy.get('input[formControlName="email"]').type('test.user@example.com');
    cy.get('input[formControlName="password"]').type('TestPassword123');

    // Ensure form is valid before submit
    cy.get('button[type="submit"]').should('not.be.disabled');

    // Submit and verify the exact data sent
    cy.get('button[type="submit"]').click();

    cy.wait('@registerSuccess').then((interception) => {
      // This tests the submit() method and form.value as RegisterRequest casting
      expect(interception.request.body).to.have.property('firstName', 'Test');
      expect(interception.request.body).to.have.property('lastName', 'User');
      expect(interception.request.body).to.have.property('email', 'test.user@example.com');
      expect(interception.request.body).to.have.property('password', 'TestPassword123');
    });

    // Verify navigation happens (tests router.navigate)
    cy.url().should('include', '/login');
  });

});