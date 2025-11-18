import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController, TestRequest } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { Session } from '../interfaces/session.interface';

import { SessionApiService } from './session-api.service';

describe('SessionsApiService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  const mockSession: Session = {
    id: 1,
    name: 'Test Session',
    description: 'Test Description',
    date: new Date('2025-12-01'),
    teacher_id: 1,
    users: [1, 2],
    createdAt: new Date(),
    updatedAt: new Date()
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[
        HttpClientTestingModule
      ]
    });

    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Verify that no unmatched requests are outstanding
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('all() method', () => {
    it('should return list of sessions', () => {
      // ARRANGE
      const mockSessions: Session[] = [mockSession];
      
      // ACT
      service.all().subscribe(sessions => {
        // ASSERT with mock sessions
        expect(sessions).toEqual(mockSessions);
        expect(sessions.length).toBe(1);
      });
      
      // ASSERT
      const testRequest: TestRequest = httpMock.expectOne('api/session');
      expect(testRequest.request.method).toBe('GET');
      testRequest.flush(mockSessions); // simulate response
    });

    it('should handle empty response', () => {
      // ACT
      service.all().subscribe(sessions => {
      // ASSERT with empty array
        expect(sessions).toEqual([]);
      });
      
      // ASSERT
      const testRequest: TestRequest = httpMock.expectOne('api/session');
      expect(testRequest.request.method).toBe('GET');
      testRequest.flush([]);
    });
  });

  describe('detail() method', () => {
    it('should return session details', () => {
      // ARRANGE
      const sessionId = '1';   
      // ACT
      service.detail(sessionId).subscribe(session => {
        // ASSERT with session details
        expect(session).toEqual(mockSession);
        expect(session.id).toBe(1);
      });    
      // ASSERT
      const testRequest: TestRequest = httpMock.expectOne(`api/session/${sessionId}`);
      expect(testRequest.request.method).toBe('GET');
      testRequest.flush(mockSession);
    });

    it('should handle 404 error', () => {
      // ARRANGE
      const sessionId = '50';     
      // ACT
      service.detail(sessionId).subscribe({
        next: () => fail('Should have failed'),
        error: (error) => {
          // ASSERT with 404 error
          expect(error.status).toBe(404);
        }
      });      
      // ASSERT
      const testRequest: TestRequest = httpMock.expectOne(`api/session/${sessionId}`);
      testRequest.flush('Session not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('delete() method', () => {
    it('should delete session successfully', () => {
      // ARRANGE
      const sessionId = '1';     
      // ACT
      service.delete(sessionId).subscribe(response => {
        // ASSERT with no content
        expect(response).toBeTruthy();
      });      
      // ASSERT
      const testRequest: TestRequest = httpMock.expectOne(`api/session/${sessionId}`);
      expect(testRequest.request.method).toBe('DELETE');
      testRequest.flush({}); // response with no content
    });
  });

  describe('create() method', () => {
    it('should create new session', () => {
      // ARRANGE
      const newSession: Session = {
        id: 3,
        name: 'New Session of yoga',
        description: 'New Description for this session of yoga',
        date: new Date('2025-12-01'),
        teacher_id: 2,
        users: [],
        createdAt: new Date(),
        updatedAt: new Date()
      };
      
      // ACT
      service.create(newSession).subscribe(session => {
        // ASSERT with created session
        expect(session).toEqual(mockSession);
      });
      
      // ASSERT
      const testRequest: TestRequest = httpMock.expectOne('api/session');
      expect(testRequest.request.method).toBe('POST');
      expect(testRequest.request.body).toEqual(newSession);
      testRequest.flush(mockSession);
    });

    it('should handle validation errors', () => {
      // ARRANGE
      const invalidSession: Session = { 
        name: '',
        description: '',
        date: new Date('2025-12-01'),
        teacher_id: 50,
        users: [],
        createdAt: new Date(),
        updatedAt: new Date()
      };
      
      // ACT
      service.create(invalidSession).subscribe({
        next: () => fail('Should have failed'),
        error: (error) => {
          // ASSERT with validation error
          expect(error.status).toBe(400);
        }
      });
      
      // ASSERT
      const testRequest: TestRequest = httpMock.expectOne('api/session');
      testRequest.flush('Validation failed', { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('update() method', () => {
    it('should update existing session', () => {
      // ARRANGE
      const sessionId = '1';
      const updatedData: Session = {
        id: 1,
        name: 'Test Session updated',
        description: 'Test Description updated',
        date: new Date('2025-12-01'),
        teacher_id: 1,
        users: [1, 2],
        createdAt: new Date(),
        updatedAt: new Date()
      };    
      // ACT
      service.update(sessionId, updatedData).subscribe(session => {
        // ASSERT with updated session
        expect(session).toEqual(mockSession);
      });   
      // ASSERT
      const testRequest: TestRequest = httpMock.expectOne(`api/session/${sessionId}`);
      expect(testRequest.request.method).toBe('PUT');
      expect(testRequest.request.body).toEqual(updatedData);
      testRequest.flush(mockSession);
    });
  });

  describe('participate() method', () => {
    it('should add user to session', () => {
      // ARRANGE
      const sessionId = '1';
      const userId = '2';      
      // ACT
      service.participate(sessionId, userId).subscribe();     
      // ASSERT
      const testRequest: TestRequest = httpMock.expectOne(`api/session/${sessionId}/participate/${userId}`);
      expect(testRequest.request.method).toBe('POST');
      expect(testRequest.request.body).toBeNull();
      testRequest.flush(null);
    });
  });

  describe('unParticipate() method', () => {
    it('should remove user from session', () => {
      // ARRANGE
      const sessionId = '1';
      const userId = '2';     
      // ACT
      service.unParticipate(sessionId, userId).subscribe();     
      // ASSERT
      const testRequest: TestRequest = httpMock.expectOne(`api/session/${sessionId}/participate/${userId}`);
      expect(testRequest.request.method).toBe('DELETE');
      testRequest.flush(null);
    });
  });
});
