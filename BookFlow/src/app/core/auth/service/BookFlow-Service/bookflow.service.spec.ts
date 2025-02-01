import { TestBed } from '@angular/core/testing';

import { BookflowService } from './bookflow.service';

describe('BookflowService', () => {
  let service: BookflowService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BookflowService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
