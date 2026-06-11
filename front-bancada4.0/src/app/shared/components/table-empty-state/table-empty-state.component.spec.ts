import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TableEmptyState } from './table-empty-state.component';

describe('TableEmptyState', () => {
  let component: TableEmptyState;
  let fixture: ComponentFixture<TableEmptyState>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TableEmptyState],
    }).compileComponents();

    fixture = TestBed.createComponent(TableEmptyState);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
