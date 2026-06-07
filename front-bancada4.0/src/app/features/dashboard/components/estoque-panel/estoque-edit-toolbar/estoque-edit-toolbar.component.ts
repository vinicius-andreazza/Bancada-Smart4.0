import { Component, input, output } from '@angular/core';
import { ColorPickerComponent } from '../../color-pick/color-pick.component';
import { CorBloco } from '../../../../../core/models/enums/corbloco.enum';

@Component({
  selector: 'app-estoque-edit-toolbar',
  imports: [ColorPickerComponent],
  template: `
    <div
      class="flex items-center gap-4 flex-wrap px-5 py-2.5
             border-b border-blue-500/15 bg-blue-500/[0.04]
             animate-[slideDown_0.2s_ease]"
      role="toolbar"
      aria-label="Ferramentas de edição"
    >
      <div class="flex items-center gap-2 shrink-0" aria-live="polite">
        <span
          class="w-1.5 h-1.5 rounded-full bg-blue-500 shrink-0
                 animate-[editPulse_1.5s_ease_infinite]"
          aria-hidden="true"
        ></span>
        <span class="font-mono text-[0.6rem] font-semibold tracking-[0.12em] uppercase text-blue-400">
          MODO EDIÇÃO — Clique nos blocos para alterar
        </span>
      </div>

      <app-color-picker
        [selectedCor]="selectedColor()"
        (colorSelect)="colorSelect.emit($event)"
      />
    </div>
  `,
})
export class EstoqueEditToolbarComponent {
  readonly selectedColor = input.required<CorBloco>();
  readonly colorSelect   = output<CorBloco>();
}