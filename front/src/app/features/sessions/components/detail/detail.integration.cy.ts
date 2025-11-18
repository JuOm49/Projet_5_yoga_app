/// <reference types="cypress" />

describe('Session Detail Integration Tests', () => {

    beforeEach(() => {
        // Mock login
        cy.intercept('POST', '/api/auth/login', {
            statusCode: 200,
            body: {
                token: 'fake-jwt-token',
                type: 'Bearer',
                id: 1,
                username: 'yoga@studio.com',
                firstName: 'Yoga',
                lastName: 'Studio',
                admin: true
            }
        }).as('loginRequest');

        // Mock sessions list (the app uses GET /api/session for sessions)
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

        // Mock session detail
        cy.intercept('GET', '/api/session/1', {
            statusCode: 200,
            body: {
                id: 1,
                name: 'Morning Yoga Session',
                description: 'A peaceful morning yoga session',
                date: '2025-12-15T09:00:00.000Z',
                teacher_id: 1,
                users: [1]
            }
        }).as('sessionDetailRequest');

        // Login
        cy.visit('/login');
        cy.get('input[formControlName="email"]').type('yoga@studio.com');
        cy.get('input[formControlName="password"]').type('test!1234');
        cy.get('button[type="submit"]').click();
        cy.wait('@loginRequest');
        cy.wait('@sessionsListRequest');

        cy.url().should('include', '/sessions');
    });

    it('should display session detail when selecting a session from the list', () => {
        // Mock teacher
        cy.intercept('GET', '/api/teacher/1', {
            statusCode: 200,
            body: { id: 1, firstName: 'Elena', lastName: 'Perez' }
        }).as('teacherRequest');

        // Ensure sessions list is visible and click Detail for first session
        cy.url().should('include', '/sessions');
        cy.get('mat-card').should('exist');
        cy.get('mat-card').first().within(() => {
            cy.get('button').contains('Detail').click();
        });

        cy.url().should('include', '/sessions/detail/1');
        cy.wait('@sessionDetailRequest');
        cy.wait('@teacherRequest');

        // Verify component is visible
        cy.get('mat-card').should('exist');
        cy.contains('Morning Yoga Session').should('exist');
        cy.contains('A peaceful morning yoga session').should('exist');
    });

    it('should navigate back to sessions when back button clicked', () => {
        // Mock teacher
        cy.intercept('GET', '/api/teacher/1', {
            statusCode: 200,
            body: { id: 1, firstName: 'Elena', lastName: 'Perez' }
        }).as('teacherRequest');

        // Ensure sessions list is visible and click Detail for first session
        cy.url().should('include', '/sessions');
        cy.get('mat-card').should('exist');
        cy.get('mat-card').first().within(() => {
            cy.get('button').contains('Detail').click();
        });

        cy.url().should('include', '/sessions/detail/1');
        cy.wait('@sessionDetailRequest');
        cy.wait('@teacherRequest');

        // Verify component is visible
        cy.get('mat-card').should('exist');

        // Test back() function deterministically by clicking the icon's parent button
        cy.get('mat-icon').contains('arrow_back').closest('button').should('be.visible').click();
        cy.url().should('include', '/sessions');
        cy.log('Back function tested');
    });
    
    it('should allow user to participate in a session', () => {
        // Re-login as not-admin so participate button is visible
        cy.clearLocalStorage();
        cy.clearCookies();
        cy.intercept('POST', '/api/auth/login', {
            statusCode: 200,
            body: {
                token: 'fake-jwt-token',
                type: 'Bearer',
                id: 1,
                username: 'yoga@studio.com',
                firstName: 'Yoga',
                lastName: 'Studio',
                admin: false
            }
        }).as('loginNotAdmin');

        cy.visit('/login');
        
        cy.get('input[formControlName="email"]').type('yoga@studio.com');
        cy.get('input[formControlName="password"]').type('test!1234');
        cy.get('button[type="submit"]').click();

        cy.wait('@loginNotAdmin');
        cy.wait('@sessionsListRequest');
        cy.url().should('include', '/sessions');

        // Ensure session detail shows user NOT participating
        cy.intercept('GET', '/api/session/1', {
            statusCode: 200,
            body: {
                id: 1,
                name: 'Morning Yoga Session',
                description: 'A peaceful morning yoga session',
                date: '2025-12-15T09:00:00.000Z',
                teacher_id: 1,
                users: []
            }
        }).as('sessionDetailRequest');

        // Mock teacher and participate API
        cy.intercept('GET', '/api/teacher/1', {
            statusCode: 200,
            body: { id: 1, firstName: 'Elena', lastName: 'Perez' }
        }).as('teacherRequest');

        cy.intercept('POST', '/api/session/1/participate/1', {
            statusCode: 200
        }).as('participateRequest');

        // Open detail by clicking the search icon in the first card
        cy.get('mat-card').should('exist');
        cy.get('mat-card').first().within(() => {
            cy.get('mat-icon').contains('search').closest('button').click();
        });

        cy.url().should('include', '/sessions/detail/1');
        cy.wait('@sessionDetailRequest');
        cy.wait('@teacherRequest');

        // Click participate button via icon
        cy.get('mat-card').within(() => {
            cy.get('mat-icon').contains('person_add').closest('button').should('be.visible').click();
        });
        cy.wait('@participateRequest');

        // Basic assertion: detail view still present after participating
        cy.get('mat-card').should('exist');
    });
    
    it('should allow admin to delete a session and redirect to sessions', () => {
        // Mock teacher
        cy.intercept('GET', '/api/teacher/1', {
            statusCode: 200,
            body: { id: 1, firstName: 'Elena', lastName: 'Perez' }
        }).as('teacherRequest');

        // Mock call delete API
        cy.intercept('DELETE', '/api/session/1', {
            statusCode: 200
        }).as('deleteSessionRequest');

        // Ensure sessions list is visible and click Detail for first session as admin
        cy.url().should('include', '/sessions');
        cy.get('mat-card').should('exist');
        cy.get('mat-card').first().within(() => {
            cy.get('button').contains('Detail').click();
        });

        cy.url().should('include', '/sessions/detail/1');
        cy.wait('@sessionDetailRequest');
        cy.wait('@teacherRequest');

        // Click the delete button and assert API call and navigation
        cy.get('button').contains('Delete').should('be.visible').click();
        cy.wait('@deleteSessionRequest');
        cy.url().should('include', '/sessions');
    });

});