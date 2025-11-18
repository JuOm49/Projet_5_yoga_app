import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';

import { AuthService } from '../../services/auth.service';

import { LoginComponent } from './login.component';
import { of, throwError } from 'rxjs';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let router: Router;

  const mockAuthService = {
    login: jest.fn().mockReturnValue(of({}))
  };

  beforeEach(async () => {
    jest.clearAllMocks();

    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        SessionService,
        { provide: AuthService, useValue: mockAuthService }
      ],
      imports: [
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule]
    })
      .compileComponents();
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    
    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate');
    
    fixture.detectChanges();
  });

  // cleanup
  afterEach(() => {
    jest.restoreAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('initialization form group', () => {
    it('should initialize the form group with empty values', () => {
      //ARRANGE
      const formGroup = component.form;
      //ACT
      const formValues = formGroup.value;
      //ASSERT
      expect(formValues).toEqual({
        email: '',
        password: ''
      });
    });
  });
    
  describe('form validation', () => {
    it('should mark the form as invalid when empty', () => {
      //ARRANGE
      const formGroup = component.form;
      //ACT - formGroup is empty initially, isValid should be false
      const isValid = formGroup.valid;
      //ASSERT
      expect(isValid).toBeFalsy();
    });

    it('should mark the form as valid when filled correctly', () => {
      //ARRANGE
      const formGroup = component.form;
      formGroup.controls['email'].setValue('test.user@test.com');
      formGroup.controls['password'].setValue('password123');
      //ACT
      const isValid = formGroup.valid;
      //ASSERT
      expect(isValid).toBeTruthy();
    });

    it('should mark the form as invalid when email is incorrect', () => {
      //ARRANGE
      const formGroup = component.form;
      formGroup.controls['email'].setValue('invalid');
      formGroup.controls['password'].setValue('password123');
      //ACT
      const isValid = formGroup.valid;
      //ASSERT
      expect(isValid).toBeFalsy();
    });

    it('should mark the form as invalid when password is incorrect', () => {
      //ARRANGE
      const formGroup = component.form;
      formGroup.controls['email'].setValue('test.user@test.com');
      formGroup.controls['password'].setValue('');
      //ACT - password without value, isValid should be false
      const isValid = formGroup.valid;
      //ASSERT
      expect(isValid).toBeFalsy();
    });
  });

  describe('submit method', () => {
    it('should call authService.login when form is valid', () => {
      //ARRANGE
      mockAuthService.login.mockReturnValue(of({})); 
      component.form.controls['email'].setValue('test.user@test.com');
      component.form.controls['password'].setValue('password123');

      //ACT
      component.submit();
      
      //ASSERT
      expect(mockAuthService.login).toHaveBeenCalledWith({
        email: 'test.user@test.com',
        password: 'password123'
      });
    });

    it('should call authService.login even when form is invalid', () => {
      //ARRANGE
      component.form.controls['email'].setValue('');
      component.form.controls['password'].setValue('');

      //ACT
      component.submit();
      
      //ASSERT - The service is called even with an invalid form
      expect(mockAuthService.login).toHaveBeenCalledWith({
        email: '',
        password: ''
      });
    });

    it('should handle login error and set onError to true', fakeAsync(() => {
      //ARRANGE
      component.form.controls['email'].setValue('test.user@test.com');
      component.form.controls['password'].setValue('password123');
      
      //mock error response
      mockAuthService.login.mockReturnValue(throwError(() => new Error('Login failed')));
      
      //ACT
      component.submit();
      tick(); // Wait for observable completion
      
      //ASSERT
      expect(component.onError).toBe(true);
      expect(router.navigate).not.toHaveBeenCalled();
    }));

  });
});
