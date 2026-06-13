import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PedidoTimerComponent } from './pedido-timer.component';

describe('PedidoTimerComponent', () => {
  let component: PedidoTimerComponent;
  let fixture: ComponentFixture<PedidoTimerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PedidoTimerComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PedidoTimerComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('shows placeholder when there is no pedido in progress', () => {
    fixture.detectChanges();
    const el: HTMLElement = fixture.nativeElement;
    expect(el.textContent).toContain('--:--:--');
    expect(el.textContent).toContain('—');
  });

  it('shows the pedido code when provided', () => {
    fixture.componentRef.setInput('codPedido', 42);
    fixture.detectChanges();
    const el: HTMLElement = fixture.nativeElement;
    expect(el.textContent).toContain('#42');
  });

  it('formats elapsed time as HH:MM:SS', () => {
    const inicio = new Date(Date.now() - 65_000).toISOString();
    fixture.componentRef.setInput('inicioPedido', inicio);
    fixture.detectChanges();
    const el: HTMLElement = fixture.nativeElement;
    expect(el.textContent).toMatch(/00:0?1:0[0-9]/);
  });
});
