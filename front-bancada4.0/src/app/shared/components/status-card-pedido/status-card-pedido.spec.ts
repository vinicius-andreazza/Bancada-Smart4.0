import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StatusCardPedido } from './status-card-pedido';

describe('StatusCardPedido', () => {
  let component: StatusCardPedido;
  let fixture: ComponentFixture<StatusCardPedido>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StatusCardPedido],
    }).compileComponents();

    fixture = TestBed.createComponent(StatusCardPedido);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
