import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController, TestRequest } from '@angular/common/http/testing';

import { expect } from '@jest/globals';

import { User } from '../interfaces/user.interface';

import { UserService } from './user.service';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  const mockUser: User = {
    id: 10,
    firstName: 'Test',
    lastName: 'User',
    createdAt: new Date(),
    email: 'test.user@example.com'
  ,  admin: false,
    password: 'password123',
    updatedAt: new Date()
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[
        HttpClientModule,
        HttpClientTestingModule
      ]
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getById() method', () => {
      it('should return user details', () => {
        // ARRANGE
        const userId = '10';
        // ACT
        service.getById(userId).subscribe(user => {
          // ASSERT with user details
          expect(user).toEqual(mockUser);
          expect(user.id).toBe(10);
        });
        // ASSERT
        const testRequest: TestRequest = httpMock.expectOne(`api/user/${userId}`);
        expect(testRequest.request.method).toBe('GET');
        testRequest.flush(mockUser);
      });

      it('should handle 404 error', () => {
        // ARRANGE
        const userId = '50';
        // ACT
        service.getById(userId).subscribe({
          next: () => {
            // This block should not be executed for 404 error
            fail('Expected 404 error, but got a user');
          },
          error: (error) => {
            // ASSERT
            expect(error.status).toBe(404);
          }
        });
        // ASSERT
        const testRequest: TestRequest = httpMock.expectOne(`api/user/${userId}`);
        expect(testRequest.request.method).toBe('GET');
        testRequest.flush('User not found', { status: 404, statusText: 'Not Found' });
      });
    });

    describe('delete() method', () => {
      it('should delete user successfully', () => {
        // ARRANGE
        const userId = '10';
        // ACT
        service.delete(userId).subscribe(response => {
          // ASSERT
          expect(response).toEqual({});
        });
        // ASSERT
        const testRequest: TestRequest = httpMock.expectOne(`api/user/${userId}`);
        expect(testRequest.request.method).toBe('DELETE');
        testRequest.flush({}); // simulate empty response
      });
    
      it('should handle delete error', () => {
        // ARRANGE
        const userId = '50';
        // ACT
        service.delete(userId).subscribe({
          next: () => {
            // This block should not be executed for error
            fail('Expected error, but delete succeeded');
          },
          error: (error) => {
            // ASSERT with error status
            expect(error.status).toBe(500);
          }
        });
        // ASSERT
        const testRequest: TestRequest = httpMock.expectOne(`api/user/${userId}`);
        expect(testRequest.request.method).toBe('DELETE');
        testRequest.flush('Delete failed', { status: 500, statusText: 'Server Error' });
      });
    });
});
