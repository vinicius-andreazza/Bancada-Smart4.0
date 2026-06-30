import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaginationComponent } from './pagination.component';

describe('PaginationComponent', () => {
  let component: PaginationComponent;
  let fixture: ComponentFixture<PaginationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaginationComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PaginationComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('emits previous index when not on first page', () => {
    fixture.componentRef.setInput('pageIndex', 2);
    fixture.componentRef.setInput('totalPages', 5);
    const spy = vi.fn();
    component.pageChange.subscribe(spy);

    component.anterior();

    expect(spy).toHaveBeenCalledWith(1);
  });

  it('does not emit past the last page', () => {
    fixture.componentRef.setInput('pageIndex', 4);
    fixture.componentRef.setInput('totalPages', 5);
    const spy = vi.fn();
    component.pageChange.subscribe(spy);

    component.proxima();

    expect(spy).not.toHaveBeenCalled();
  });
});
