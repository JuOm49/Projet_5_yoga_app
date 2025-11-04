import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from '../../../../services/session.service';
import { TeacherService } from '../../../../services/teacher.service';
import { SessionApiService } from '../../services/session-api.service';
import { FormComponent } from './form.component';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let router: Router;
  let mockSessionService: any;
  let mockTeacherService: any;
  let mockSessionApiService: any;

  beforeEach(async () => {
    // init mocks
    mockSessionService = {
      sessionInformation: {
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
        RouterTestingModule,
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
    jest.spyOn(router, 'navigate');
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
      mockSessionService.sessionInformation.admin = true;
      
      // ACT
      component.ngOnInit();
      
      // ASSERT
      expect(router.navigate).not.toHaveBeenCalled();
    });

    it('should redirect non-admin users to sessions page', () => {
      // ARRANGE
      mockSessionService.sessionInformation.admin = false;
      
      // ACT
      component.ngOnInit();
      
      // ASSERT
      expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
    });
  });

  describe('Form Initialization', () => {
    beforeEach(() => {
      // S'assurer que l'utilisateur est admin pour ces tests
      mockSessionService.sessionInformation.admin = true;
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
      date: '2025-12-01',
      teacher_id: 1,
      description: 'A test session'
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
      date: '2025-12-01',
      teacher_id: 1,
      description: 'A test session'
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
    it('should validate form as valid when properly filled', () => {
      // ARRANGE
      component.ngOnInit();
      
      // ACT
      component.sessionForm?.patchValue({
        name: 'Yoga Session',
        date: '2025-12-06',
        teacher_id: '1',
        description: 'A relaxing yoga session'
      });
      
      // ASSERT
      expect(component.sessionForm?.valid).toBe(true);
    });
  });
});  