import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { expect } from '@jest/globals';

import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';

import { ListComponent } from './list.component';
import { Observable, of } from 'rxjs';

// types mocks
type SessionApiServiceMock = {
  all: ReturnType<typeof jest.fn>;
};

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;
  let mockSessionService: any; // Utilisation d'any pour la flexibilité des tests
  let mockSessionApiService: SessionApiServiceMock;

  beforeEach(async () => {
    // Init mocks
    jest.clearAllMocks();

    mockSessionService = {
      sessionInformation: {
        admin: true
      }
    };

    mockSessionApiService = {
      all: jest.fn().mockReturnValue(of([]))
    };

    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      imports: [HttpClientModule, MatCardModule, MatIconModule],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Component Initialization', () => {
    it('should have sessions$ observable from session service', () => {
      //ASSERT
      expect(component.sessions$).toBeTruthy();
      expect(component.sessions$).toBeInstanceOf(Observable);
    });

    it('should load the list of sessions on component initialization', () => {
      //ASSERT
      expect(component.sessions$).toBeDefined();
      expect(mockSessionApiService.all).toHaveBeenCalled();
    });
  });

  describe('Admin Access Control', () => {
    it('should return admin status correctly for admin user', () => {
      // ARRANGE
      mockSessionService.sessionInformation = {
        admin: true
      };
      
      // ACT
      const user = component.user;
      
      // ASSERT
      expect(user).toBeDefined();
      expect(user?.admin).toBe(true);
    });

    it('should return status correctly for non-admin user', () => {
      // ARRANGE
      mockSessionService.sessionInformation = {
        admin: false
      };
      
      // ACT
      const user = component.user;
      
      // ASSERT
      expect(user?.admin).toBe(false);
    });
  });
});