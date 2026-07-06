import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StatusCardPedido } from './status-card-pedido.component';

describe('StatusCardPedido', () => {
  let component: StatusCardPedido;
  let fixture: ComponentFixture<StatusCardPedido>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StatusCardPedido],
    }).compileComponents();

    fixture = TestBed.createComponent(StatusCardPedido);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('type', 'TOTAL');
    fixture.componentRef.setInput('total', 0);
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
