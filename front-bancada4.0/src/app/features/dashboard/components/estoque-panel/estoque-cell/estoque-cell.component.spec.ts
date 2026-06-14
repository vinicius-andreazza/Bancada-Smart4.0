import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EstoqueCellComponent } from './estoque-cell.component';
import { CorBloco } from '../../../../../core/models/enums/corbloco.enum';
import { Estoque } from '../../../../../core/models/estoque.model';

describe('EstoqueCellComponent', () => {
  let component: EstoqueCellComponent;
  let fixture: ComponentFixture<EstoqueCellComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EstoqueCellComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(EstoqueCellComponent);
    component = fixture.componentInstance;
    const pos: Estoque = { posicao: 1, cor: CorBloco.VAZIO };
    fixture.componentRef.setInput('pos', pos);
    fixture.componentRef.setInput('editMode', false);
    fixture.componentRef.setInput('cor', CorBloco.VAZIO);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
