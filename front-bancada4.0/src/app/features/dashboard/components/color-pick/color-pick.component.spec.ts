import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ColorPick } from './color-pick.component';

describe('ColorPick', () => {
  let component: ColorPick;
  let fixture: ComponentFixture<ColorPick>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ColorPick],
    }).compileComponents();

    fixture = TestBed.createComponent(ColorPick);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
