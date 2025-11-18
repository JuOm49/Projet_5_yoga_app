import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController, TestRequest } from '@angular/common/http/testing';

import { expect } from '@jest/globals';

import { Teacher } from '../interfaces/teacher.interface';

import { TeacherService } from './teacher.service';


describe('TeacherService', () => {
  let service: TeacherService;
  let httpMock: HttpTestingController;

  const mockTeachers: Teacher[] = [
    {
      id: 1,
      firstName: 'Hervé',
      lastName: 'Dupont',
      createdAt: new Date(),
      updatedAt: new Date()
    },
    {
      id: 2,
      firstName: 'Sergio',
      lastName: 'Ombassa',
      createdAt: new Date(),
      updatedAt: new Date()
    }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[
        HttpClientModule,
        HttpClientTestingModule
      ]
    });

    service = TestBed.inject(TeacherService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Verify that no unmatched requests are outstanding
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('all() method', () => {
      it('should return list of teachers', () => {
        // ARRANGE
        const mockAllTeachers: Teacher[] = mockTeachers;
        
        // ACT
        service.all().subscribe(teachers => {
          // ASSERT with mock teachers
          expect(teachers).toEqual(mockAllTeachers);
          expect(teachers.length).toBe(2);
        });
        
        // ASSERT
        const testRequest: TestRequest = httpMock.expectOne('api/teacher');
        expect(testRequest.request.method).toBe('GET');
        testRequest.flush(mockAllTeachers); // simulate response
      });
  
      it('should handle empty response', () => {
        // ACT
        service.all().subscribe(teachers => {
        // ASSERT with empty array
          expect(teachers).toEqual([]);
        });
        
        // ASSERT
        const testRequest: TestRequest = httpMock.expectOne('api/teacher');
        expect(testRequest.request.method).toBe('GET');
        testRequest.flush([]);
      });
    });

    describe('detail() method', () => {
      it('should return teacher details', () => {
        // ARRANGE
        const teacherId = '1';
        // ACT
        service.detail(teacherId).subscribe(teacher => {
          // ASSERT with teacher details
          expect(teacher).toEqual(mockTeachers[0]);
          expect(teacher.id).toBe(1);
        });
        // ASSERT
        const testRequest: TestRequest = httpMock.expectOne(`api/teacher/${teacherId}`);
        expect(testRequest.request.method).toBe('GET');
        testRequest.flush(mockTeachers[0]);
      });

      it('should handle 404 error', () => {
        // ARRANGE
        const teacherId = '50';
        // ACT
        service.detail(teacherId).subscribe({
          next: () => {
            // This block should not be executed for 404 error
            fail('Expected 404 error, but got a teacher');
          },
          error: (error) => {
            // ASSERT
            expect(error.status).toBe(404);
          }
        });
        // ASSERT
        const testRequest: TestRequest = httpMock.expectOne(`api/teacher/${teacherId}`);
        expect(testRequest.request.method).toBe('GET');
        testRequest.flush('Teacher not found', { status: 404, statusText: 'Not Found' });
      });
    });
});
