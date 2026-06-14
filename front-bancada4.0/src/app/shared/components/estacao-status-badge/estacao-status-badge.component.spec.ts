import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EstacaoStatusBadgeComponent } from './estacao-status-badge.component';
import { StatusEstacao } from '../../../core/models/enums/statusestacao.enum';

describe('EstacaoStatusBadgeComponent', () => {
  let component: EstacaoStatusBadgeComponent;
  let fixture: ComponentFixture<EstacaoStatusBadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EstacaoStatusBadgeComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(EstacaoStatusBadgeComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it.each([
    [StatusEstacao.START, 'AGUARDANDO INÍCIO'],
    [StatusEstacao.OCUPADO, 'EM OPERAÇÃO'],
    [StatusEstacao.CANCELADO, 'CANCELADO'],
    [StatusEstacao.FINALIZADO, 'FINALIZADO'],
  ])('renders default label for status %s', (status, expectedLabel) => {
    fixture.componentRef.setInput('status', status);
    fixture.detectChanges();

    const el: HTMLElement = fixture.nativeElement;
    expect(el.querySelector('[role="status"]')?.textContent?.trim()).toBe(expectedLabel);
  });
});
