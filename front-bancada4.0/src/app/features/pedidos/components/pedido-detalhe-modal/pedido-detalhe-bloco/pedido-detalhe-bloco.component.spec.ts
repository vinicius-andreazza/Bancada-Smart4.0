import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PedidoDetalheBloco } from './pedido-detalhe-bloco.component';

describe('PedidoDetalheBloco', () => {
  let component: PedidoDetalheBloco;
  let fixture: ComponentFixture<PedidoDetalheBloco>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PedidoDetalheBloco],
    }).compileComponents();

    fixture = TestBed.createComponent(PedidoDetalheBloco);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
