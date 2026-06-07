import { TestBed } from '@angular/core/testing';

import { Expedicao } from './expedicao';

describe('Expedicao', () => {
  let service: Expedicao;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Expedicao);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
