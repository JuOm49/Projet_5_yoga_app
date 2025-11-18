import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionService } from './session.service';

describe('SessionService', () => {
  let mockService: SessionService;
  let mockUser: any;

  beforeEach(() => {
    mockUser = {
        token: 'test-token',
        type: 'Bearer',
        id: 1,
        username: 'testuser',
        firstName: 'Test',
        lastName: 'User',
        admin: false
      };

    TestBed.configureTestingModule({});
    mockService = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(mockService).toBeTruthy();
  });

  describe('Service initialization', () => {
    it('should initialize with isLogged as false', () => {
      expect(mockService.isLogged).toBe(false);
    });

    it('should initialize with sessionInformation as undefined', () => {
      expect(mockService.sessionInformation).toBeUndefined();
    });

    it('should initialize observable with false value', () => {
      //ARRANGE AND ACT
      const isLoggedObservable = mockService.$isLogged();
      //ASSERT
      isLoggedObservable.subscribe((isLogged) => {
        expect(isLogged).toBe(false);
      });
    });
  });

  describe('Observable behavior $isLogged', () => {
    it('should emit initial value immediately upon subscription', () => {
      // ARRANGE
      const mockCallback = jest.fn();
      // ACT
      mockService.$isLogged().subscribe(mockCallback);
      // ASSERT
      expect(mockCallback).toHaveBeenCalledTimes(1);
      expect(mockCallback).toHaveBeenCalledWith(false);
    });

    it('should emit true when user logs in', () => {
      // ARRANGE
      const emittedValues: boolean[] = [];
      // ACT
      mockService.$isLogged().subscribe(value => emittedValues.push(value));
      mockService.logIn(mockUser);
      // ASSERT
      expect(emittedValues).toEqual([false, true]); // Initial false, then true after login
    });

    it('should emit false when user logs out', () => {
      // ARRANGE
      const emittedValues: boolean[] = [];
      // ACT
      mockService.$isLogged().subscribe(value => emittedValues.push(value));
      mockService.logIn(mockUser);  // Login first
      mockService.logOut();         // Then logout
      // ASSERT
      expect(emittedValues).toEqual([false, true, false]); // Initial false, true after login, false after logout
    });

    it('should emit state changes to multiple subscribers', () => {
      // ARRANGE
      const subscriber1Values: boolean[] = [];
      const subscriber2Values: boolean[] = []; 
      // ACT
      mockService.$isLogged().subscribe(value => subscriber1Values.push(value));
      mockService.$isLogged().subscribe(value => subscriber2Values.push(value));
      mockService.logIn(mockUser);
      // ASSERT
      expect(subscriber1Values).toEqual([false, true]);
      expect(subscriber2Values).toEqual([false, true]);
    });
  });

    describe('Logout functionality', () => {
    it('should clear user information when logging out', () => {
      // ARRANGE - Login first
      const mockUser = {
        token: 'test-token',
        type: 'Bearer',
        id: 1,
        username: 'testuser',
        firstName: 'Test',
        lastName: 'User',
        admin: false
      };
      mockService.logIn(mockUser);
      
      // ACT
      mockService.logOut();

      // ASSERT
      expect(mockService.sessionInformation).toBeUndefined();
    });

    it('should set isLogged to false when logging out', () => {
      mockService.logIn(mockUser);
      // ACT
      mockService.logOut();
      // ASSERT
      expect(mockService.isLogged).toBe(false);
    });
  });
});
