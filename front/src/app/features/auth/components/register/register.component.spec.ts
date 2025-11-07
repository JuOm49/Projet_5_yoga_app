import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of, throwError } from 'rxjs';

import { AuthService } from '../../services/auth.service';
import { RegisterComponent } from './register.component';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let router: Router;

  const mockAuthService = {
    register: jest.fn().mockReturnValue(of({}))
  };

  beforeEach(async () => {
    jest.clearAllMocks();

    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      providers: [
        { provide: AuthService, useValue: mockAuthService }
      ],
      imports: [
        RouterTestingModule,
        NoopAnimationsModule,
        ReactiveFormsModule,  
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;

    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate');
    
    fixture.detectChanges();
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
        firstName: '',
        lastName: '',
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
      formGroup.controls['firstName'].setValue('John');
      formGroup.controls['lastName'].setValue('Doe');
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
      formGroup.controls['firstName'].setValue('user');
      formGroup.controls['lastName'].setValue('Test');
      //ACT
      const isValid = formGroup.valid;
      //ASSERT
      expect(isValid).toBeFalsy();
    });

    it('should mark the form as invalid when password is incorrect', () => {
      //ARRANGE
      const formGroup = component.form;
      formGroup.controls['email'].setValue('test.user@test.com');
      formGroup.controls['firstName'].setValue('user');
      formGroup.controls['lastName'].setValue('Test');
      formGroup.controls['password'].setValue('');
      //ACT - password without value, isValid should be false
      const isValid = formGroup.valid;
      //ASSERT
      expect(isValid).toBeFalsy();
    });

    it('should mark the form as invalid when lastName is missing', () => {
      //ARRANGE
      const formGroup = component.form;
      formGroup.controls['email'].setValue('test.user@test.com');
      formGroup.controls['firstName'].setValue('John');
      formGroup.controls['lastName'].setValue('');
      formGroup.controls['password'].setValue('password123');
      //ACT
      const isValid = formGroup.valid;
      //ASSERT
      expect(isValid).toBeFalsy();
    });

    
  });

  describe('submit method', () => {
    it('should call authService.login when form is valid', () => {
      //ARRANGE
      mockAuthService.register.mockReturnValue(of({})); 
      component.form.controls['email'].setValue('test.user@test.com');
      component.form.controls['password'].setValue('password123');
      component.form.controls['firstName'].setValue('Test');
      component.form.controls['lastName'].setValue('User');

      //ACT
      component.submit();
      
      //ASSERT
      expect(mockAuthService.register).toHaveBeenCalledWith({
        email: 'test.user@test.com',
        password: 'password123',
        firstName: 'Test',
        lastName: 'User'
      });
    });

    it('should call authService.login even when form is invalid', () => {
      //ARRANGE
      component.form.controls['email'].setValue('');
      component.form.controls['password'].setValue('');
      component.form.controls['firstName'].setValue('');
      component.form.controls['lastName'].setValue('');

      //ACT
      component.submit();
      
      //ASSERT - The service is called even with an invalid form
      expect(mockAuthService.register).toHaveBeenCalledWith({
        email: '',
        password: '',
        firstName: '',
        lastName: ''
      });
    });

    it('should handle login error and set onError to true', fakeAsync(() => {
      //ARRANGE
      component.form.controls['email'].setValue('test.user@test.com');
      component.form.controls['password'].setValue('password123');
      component.form.controls['firstName'].setValue('Test');
      component.form.controls['lastName'].setValue('User');
      
      //mock error response
      mockAuthService.register.mockReturnValue(throwError(() => new Error('Login failed')));
      
      //ACT
      component.submit();
      tick(); // Wait for observable completion
      
      //ASSERT
      expect(component.onError).toBe(true);
      expect(router.navigate).not.toHaveBeenCalled();
    }));

  });
});