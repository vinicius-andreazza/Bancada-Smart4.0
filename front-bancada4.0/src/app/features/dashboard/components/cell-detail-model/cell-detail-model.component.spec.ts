import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CellDetailModel } from './cell-detail-model.component';

describe('CellDetailModel', () => {
  let component: CellDetailModel;
  let fixture: ComponentFixture<CellDetailModel>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CellDetailModel],
    }).compileComponents();

    fixture = TestBed.createComponent(CellDetailModel);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('opened', false);
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
