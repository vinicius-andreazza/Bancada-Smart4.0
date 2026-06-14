import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PedidoDetalheBlocoComponent } from './pedido-detalhe-bloco.component';
import { Bloco } from '../../../../../core/models/bloco.model';
import { CorBloco } from '../../../../../core/models/enums/corbloco.enum';

describe('PedidoDetalheBlocoComponent', () => {
  let component: PedidoDetalheBlocoComponent;
  let fixture: ComponentFixture<PedidoDetalheBlocoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PedidoDetalheBlocoComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PedidoDetalheBlocoComponent);
    component = fixture.componentInstance;
    const bloco = {
      id: 1,
      corBloco: CorBloco.AZUL,
      posEstoque: 1,
      andar: 0,
      laminas: [],
    } as unknown as Bloco;
    fixture.componentRef.setInput('bloco', bloco);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
