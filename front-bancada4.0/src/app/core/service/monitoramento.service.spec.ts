import { TestBed } from '@angular/core/testing';
import { MonitoramentoService, mapMonitoramentoSnapshot } from './monitoramento.service';
import { ConfigService } from './config.service';

const API = 'http://test-api';

class FakeEventSource {
  static instances: FakeEventSource[] = [];
  static readonly CONNECTING = 0;
  static readonly OPEN = 1;
  static readonly CLOSED = 2;

  readyState = FakeEventSource.CONNECTING;
  onopen: (() => void) | null = null;
  onerror: (() => void) | null = null;
  private readonly listeners = new Map<string, (ev: MessageEvent<string>) => void>();

  constructor(public readonly url: string) {
    FakeEventSource.instances.push(this);
  }

  addEventListener(nome: string, fn: (ev: MessageEvent<string>) => void): void {
    this.listeners.set(nome, fn);
  }

  emit(nome: string, data: string): void {
    this.listeners.get(nome)?.({ data } as MessageEvent<string>);
  }

  close(): void {
    this.readyState = FakeEventSource.CLOSED;
  }
}

const PAYLOAD_VALIDO = JSON.stringify({
  codPedidoAtual: 12,
  duracao: 5,
  estacaoStatus: [{ estacao: 'ESTOQUE', status: 'ATIVO', atualizadoEm: '2026-01-01T00:00:00' }],
  estoque: [{ posicao: 1, cor: 1 }],
  expedicao: [],
});

describe('MonitoramentoService', () => {
  let service: MonitoramentoService;

  beforeEach(() => {
    FakeEventSource.instances = [];
    vi.stubGlobal('EventSource', FakeEventSource);
    TestBed.configureTestingModule({
      providers: [{ provide: ConfigService, useValue: { apiUrl: API } }],
    });
    service = TestBed.inject(MonitoramentoService);
  });

  afterEach(() => {
    vi.useRealTimers();
    vi.unstubAllGlobals();
  });

  it('should start with no snapshot and connecting status', () => {
    expect(service.snapshot()).toBeNull();
    expect(service.connectionStatus()).toBe('connecting');
  });

  it('opens the SSE stream on the readAll endpoint', () => {
    service.connect();
    expect(FakeEventSource.instances).toHaveLength(1);
    expect(FakeEventSource.instances[0].url).toBe(`${API}/api/smart/readAll`);
  });

  it('parses a valid snapshot remapping estacaoStatus to estacoes (lowercase)', () => {
    service.connect();
    FakeEventSource.instances[0].emit('monitoramento', PAYLOAD_VALIDO);

    const snap = service.snapshot();
    expect(snap).not.toBeNull();
    expect(snap!.codPedidoAtual).toBe(12);
    expect(snap!.duracao).toBe(5);
    expect(snap!.estacoes).toHaveLength(1);
    expect(snap!.estacoes[0].estacao).toBe('estoque');
    expect(snap!.estacoes[0].status).toBe('ativo');
    expect(snap!.estoque).toEqual([{ posicao: 1, cor: 1 }]);
  });

  it('ignores malformed JSON keeping the previous snapshot', () => {
    service.connect();
    const es = FakeEventSource.instances[0];
    es.emit('monitoramento', PAYLOAD_VALIDO);
    const anterior = service.snapshot();

    expect(() => es.emit('monitoramento', '{invalid')).not.toThrow();
    expect(service.snapshot()).toBe(anterior);
  });

  it('reflects connection status transitions', () => {
    service.connect();
    const es = FakeEventSource.instances[0];

    es.onopen!();
    expect(service.connectionStatus()).toBe('connected');

    es.readyState = FakeEventSource.CONNECTING;
    es.onerror!();
    expect(service.connectionStatus()).toBe('reconnecting');
  });

  it('recreates the EventSource with backoff when the stream is closed', () => {
    vi.useFakeTimers();
    service.connect();
    const es = FakeEventSource.instances[0];

    es.readyState = FakeEventSource.CLOSED;
    es.onerror!();
    expect(service.connectionStatus()).toBe('error');
    expect(FakeEventSource.instances).toHaveLength(1);

    vi.advanceTimersByTime(1000);
    expect(FakeEventSource.instances).toHaveLength(2);
    expect(service.connectionStatus()).toBe('connecting');
  });

  it('disconnect closes the stream and cancels pending reconnections', () => {
    vi.useFakeTimers();
    service.connect();
    const es = FakeEventSource.instances[0];

    es.readyState = FakeEventSource.CLOSED;
    es.onerror!();
    service.disconnect();

    vi.advanceTimersByTime(60_000);
    expect(FakeEventSource.instances).toHaveLength(1);
    expect(es.readyState).toBe(FakeEventSource.CLOSED);
  });
});

describe('mapMonitoramentoSnapshot', () => {
  it('applies defaults for missing fields', () => {
    const snap = mapMonitoramentoSnapshot({});
    expect(snap.codPedidoAtual).toBeNull();
    expect(snap.duracao).toBeNull();
    expect(snap.estacoes).toEqual([]);
    expect(snap.estoque).toEqual([]);
    expect(snap.expedicao).toEqual([]);
  });
});
