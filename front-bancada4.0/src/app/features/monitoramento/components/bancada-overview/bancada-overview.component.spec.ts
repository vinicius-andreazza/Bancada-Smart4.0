import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BancadaOverviewComponent } from './bancada-overview.component';
import { Estacao } from '../../../../core/models/enums/estacao.enum';
import { StatusEstacao } from '../../../../core/models/enums/statusestacao.enum';

describe('BancadaOverviewComponent', () => {
  let component: BancadaOverviewComponent;
  let fixture: ComponentFixture<BancadaOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BancadaOverviewComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(BancadaOverviewComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('renders only the base image when there is no status', () => {
    fixture.componentRef.setInput('estacoes', [{ estacao: Estacao.ESTOQUE, status: null }]);
    fixture.detectChanges();

    const el: HTMLElement = fixture.nativeElement;
    expect(el.querySelectorAll('img').length).toBe(1);
  });

  it('renders the bar and icon overlays for an active station', () => {
    fixture.componentRef.setInput('estacoes', [
      { estacao: Estacao.PROCESSO, status: StatusEstacao.OCUPADO },
    ]);
    fixture.detectChanges();

    const el: HTMLElement = fixture.nativeElement;
    const sources = Array.from(el.querySelectorAll('img')).map((img) => img.getAttribute('ng-src') ?? img.src);
    expect(sources.some((s) => s.includes('Smart40-Pro_pause.png'))).toBe(true);
    expect(sources.some((s) => s.includes('Smart40_Processo_1.png'))).toBe(true);
  });

  it('renders only the bar overlay for cancelado (no icon)', () => {
    fixture.componentRef.setInput('estacoes', [
      { estacao: Estacao.EXPEDICAO, status: StatusEstacao.CANCELADO },
    ]);
    fixture.detectChanges();

    const el: HTMLElement = fixture.nativeElement;
    expect(el.querySelectorAll('img').length).toBe(2);
  });
});
