// test for register and login
describe('save new user with register, then login and access sessions', () => {
  it('Register successful', () => {
    cy.visit('/register')

    cy.intercept('POST', '/api/auth/register', {
      body: {
        id: 1,
        username: 'YogaStudio',
        firstName: 'Yoga',
        lastName: 'Studio',
        email: 'yoga@studio.com',
        password: 'test!1234',
        admin: true
      },
    })

    cy.intercept(
      {
        method: 'POST',
        url: '/api/auth/login',
      },
      []).as('login')

    cy.get('input[formControlName=firstName]').type("Yoga")
    cy.get('input[formControlName=lastName]').type("Studio")
    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    cy.url().should('include', '/login');

    //connection with login after register
    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []).as('session')

    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    cy.wait('@login');
    cy.url().should('include', '/sessions');
  })
});