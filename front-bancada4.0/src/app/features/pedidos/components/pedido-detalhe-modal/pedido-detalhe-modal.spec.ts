import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PedidoDetalheModal } from './pedido-detalhe-modal';

describe('PedidoDetalheModal', () => {
  let component: PedidoDetalheModal;
  let fixture: ComponentFixture<PedidoDetalheModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PedidoDetalheModal],
    }).compileComponents();

    fixture = TestBed.createComponent(PedidoDetalheModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
