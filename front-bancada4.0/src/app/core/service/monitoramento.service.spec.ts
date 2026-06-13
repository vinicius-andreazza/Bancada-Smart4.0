import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { MonitoramentoService } from './monitoramento.service';

describe('MonitoramentoService', () => {
  let service: MonitoramentoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(MonitoramentoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should start with no snapshot and connecting status', () => {
    expect(service.snapshot()).toBeNull();
    expect(service.connectionStatus()).toBe('connecting');
  });
});
