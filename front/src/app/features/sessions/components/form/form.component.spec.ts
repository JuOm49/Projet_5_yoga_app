import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';

import { of, Observable } from 'rxjs';

import { expect } from '@jest/globals';

import { SessionService } from '../../../../services/session.service';
import { TeacherService } from '../../../../services/teacher.service';
import { SessionApiService } from '../../services/session-api.service';
import { SessionInformation } from '../../../../interfaces/sessionInformation.interface';
import { Teacher } from '../../../../interfaces/teacher.interface';
import { Session } from '../../interfaces/session.interface';

import { FormComponent } from './form.component';

// types for mocks
type MockSessionService = {
  sessionInformation: SessionInformation | undefined;
};

type MockTeacherService = {
  all: jest.Mock<Observable<Teacher[]>>;
};

type MockSessionApiService = {
  detail: jest.Mock<Observable<Session>>;
  create: jest.Mock<Observable<Session>>;
  update: jest.Mock<Observable<Session>>;
};

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let router: Router;
  let matSnackBar: MatSnackBar;
  let mockSessionService: MockSessionService;
  let mockTeacherService: MockTeacherService;
  let mockSessionApiService: MockSessionApiService;

  beforeEach(async () => {

    // init mocks
    mockSessionService = {
      sessionInformation: {
        token: 'test-token',
        type: 'Bearer',
        id: 1,
        username: 'testuser',
        firstName: 'Test',
        lastName: 'User',
        admin: true
      }
    };

    mockTeacherService = {
      all: jest.fn().mockReturnValue(of([
        { id: 1, firstName: 'Stéphane', lastName: 'Pavard' },
        { id: 2, firstName: 'Marie', lastName: 'Curie' }
      ]))
    };

    mockSessionApiService = {
      detail: jest.fn(),
      create: jest.fn(),
      update: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [FormComponent],
      imports: [
        RouterTestingModule.withRoutes([
          { path: '', redirectTo: '/sessions', pathMatch: 'full' },
          { path: 'login', component: FormComponent },
          { path: 'register', component: FormComponent },
          { path: 'sessions', component: FormComponent },
          { path: 'sessions/detail/:id', component: FormComponent },
          { path: 'sessions/create', component: FormComponent },
          { path: 'sessions/update/:id', component: FormComponent },
          { path: 'me', component: FormComponent },
          { path: '**', redirectTo: '/sessions' }
        ]),
        ReactiveFormsModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        MatSnackBarModule,
        MatSelectModule,
        NoopAnimationsModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: SessionApiService, useValue: mockSessionApiService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    matSnackBar = TestBed.inject(MatSnackBar);
    jest.spyOn(router, 'navigate');
    jest.spyOn(matSnackBar, 'open');
  });

  // cleanup
  afterEach(() => {
    jest.restoreAllMocks();
  });

  describe('Component Initialization', () => {
    it('should create the component successfully', () => {
      expect(component).toBeTruthy();
    });

    it('should load the list of teachers on component creation', () => {
      expect(component.teachers$).toBeDefined();
      expect(mockTeacherService.all).toHaveBeenCalled();
    });

    it('should initialize with onUpdate as false by default', () => {
      expect(component.onUpdate).toBe(false);
    });
  });

  describe('Admin Access Control', () => {
    it('should allow access for admin users', () => {
      // ARRANGE
      if (mockSessionService.sessionInformation) {
        mockSessionService.sessionInformation.admin = true;
      }
      
      // ACT
      component.ngOnInit();
      
      // ASSERT
      expect(router.navigate).not.toHaveBeenCalled();
    });

    it('should redirect non-admin users to sessions page', () => {
      // ARRANGE
      if (mockSessionService.sessionInformation) {
        mockSessionService.sessionInformation.admin = false;
      }
      
      // ACT
      component.ngOnInit();
      
      // ASSERT
      expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
    });
  });

  describe('Form Initialization', () => {
    beforeEach(() => {
      // Ensure admin access for these tests
      if (mockSessionService.sessionInformation) {
        mockSessionService.sessionInformation.admin = true;
      }
    });

    it('should initialize form with empty values for new session', () => {
      // ARRANGE & ACT
      component.ngOnInit();
      
      // ASSERT
      expect(component.sessionForm).toBeDefined();
      expect(component.sessionForm?.value).toEqual({
        name: '',
        description: '',
        date: '',
        teacher_id: ''
      });
    });

    it('should create form with required validators', () => {
      // ARRANGE & ACT
      component.ngOnInit();
      
      // ASSERT
      const form = component.sessionForm;
      expect(form?.get('name')?.hasError('required')).toBe(true);
      expect(form?.get('date')?.hasError('required')).toBe(true);
      expect(form?.get('teacher_id')?.hasError('required')).toBe(true);
      expect(form?.get('description')?.hasError('required')).toBe(true);
    });

    it('should validate form as invalid when empty', () => {
      // ARRANGE & ACT
      component.ngOnInit();
      
      // ASSERT
      expect(component.sessionForm?.valid).toBe(false);
    });
  });

  describe('Update Mode', () => {
  it('should set onUpdate to true when URL contains update', () => {
    // ARRANGE
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/update/1');
    mockSessionApiService.detail.mockReturnValue(of({
      id: 1,
      name: 'Test Session',
      date: new Date('2025-12-01'),
      teacher_id: 1,
      description: 'A test session',
      users: [1, 2, 3]
    }));  
    // ACT
    component.ngOnInit();
    // ASSERT
    expect(component.onUpdate).toBe(true);
  });
  
  it('should load existing session data for editing', () => {
    // ARRANGE
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/update/1');
    mockSessionApiService.detail.mockReturnValue(of({
      id: 1,
      name: 'Test Session',
      date: new Date('2025-12-01'),
      teacher_id: 1,
      description: 'A test session',
      users: [1, 2, 3]
    }));
    // ACT
    component.ngOnInit();
    // ASSERT
    expect(component.sessionForm?.value).toEqual({
      name: 'Test Session',
      date: '2025-12-01',
      teacher_id: 1,
      description: 'A test session'
    });
  });
});

  describe('Form Submission', () => {
    beforeEach(() => {
      if (mockSessionService.sessionInformation) {
        mockSessionService.sessionInformation.admin = true;
      }
      component.ngOnInit();
    });

    it('should validate form as valid when properly filled', () => {
      // ARRANGE & ACT
      component.sessionForm?.patchValue({
        name: 'Yoga Session',
        date: '2025-12-06',
        teacher_id: '1',
        description: 'A relaxing yoga session'
      });
      
      // ASSERT
      expect(component.sessionForm?.valid).toBe(true);
    });

    it('should not validate form when required fields are missing', () => {
      // ARRANGE & ACT
      component.sessionForm?.patchValue({
        name: '',
        date: '',
        teacher_id: '',
        description: ''
      });

      // ASSERT
      expect(component.sessionForm?.valid).toBe(false);
      expect(mockSessionApiService.create).not.toHaveBeenCalled();
      expect(mockSessionApiService.update).not.toHaveBeenCalled();
      expect(matSnackBar.open).not.toHaveBeenCalled();
      expect(router.navigate).not.toHaveBeenCalled();
    });

    it('should create new session when not in update mode', () => {
      // ARRANGE
      const mockSession = { 
        name: 'Test', 
        date: new Date('2025-11-12'), 
        teacher_id: 1, 
        description: 'Test',
        users: []
      };
      mockSessionApiService.create.mockReturnValue(of(mockSession));
      component.sessionForm?.patchValue({
        name: mockSession.name,
        date: '2025-11-12',
        teacher_id: mockSession.teacher_id.toString(),
        description: mockSession.description
      });
      component.onUpdate = false;
      
      // ACT
      component.submit();

      // ASSERT
      expect(mockSessionApiService.create).toHaveBeenCalledWith({
        name: 'Test',
        date: '2025-11-12',
        teacher_id: '1',
        description: 'Test'
      });
    });

    it('should update existing session when in update mode', () => {
      // ARRANGE
      const mockSession = { 
        name: 'Updated', 
        date: new Date('2025-12-01'), 
        teacher_id: 1, 
        description: 'Updated',
        users: []
      };
      mockSessionApiService.update.mockReturnValue(of(mockSession));
      component.sessionForm?.patchValue({
        name: mockSession.name,
        date: '2025-12-01',
        teacher_id: mockSession.teacher_id.toString(),
        description: mockSession.description
      });
      component.onUpdate = true;
      component['id'] = '1';
      
      // ACT
      component.submit();

      // ASSERT
      expect(mockSessionApiService.update).toHaveBeenCalledWith('1', {
        name: 'Updated',
        date: '2025-12-01',
        teacher_id: '1',
        description: 'Updated'
      });
    });

    it('should handle submit when sessionForm is undefined', () => {
      // ARRANGE
      component.sessionForm = undefined;  
      // ACT & ASSERT
      expect(() => component.submit()).toThrow('Cannot read properties of undefined');
    });

    it('should show success message and redirect after creating session', () => {
      // ARRANGE
      const mockSession = { 
        name: 'Test', 
        date: new Date('2025-12-01'), 
        teacher_id: 1, 
        description: 'Test',
        users: []
      };
      mockSessionApiService.create.mockReturnValue(of(mockSession));
      component.sessionForm?.patchValue({
        name: mockSession.name,
        date: '2025-12-01',
        teacher_id: mockSession.teacher_id.toString(),
        description: mockSession.description
      });
      component.onUpdate = false;
      
      // ACT
      component.submit();

      // ASSERT
      expect(matSnackBar.open).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
      expect(router.navigate).toHaveBeenCalledWith(['sessions']);
    });

    it('should show success message and redirect after updating session', () => {
      // ARRANGE
      const mockSession = { 
        name: 'Updated', 
        date: new Date('2025-12-01'), 
        teacher_id: 1, 
        description: 'Updated',
        users: []
      };
      mockSessionApiService.update.mockReturnValue(of(mockSession));
      component.sessionForm?.patchValue({
        name: mockSession.name,
        date: '2025-12-01',
        teacher_id: mockSession.teacher_id.toString(),
        description: mockSession.description
      });
      component.onUpdate = true;
      component['id'] = '1';
      
      // ACT
      component.submit();

      // ASSERT
      expect(matSnackBar.open).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
      expect(router.navigate).toHaveBeenCalledWith(['sessions']);
    });

  });
});  