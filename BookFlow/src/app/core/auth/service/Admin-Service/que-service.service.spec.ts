import { TestBed } from '@angular/core/testing';

import { QueServiceService } from './que-service.service';

describe('QueServiceService', () => {
  let service: QueServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(QueServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
