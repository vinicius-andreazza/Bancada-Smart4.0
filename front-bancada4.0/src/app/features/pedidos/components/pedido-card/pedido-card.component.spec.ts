import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';

import { PedidoCard } from './pedido-card.component';
import { criarBlocoVazio } from '../../shared/pedido-form.factory';

describe('PedidoCard', () => {
  let component: PedidoCard;
  let fixture: ComponentFixture<PedidoCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PedidoCard],
    }).compileComponents();

    const fb = TestBed.inject(FormBuilder);
    fixture = TestBed.createComponent(PedidoCard);
    component = fixture.componentInstance;
    fixture.componentRef.setInput(
      'form',
      fb.group({
        codPedido: '000',
        status: '1',
        tipoPedido: '1',
        corTampa: '1',
        blocos: fb.array([criarBlocoVazio(fb, 1)]),
      }),
    );
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
