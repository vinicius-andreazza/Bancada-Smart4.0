import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EstacaoCardComponent } from './estacao-card.component';
import { Estacao } from '../../../../core/models/enums/estacao.enum';
import { StatusEstacao } from '../../../../core/models/enums/statusestacao.enum';

describe('EstacaoCardComponent', () => {
  let component: EstacaoCardComponent;
  let fixture: ComponentFixture<EstacaoCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EstacaoCardComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(EstacaoCardComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('estacao', Estacao.MONTAGEM);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('shows the estacao label', () => {
    fixture.detectChanges();
    const el: HTMLElement = fixture.nativeElement;
    expect(el.textContent).toContain('Montagem');
  });

  it('shows "SEM DADOS" when status is null', () => {
    fixture.detectChanges();
    const el: HTMLElement = fixture.nativeElement;
    expect(el.textContent).toContain('SEM DADOS');
  });

  it('shows the status badge when status is provided', () => {
    fixture.componentRef.setInput('status', StatusEstacao.OCUPADO);
    fixture.detectChanges();
    const el: HTMLElement = fixture.nativeElement;
    expect(el.querySelector('app-estacao-status-badge')).toBeTruthy();
  });
});
