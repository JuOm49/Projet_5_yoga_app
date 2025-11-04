import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed, fakeAsync, tick, discardPeriodicTasks } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { SessionService } from 'src/app/services/session.service';
import { UserService } from 'src/app/services/user.service';

import { expect } from '@jest/globals';

import { MeComponent } from './me.component';
import { of } from 'rxjs';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    },
    logOut: jest.fn()
  }

  const mockUserService = {
    getById: jest.fn().mockReturnValue(of({})),
    delete: jest.fn()
  }

  beforeEach(async () => {
    jest.clearAllMocks();

    // delete warnings for navigation outside Angular zone
    jest.spyOn(console, 'warn').mockImplementation(() => {});

    // Mock window.history.back
    Object.defineProperty(window, 'history', {
      value: { back: jest.fn() },
      writable: true
    });

    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        RouterTestingModule,
        NoopAnimationsModule // for disable animations of MatSnackBar
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: UserService, useValue: mockUserService }
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load user data on component initialization', () => {
    // ARRANGE
    const mockUser = { 
      id: 1, 
      firstName: 'Test', 
      lastName: 'User',
      email: 'test.user@test.com'
    };
    mockUserService.getById.mockReturnValue(of(mockUser));

    // ACT
    component.ngOnInit();

    // ASSERT
    expect(mockUserService.getById).toHaveBeenCalledWith('1');
    expect(component.user).toEqual(mockUser);
  });

  describe('when session information is defined', () => {
    it('should user property be defined', () => {
      expect(mockSessionService.sessionInformation).toBeDefined();
      expect(mockSessionService.sessionInformation.id).toBe(1);
    });

    it('should have admin rights', () => {
      expect(mockSessionService.sessionInformation?.admin).toBe(true);
    });

    it('should delete user, show matSnackBar, logOut and navigate to home', fakeAsync(() => {
      // ARRANGE
      mockUserService.delete.mockReturnValue(of({}));
      const matSnackBar = TestBed.inject(MatSnackBar);
      const router = TestBed.inject(Router);

      // Spies
      const navigateSpy = jest.spyOn(router, 'navigate');
      const snackBarSpy = jest.spyOn(matSnackBar, 'open');
      
      // ACT
      component.delete();
      tick(); // to force async operations to complete

      // ASSERT
      expect(mockUserService.delete).toHaveBeenCalledWith('1');
      expect(mockSessionService.logOut).toHaveBeenCalled();
      expect(snackBarSpy).toHaveBeenCalledWith(
        "Your account has been deleted !", 
        'Close', 
        { duration: 3000 }
      );
      expect(navigateSpy).toHaveBeenCalledWith(['/']);

      tick(3000); // to advance 3000ms to finish the MatSnackBar timer
      discardPeriodicTasks(); // Clean up any remaining periodic timers
    }));

    it('should call window.history.back when back is called', () => {
      // ARRANGE
      const locationSpy = jest.spyOn(window.history, 'back');
      // ACT
      component.back();
      // ASSERT
      expect(locationSpy).toHaveBeenCalled();
    });
  });
});
