import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';

import { PedidoBlocos } from './pedido-blocos.component';
import { criarBlocoVazio } from '../../shared/pedido-form.factory';

describe('PedidoBlocos', () => {
  let component: PedidoBlocos;
  let fixture: ComponentFixture<PedidoBlocos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PedidoBlocos],
    }).compileComponents();

    const fb = TestBed.inject(FormBuilder);
    fixture = TestBed.createComponent(PedidoBlocos);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('form', criarBlocoVazio(fb, 1));
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
