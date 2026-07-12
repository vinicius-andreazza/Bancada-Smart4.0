import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';

import { PedidoLaminas } from './pedido-laminas.component';

describe('PedidoLaminas', () => {
  let component: PedidoLaminas;
  let fixture: ComponentFixture<PedidoLaminas>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PedidoLaminas],
    }).compileComponents();

    const fb = TestBed.inject(FormBuilder);
    fixture = TestBed.createComponent(PedidoLaminas);
    component = fixture.componentInstance;
    fixture.componentRef.setInput(
      'form',
      fb.group({ corLamina: '1', padraoLamina: '0', posicaoLamina: '2' }),
    );
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
