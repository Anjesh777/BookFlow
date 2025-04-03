import { TestBed } from '@angular/core/testing';

import { UserService2Service } from './user-service2.service';

describe('UserService2Service', () => {
  let service: UserService2Service;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserService2Service);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
