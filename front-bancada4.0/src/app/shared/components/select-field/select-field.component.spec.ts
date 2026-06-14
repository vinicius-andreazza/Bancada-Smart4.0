import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl } from '@angular/forms';

import { SelectFieldComponent } from './select-field.component';

describe('SelectFieldComponent', () => {
  let component: SelectFieldComponent;
  let fixture: ComponentFixture<SelectFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectFieldComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SelectFieldComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('id', 'campo');
    fixture.componentRef.setInput('label', 'Campo');
    fixture.componentRef.setInput('control', new FormControl(''));
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
