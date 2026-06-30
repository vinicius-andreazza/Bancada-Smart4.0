import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ColorPickerComponent } from './color-pick.component';
import { CorBloco } from '../../../../core/models/enums/corbloco.enum';

describe('ColorPickerComponent', () => {
  let component: ColorPickerComponent;
  let fixture: ComponentFixture<ColorPickerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ColorPickerComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ColorPickerComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('selectedCor', CorBloco.AZUL);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
