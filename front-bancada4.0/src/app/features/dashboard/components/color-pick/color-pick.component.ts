import { Component, input, output } from '@angular/core';
import { CorBloco } from '../../../../core/models/enums/corbloco.enum';

export interface ColorOption {
  cor: CorBloco;
  label: string;
  bgClass: string;
  borderClass: string;
  textClass: string;
  glowColor: string;
}

@Component({
  selector: 'app-color-picker',
  imports: [],
  template: `
    <div class="color-picker-bar" role="group" aria-label="Selecionar cor do bloco">

      <span class="color-picker-label">COR:</span>

      @for (option of colorOptions; track option.cor) {
        <button
          type="button"
          class="color-btn"
          [class.color-btn--active]="selectedCor() === option.cor"
          [style.--glow]="option.glowColor"
          (click)="colorSelect.emit(option.cor)"
          [attr.aria-label]="'Selecionar cor ' + option.label"
          [attr.aria-pressed]="selectedCor() === option.cor">

          <span
            class="color-dot"
            [class]="option.bgClass + ' ' + option.borderClass">
          </span>

          <span class="color-name" [class]="option.textClass">
            {{ option.label }}
          </span>

        </button>
      }

    </div>
  `,
  styles: [`
    .color-picker-bar {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 8px 12px;
      background: rgba(13, 20, 36, 0.8);
      border: 1px solid #243558;
      border-radius: 10px;
    }

    .color-picker-label {
      font-family: 'JetBrains Mono', monospace;
      font-size: 0.6rem;
      font-weight: 700;
      letter-spacing: 0.15em;
      color: #3d5068;
      margin-right: 4px;
      flex-shrink: 0;
    }

    .color-btn {
      display: flex;
      align-items: center;
      gap: 5px;
      padding: 5px 10px;
      border-radius: 6px;
      border: 1px solid transparent;
      background: transparent;
      cursor: pointer;
      transition: all 0.15s ease;
      outline: none;
    }

    .color-btn:hover {
      background: rgba(255, 255, 255, 0.05);
      border-color: rgba(255,255,255,0.08);
    }

    .color-btn--active {
      background: rgba(255, 255, 255, 0.07);
      border-color: rgba(255,255,255,0.15);
      box-shadow: 0 0 12px var(--glow, rgba(91,192,248,0.3));
    }

    .color-dot {
      width: 14px;
      height: 14px;
      border-radius: 3px;
      border: 1px solid;
      flex-shrink: 0;
      transition: box-shadow 0.15s;
    }

    .color-btn--active .color-dot {
      box-shadow: 0 0 8px var(--glow, rgba(91,192,248,0.5));
    }

    .color-name {
      font-family: 'JetBrains Mono', monospace;
      font-size: 0.62rem;
      font-weight: 600;
      letter-spacing: 0.08em;
      text-transform: uppercase;
    }
  `],
})
export class ColorPickerComponent {
  selectedCor = input.required<CorBloco>();
  colorSelect = output<CorBloco>();

  readonly colorOptions: ColorOption[] = [
    {
      cor: CorBloco.VAZIO,
      label: 'Vazio',
      bgClass: 'bg-block-vazio-bg',
      borderClass: 'border-block-vazio-border',
      textClass: 'text-text-muted',
      glowColor: 'rgba(61,80,104,0.4)',
    },
    {
      cor: CorBloco.PRETO,
      label: 'Preto',
      bgClass: 'bg-block-preto-bg',
      borderClass: 'border-block-preto-border',
      textClass: 'text-block-preto-text',
      glowColor: 'rgba(160,160,160,0.35)',
    },
    {
      cor: CorBloco.VERMELHO,
      label: 'Vermelho',
      bgClass: 'bg-block-vermelho-bg',
      borderClass: 'border-block-vermelho-border',
      textClass: 'text-block-vermelho-text',
      glowColor: 'rgba(229,62,62,0.45)',
    },
    {
      cor: CorBloco.AZUL,
      label: 'Azul',
      bgClass: 'bg-block-azul-bg',
      borderClass: 'border-block-azul-border',
      textClass: 'text-block-azul-text',
      glowColor: 'rgba(59,130,246,0.45)',
    },
  ];
}