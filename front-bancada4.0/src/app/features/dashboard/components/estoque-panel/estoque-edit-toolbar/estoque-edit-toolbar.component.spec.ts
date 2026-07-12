import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EstoqueEditToolbarComponent } from './estoque-edit-toolbar.component';
import { CorBloco } from '../../../../../core/models/enums/corbloco.enum';

describe('EstoqueEditToolbarComponent', () => {
  let component: EstoqueEditToolbarComponent;
  let fixture: ComponentFixture<EstoqueEditToolbarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EstoqueEditToolbarComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(EstoqueEditToolbarComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('selectedColor', CorBloco.AZUL);
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
