import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PedidoLaminas } from './pedido-laminas.component';

describe('PedidoLaminas', () => {
  let component: PedidoLaminas;
  let fixture: ComponentFixture<PedidoLaminas>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PedidoLaminas],
    }).compileComponents();

    fixture = TestBed.createComponent(PedidoLaminas);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
