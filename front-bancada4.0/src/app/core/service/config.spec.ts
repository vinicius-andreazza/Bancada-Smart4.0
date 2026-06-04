import { TestBed } from '@angular/core/testing';

import { Config } from '../services/config';

describe('Config', () => {
  let service: Config;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Config);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
