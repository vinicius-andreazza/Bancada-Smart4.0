
import { Component, computed, input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { switchMap, startWith, of } from 'rxjs';
import {
  Bloco3dViewerComponent,
  BlocoConfig,
  CorBloco,
  CorLamina,
  CorTampa,
  LaminaConfig,
  PadraoLamina,
  PedidoPreviewConfig,
  PosicaoLamina,
} from './bloco-3d-viewer.component';

const COR_BLOCO_LABEL: Record<number, string> = {
  0: 'Vazio', 1: 'Preto', 2: 'Vermelho', 3: 'Azul',
};
const COR_BLOCO_HEX: Record<number, string> = {
  0: '#8b97a8', 1: '#1c1c1c', 2: '#c41e1e', 3: '#1a4dcc',
};
const TAMPA_LABEL: Record<number, string> = {
  1: 'Preto', 2: 'Vermelho', 3: 'Azul',
};
const TAMPA_HEX: Record<number, string> = {
  1: '#1c1c1c', 2: '#c41e1e', 3: '#1a4dcc',
};
const COR_LAMINA_LABEL: Record<number, string> = {
  1: 'Vermelho', 2: 'Azul', 3: 'Amarelo', 4: 'Verde', 5: 'Preto', 6: 'Branco',
};
const PADRAO_LABEL: Record<number, string> = {
  0: 'Nenhum', 1: 'Casa', 2: 'Navio', 3: 'Estrela',
};
const POSICAO_LABEL: Record<number, string> = {
  1: 'Esquerda', 2: 'Frente', 3: 'Direita',
};

interface BlocoSummary {
  andar: number;
  corLabel: string;
  corHex: string;
  laminas: { posLabel: string; corLabel: string; padraoLabel: string }[];
}

@Component({
  selector: 'app-pedido-3d-preview',
  imports: [ReactiveFormsModule, Bloco3dViewerComponent],
  template: `
    <div
      class="sticky top-4 flex flex-col gap-3 rounded-xl border border-gray-800 bg-gray-900 p-4 shadow-lg"
      role="complementary"
      aria-label="Visualização 3D do pedido"
    >
      <div class="flex items-center gap-2">
        <i class="fa-solid fa-cubes text-sky-400 text-sm" aria-hidden="true"></i>
        <span class="font-mono text-xs font-semibold tracking-[0.12em] uppercase text-gray-300">
          Visualização do pedido
        </span>
        <span class="ml-auto font-mono text-[0.6rem] text-gray-600">
          {{ blocosSummary().length }} {{ blocosSummary().length === 1 ? 'bloco' : 'blocos' }}
        </span>
      </div>

      <app-bloco-3d-viewer [pedido]="pedidoConfig()" />

      <div class="flex flex-col gap-2 pt-1 border-t border-gray-800">
        @for (bloco of blocosSummary(); track bloco.andar) {
          <div class="flex flex-col gap-1 rounded-md bg-gray-950/60 px-3 py-2">
            <div class="flex items-center gap-2">
              <span class="w-2.5 h-2.5 rounded-sm border border-gray-700 shrink-0" [style.background]="bloco.corHex"></span>
              <span class="font-mono text-[0.62rem] font-semibold tracking-[0.08em] uppercase text-gray-400">
                Andar {{ bloco.andar }} · {{ bloco.corLabel }}
              </span>
              @if (bloco.andar === topAndar() && corTampaLabel()) {
                <span class="ml-auto inline-flex items-center gap-1.5 font-mono text-[0.58rem] text-gray-500">
                  <span class="w-2 h-2 rounded-sm border border-gray-700 shrink-0" [style.background]="corTampaHex()"></span>
                  Tampa {{ corTampaLabel() }}
                </span>
              }
            </div>

            @if (bloco.laminas.length > 0) {
              <div class="flex flex-col gap-0.5 pl-4">
                @for (lam of bloco.laminas; track lam.posLabel) {
                  <span class="font-mono text-[0.58rem] text-gray-500">
                    {{ lam.posLabel }} — {{ lam.corLabel }}
                    @if (lam.padraoLabel !== 'Nenhum') { · {{ lam.padraoLabel }} }
                  </span>
                }
              </div>
            } @else {
              <span class="pl-4 font-mono text-[0.58rem] text-gray-600 italic">Sem lâminas</span>
            }
          </div>
        } @empty {
          <p class="font-mono text-[0.6rem] text-gray-600 italic px-1">
            Adicione um bloco para visualizar o pedido.
          </p>
        }
      </div>
    </div>
  `,
})
export class Pedido3dPreviewComponent {
  readonly form         = input<FormGroup | null>(null);
  readonly staticConfig = input<PedidoPreviewConfig | null>(null);

  private readonly formValue = toSignal(
    toObservable(this.form).pipe(
      switchMap(f => f ? f.valueChanges.pipe(startWith(f.value)) : of(null)),
    ),
    { initialValue: null },
  );

  // ── Derived pedido config consumed by the 3D viewer ─────────────────────
  readonly pedidoConfig = computed<PedidoPreviewConfig>(() => {
    const staticCfg = this.staticConfig();
    if (staticCfg) return staticCfg;

    const value = this.formValue();
    const blocosRaw = (value?.blocos ?? []) as Array<{
      andar?: number | string;
      corBloco?: number | string;
      laminas?: Array<{
        corLamina?: number | string;
        padraoLamina?: number | string;
        posicaoLamina?: number | string;
      }>;
    }>;

    const blocos: BlocoConfig[] = [...blocosRaw]
      .map((b, i) => ({
        andar:    Number(b.andar ?? i + 1),
        corBloco: Number(b.corBloco ?? CorBloco.PRETO) as CorBloco,
        laminas:  (b.laminas ?? []).map((l): LaminaConfig => ({
          corLamina:     Number(l.corLamina     ?? CorLamina.VERMELHO) as CorLamina,
          padraoLamina:  Number(l.padraoLamina  ?? PadraoLamina.NENHUM) as PadraoLamina,
          posicaoLamina: Number(l.posicaoLamina ?? PosicaoLamina.FRENTE) as PosicaoLamina,
        })),
      }))
      .sort((a, b) => a.andar - b.andar);

    const corTampaRaw = value?.corTampa;
    const corTampa = corTampaRaw !== null && corTampaRaw !== undefined && corTampaRaw !== ''
      ? Number(corTampaRaw) as CorTampa
      : null;

    return { blocos, corTampa };
  });

  // ── Legend helpers ──────────────────────────────────────────────────────
  readonly topAndar = computed(() => {
    const blocos = this.pedidoConfig().blocos;
    return blocos.length > 0 ? blocos[blocos.length - 1].andar : 0;
  });

  readonly corTampaLabel = computed(() => {
    const t = this.pedidoConfig().corTampa;
    return t !== null ? TAMPA_LABEL[t] ?? '' : '';
  });

  readonly corTampaHex = computed(() => {
    const t = this.pedidoConfig().corTampa;
    return t !== null ? TAMPA_HEX[t] ?? '#555' : '#555';
  });

  readonly blocosSummary = computed<BlocoSummary[]>(() =>
    this.pedidoConfig().blocos.map(b => ({
      andar:    b.andar,
      corLabel: COR_BLOCO_LABEL[b.corBloco] ?? '—',
      corHex:   COR_BLOCO_HEX[b.corBloco] ?? '#555',
      laminas:  b.laminas.map(l => ({
        posLabel:    POSICAO_LABEL[l.posicaoLamina] ?? '—',
        corLabel:    COR_LAMINA_LABEL[l.corLamina] ?? '—',
        padraoLabel: PADRAO_LABEL[l.padraoLamina] ?? 'Nenhum',
      })),
    })),
  );
}

