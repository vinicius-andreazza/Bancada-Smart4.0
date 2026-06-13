import { Component, computed, input, OnDestroy, OnInit, signal } from '@angular/core';

@Component({
  selector: 'app-pedido-timer',
  template: `
    <div class="flex items-center gap-4 bg-bg-card border border-border-subtle rounded-smart px-4 py-2">
      <div>
        <div class="text-[0.7rem] text-text-secondary font-mono tracking-[0.06em] uppercase">
          Pedido atual
        </div>
        <div class="font-heading text-base font-bold leading-none text-text-primary">
          {{ codigoLabel() }}
        </div>
      </div>

      <div class="w-px h-8 bg-border-subtle"></div>

      <div>
        <div class="text-[0.7rem] text-text-secondary font-mono tracking-[0.06em] uppercase">
          Tempo total
        </div>
        <div class="font-mono-tech text-base font-bold leading-none text-text-accent">
          {{ elapsedLabel() }}
        </div>
      </div>
    </div>
  `,
})
export class PedidoTimerComponent implements OnInit, OnDestroy {
  readonly codPedido    = input<number | null>(null);
  readonly inicioPedido = input<string | null>(null);

  private readonly now = signal(Date.now());
  private intervalId?: ReturnType<typeof setInterval>;

  readonly codigoLabel = computed(() => {
    const codigo = this.codPedido();
    return codigo !== null ? `#${codigo}` : '—';
  });

  readonly elapsedLabel = computed(() => {
    const inicio = this.inicioPedido();
    if (!inicio) return '--:--:--';

    const elapsedMs = Math.max(0, this.now() - new Date(inicio).getTime());
    return this.formatDuration(elapsedMs);
  });

  ngOnInit(): void {
    this.intervalId = setInterval(() => {
      this.now.set(Date.now());
    }, 1000);
  }

  ngOnDestroy(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }

  private formatDuration(elapsedMs: number): string {
    const totalSeconds = Math.floor(elapsedMs / 1000);
    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = totalSeconds % 60;

    const pad = (value: number) => value.toString().padStart(2, '0');
    return `${pad(hours)}:${pad(minutes)}:${pad(seconds)}`;
  }
}
