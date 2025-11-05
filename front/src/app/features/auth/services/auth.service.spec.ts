import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController, TestRequest } from '@angular/common/http/testing';

import { expect } from '@jest/globals';

import { RegisterRequest } from '../interfaces/registerRequest.interface';
import { LoginRequest } from '../interfaces/loginRequest.interface';

import { AuthService } from './auth.service';

describe('LoginRequestService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  const mockLoginRequest: LoginRequest = {
    email: 'test.LoginRequest@example.com',
    password: 'passwordTest1234'
  };

  const mockRegisterRequest: RegisterRequest = {
    firstName: 'Test',
    lastName: 'User',
    email: 'test.RegisterRequest@example.com',
    password: 'passwordTest1234'
};

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[
        HttpClientModule,
        HttpClientTestingModule
      ]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('register() method', () => {
        it('should register a new user', () => {
            // ARRANGE
            service.register(mockRegisterRequest).subscribe();
            // ASSERT
            const testRequest: TestRequest = httpMock.expectOne('api/auth/register');
            expect(testRequest.request.method).toBe('POST');
            expect(testRequest.request.body).toEqual(mockRegisterRequest);
            testRequest.flush({}); // simulate response
        });
    });

    describe('login() method', () => {
        it('should log in a user', () => {
            // ARRANGE
            service.login(mockLoginRequest).subscribe();
            // ASSERT
            const testRequest: TestRequest = httpMock.expectOne('api/auth/login');
            expect(testRequest.request.method).toBe('POST');
            expect(testRequest.request.body).toEqual(mockLoginRequest);
            testRequest.flush({}); // simulate response
        });
    });
});