import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ConexaoService } from './conexao.service';
import { ConfigService } from './config.service';
import { BancadaConfig } from '../models/bancada-config.model';

const API = 'http://test-api';

const CONFIG: BancadaConfig = {
  estoqueIp: '192.168.0.1',
  processoIp: '192.168.0.2',
  montagemIp: '192.168.0.3',
  expedicaoIp: '192.168.0.4',
  seletorTampasIp: null,
};

describe('ConexaoService', () => {
  let service: ConexaoService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ConfigService, useValue: { apiUrl: API } },
      ],
    });
    service = TestBed.inject(ConexaoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created and start disconnected with no saved config', () => {
    expect(service).toBeTruthy();
    expect(service.isConnected()).toBe(false);
    expect(service.bancadaConfig()).toBeNull();
  });

  it('should mark connected and persist config on successful POST', () => {
    service.connect(CONFIG).subscribe();

    const req = httpMock.expectOne(`${API}/api/configuracao`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(CONFIG);
    req.flush({});

    expect(service.isConnected()).toBe(true);
    expect(service.bancadaConfig()).toEqual(CONFIG);
    expect(JSON.parse(localStorage.getItem('bancada.config')!)).toEqual(CONFIG);
  });

  it('should stay disconnected when POST fails', () => {
    service.connect(CONFIG).subscribe({ error: () => {} });

    httpMock
      .expectOne(`${API}/api/configuracao`)
      .flush('falha', { status: 500, statusText: 'Server Error' });

    expect(service.isConnected()).toBe(false);
  });

  it('ativarModoTeste() marks connected without any HTTP call', () => {
    service.ativarModoTeste();
    expect(service.isConnected()).toBe(true);
    // afterEach httpMock.verify() garante que nenhuma requisição foi feita
  });

  it('desconectar() should set isConnected to false', () => {
    service.connect(CONFIG).subscribe();
    httpMock.expectOne(`${API}/api/configuracao`).flush({});
    expect(service.isConnected()).toBe(true);

    service.desconectar();
    expect(service.isConnected()).toBe(false);
  });
});
