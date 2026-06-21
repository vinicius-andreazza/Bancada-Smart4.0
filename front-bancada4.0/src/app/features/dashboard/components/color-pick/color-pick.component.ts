import { Component, input, output } from '@angular/core';
import { CorBloco } from '../../../../core/models/enums/corbloco.enum';

export interface ColorOption {
  cor: CorBloco;
  label: string;
  dotClass: string;
  textClass: string;
  glowColor: string;
}

@Component({
  selector: 'app-color-picker',
  imports: [],
  template: `
    <div
      class="flex items-center gap-1.5 px-3 py-2 rounded-[10px]
             bg-[rgba(13,20,36,0.8)] border border-border-mid"
      role="group"
      aria-label="Selecionar cor do bloco"
    >

      <span class="font-mono text-[0.6rem] font-bold tracking-[0.15em] text-text-muted mr-1 shrink-0">
        COR:
      </span>

      @for (option of colorOptions; track option.cor) {
        <button
          type="button"
          [style.--glow]="option.glowColor"
          [attr.aria-label]="'Selecionar cor ' + option.label"
          [attr.aria-pressed]="selectedCor() === option.cor"
          (click)="colorSelect.emit(option.cor)"
          [class]="btnClass(option.cor)"
        >
          <span [class]="'w-3.5 h-3.5 rounded-sm border shrink-0 transition-shadow duration-150 ' + option.dotClass"></span>
          <span [class]="'font-mono text-[0.62rem] font-semibold tracking-[0.08em] uppercase ' + option.textClass">
            {{ option.label }}
          </span>
        </button>
      }

    </div>
  `,
})
export class ColorPickerComponent {
  readonly selectedCor = input.required<CorBloco>();
  readonly colorSelect = output<CorBloco>();

  protected btnClass(cor: CorBloco): string {
    const base = 'flex items-center gap-1.5 px-2.5 py-1.5 rounded-md border transition-all duration-150 cursor-pointer outline-none';
    const inactive = 'border-transparent bg-transparent hover:bg-white/5 hover:border-white/10';
    const active = 'bg-white/[0.07] border-white/15 [box-shadow:0_0_12px_var(--glow)]';
    return `${base} ${this.selectedCor() === cor ? active : inactive}`;
  }

  readonly colorOptions: ColorOption[] = [
    {
      cor: CorBloco.VAZIO,
      label: 'Vazio',
      dotClass: 'bg-slate-800 border-slate-600',
      textClass: 'text-slate-500',
      glowColor: 'rgba(61,80,104,0.4)',
    },
    {
      cor: CorBloco.PRETO,
      label: 'Preto',
      dotClass: 'bg-neutral-800 border-neutral-500',
      textClass: 'text-neutral-400',
      glowColor: 'rgba(160,160,160,0.35)',
    },
    {
      cor: CorBloco.VERMELHO,
      label: 'Vermelho',
      dotClass: 'bg-red-700 border-red-500',
      textClass: 'text-red-400',
      glowColor: 'rgba(229,62,62,0.45)',
    },
    {
      cor: CorBloco.AZUL,
      label: 'Azul',
      dotClass: 'bg-blue-700 border-blue-500',
      textClass: 'text-blue-400',
      glowColor: 'rgba(59,130,246,0.45)',
    },
  ];
}