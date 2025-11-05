import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { expect } from '@jest/globals';

import { SessionService } from '../../../../services/session.service';
import { TeacherService } from '../../../../services/teacher.service';
import { SessionApiService } from '../../services/session-api.service';

import { DetailComponent } from './detail.component';
import { Observable, of } from 'rxjs';
import { Session } from '../../interfaces/session.interface';
import { Teacher } from '../../../../interfaces/teacher.interface';

// Types pour les mocks
type SessionApiServiceMock = {
  delete: jest.Mock<Observable<any>>;
  participate: jest.Mock<Observable<void>>;
  unParticipate: jest.Mock<Observable<void>>;
  detail: jest.Mock<Observable<Session>>;
};

type TeacherServiceMock = {
  detail: jest.Mock<Observable<Teacher>>;
};

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>; 
  let mockSessionApiService: SessionApiServiceMock;
  let mockTeacherService: TeacherServiceMock;
  let router: Router;
  let matSnackBar: MatSnackBar;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    }
  };

  const mockSession: Session = {
    id: 1,
    name: 'Test Session',
    description: 'Test Description',
    date: new Date('2025-12-01'),
    teacher_id: 1,
    users: [1, 2, 3]
  };

  const mockTeacher: Teacher = {
    id: 1,
    firstName: 'Test',
    lastName: 'Teacher',
    createdAt: new Date(),
    updatedAt: new Date()
  };

  beforeEach(async () => {
    jest.clearAllMocks();

    // Mock window.history.back
    Object.defineProperty(window, 'history', {
      value: { back: jest.fn() },
      writable: true
    });

    mockSessionApiService = {
      delete: jest.fn().mockReturnValue(of({})),
      participate: jest.fn().mockReturnValue(of(undefined)),
      unParticipate: jest.fn().mockReturnValue(of(undefined)),
      detail: jest.fn().mockReturnValue(of(mockSession))
    };

    mockTeacherService = {
      detail: jest.fn().mockReturnValue(of(mockTeacher))
    };

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          { path: '', redirectTo: '/sessions', pathMatch: 'full' }
        ]),
        HttpClientModule,
        MatSnackBarModule,
        MatCardModule,
        MatIconModule,
        MatButtonModule,
        NoopAnimationsModule,
        ReactiveFormsModule
      ],
      declarations: [DetailComponent], 
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: jest.fn().mockReturnValue('1')
              }
            }
          }
        }
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    matSnackBar = TestBed.inject(MatSnackBar);
    
    jest.spyOn(router, 'navigate');
    jest.spyOn(matSnackBar, 'open');
    
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Component Initialization', () => {
    it('should have session detail from sessionApiService', () => {
      //ASSERT
      expect(component.session).toBeTruthy();
      expect(mockSessionApiService.detail).toHaveBeenCalledWith('1');
    });

    it('should load teacher information', () => {
      //ASSERT
      expect(component.teacher).toBeTruthy();
      expect(mockTeacherService.detail).toHaveBeenCalledWith('1');
    });
  });

  describe('Participation Management', () => {
    beforeEach(() => {
      component.session = mockSession;
      component.sessionId = '1';
      component.userId = '1';
    });

    it('should call participate method with correct parameters', () => {
      // ARRANGE
      const participateSpy = jest.spyOn(mockSessionApiService, 'participate');
      
      // ACT
      component.participate();

      // ASSERT
      expect(participateSpy).toHaveBeenCalledWith('1', '1');
    });

    it('should call unParticipate method with correct parameters', () => {
      // ARRANGE
      const unParticipateSpy = jest.spyOn(mockSessionApiService, 'unParticipate');
      
      // ACT
      component.unParticipate();

      // ASSERT
      expect(unParticipateSpy).toHaveBeenCalledWith('1', '1');
    });

    it('should fetch session after participating', () => {
      // ARRANGE
      const detailSpy = jest.spyOn(mockSessionApiService, 'detail');
      
      // ACT
      component.participate();

      // ASSERT
      expect(detailSpy).toHaveBeenCalledWith('1');
    });

    it('should fetch session after unparticipating', () => {
      // ARRANGE
      const detailSpy = jest.spyOn(mockSessionApiService, 'detail');
      
      // ACT
      component.unParticipate();

      // ASSERT
      expect(detailSpy).toHaveBeenCalledWith('1');
    });
  });

  describe('Navigation and Actions', () => {
    it('should call window.history.back when back is called', () => {
      // ARRANGE
      const locationSpy = jest.spyOn(window.history, 'back');
      
      // ACT
      component.back();
      
      // ASSERT
      expect(locationSpy).toHaveBeenCalled();
    });

    it('should delete session and show success message', () => {
      // ARRANGE
      const deleteSpy = jest.spyOn(mockSessionApiService, 'delete');
      component.sessionId = '1';
      
      // ACT
      component.delete();

      // ASSERT
      expect(deleteSpy).toHaveBeenCalledWith('1');
      expect(matSnackBar.open).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
      expect(router.navigate).toHaveBeenCalledWith(['sessions']);
    });
  });

});