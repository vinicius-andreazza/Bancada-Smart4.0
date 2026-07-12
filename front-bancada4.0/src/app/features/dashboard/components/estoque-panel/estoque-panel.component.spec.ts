import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EstoquePanel } from './estoque-panel.component';
import { CorBloco } from '../../../../core/models/enums/corbloco.enum';

describe('EstoquePanel', () => {
  let component: EstoquePanel;
  let fixture: ComponentFixture<EstoquePanel>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EstoquePanel],
    }).compileComponents();

    fixture = TestBed.createComponent(EstoquePanel);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('positions', []);
    fixture.componentRef.setInput('editMode', false);
    fixture.componentRef.setInput('selectedColor', CorBloco.AZUL);
    fixture.componentRef.setInput('pendingChanges', new Map());
    fixture.componentRef.setInput('cellColorMap', new Map());
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
