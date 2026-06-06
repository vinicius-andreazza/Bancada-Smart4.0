import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PedidoBlocos } from './pedido-blocos';

describe('PedidoBlocos', () => {
  let component: PedidoBlocos;
  let fixture: ComponentFixture<PedidoBlocos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PedidoBlocos],
    }).compileComponents();

    fixture = TestBed.createComponent(PedidoBlocos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
