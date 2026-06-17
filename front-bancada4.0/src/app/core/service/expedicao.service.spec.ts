import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ExpedicaoService } from './expedicao.service';

describe('ExpedicaoService', () => {
  let service: ExpedicaoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(ExpedicaoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
