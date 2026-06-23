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
  readonly codPedido = input<number | null>(null);
       readonly duracao   = input<string | null>(null);

       readonly codigoLabel  = computed(() => {
         const codigo = this.codPedido();
         return codigo !== null ? `#${codigo}` : '—';
       });

       readonly elapsedLabel = computed(() => {
         const duracao = this.duracao();
         if (!duracao) return '--:--:--';
         return this.parseDuracao(duracao);
       });

       private parseDuracao(d: string): string {
         const m = d.match(/PT(?:(\d+)H)?(?:(\d+)M)?(?:(\d+(?:\.\d+)?)S)?/);
         if (!m) return '--:--:--';
         const h   = parseInt(m[1] ?? '0', 10);
         const min = parseInt(m[2] ?? '0', 10);
         const sec = Math.floor(parseFloat(m[3] ?? '0'));
         const pad = (n: number) => n.toString().padStart(2, '0');
         return `${pad(h)}:${pad(min)}:${pad(sec)}`;
       }


  private readonly now = signal(Date.now());
  private intervalId?: ReturnType<typeof setInterval>;

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
  
}
