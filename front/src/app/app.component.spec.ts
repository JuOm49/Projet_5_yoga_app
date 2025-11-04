import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { Router } from '@angular/router';

import { AppComponent } from './app.component';
import { of, firstValueFrom } from 'rxjs';
import { SessionService } from './services/session.service';


describe('AppComponent', () => {

  let mockSessionService: jest.Mocked<Partial<SessionService>>;

  beforeEach(async () => {

    mockSessionService = {
      $isLogged: jest.fn(),
      logOut: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatToolbarModule
      ],
      declarations: [
        AppComponent
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService }
      ]
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should inject all required services', () => {
  const fixture = TestBed.createComponent(AppComponent);
  const component = fixture.componentInstance;

  // ASSERT - Verify that services are injected
  expect(component['authService']).toBeDefined();
  expect(component['router']).toBeDefined();
  expect(component['sessionService']).toBeDefined();
});

  describe('when user is logged in', () => {
    it('should return true when user is logged', async () => {
      // ARRANGE - mock return true
      (mockSessionService.$isLogged as jest.Mock).mockReturnValue(of(true));
    
      const fixture = TestBed.createComponent(AppComponent);
      const component = fixture.componentInstance;

      // ACT
      const result = await firstValueFrom(component.$isLogged());

      // ASSERT
      expect(result).toBe(true);
    });

    it('should return false when user is not logged', async () => {
      // ARRANGE - mock return false
      (mockSessionService.$isLogged as jest.Mock).mockReturnValue(of(false));
    
      const fixture = TestBed.createComponent(AppComponent);
      const component = fixture.componentInstance;

      // ACT
      const result = await firstValueFrom(component.$isLogged());

      // ASSERT
      expect(result).toBe(false);
    });
  });

  describe('when user click on logout', () => {
    it('should call mockSessionService.logOut and navigate to home when logout is called', () => {
      // ARRANGE
      const fixture = TestBed.createComponent(AppComponent);
      const component = fixture.componentInstance;
      const router = TestBed.inject(Router);
  
      // Create spies for logOut and navigate methods
      const logOutSpy = jest.spyOn(mockSessionService, 'logOut');
      const navigateSpy = jest.spyOn(router, 'navigate');

      // ACT - Call the logout method
      component.logout();

      // ASSERT - Verify that the correct methods were called
      expect(logOutSpy).toHaveBeenCalled();
      expect(navigateSpy).toHaveBeenCalledWith(['']);
    });
  });
});