import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PedidoRow } from './pedido-row';

describe('PedidoRow', () => {
  let component: PedidoRow;
  let fixture: ComponentFixture<PedidoRow>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PedidoRow],
    }).compileComponents();

    fixture = TestBed.createComponent(PedidoRow);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
