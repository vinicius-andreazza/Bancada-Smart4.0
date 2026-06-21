import { Component, input } from '@angular/core';
import { AbstractControl } from '@angular/forms';

export interface ColorSwatchOption {
  valor: string;
  label: string;
  hex: string;
  glow?: string;
}

@Component({
  selector: 'app-color-swatch-group',
  imports: [],
  template: `
    <div class="flex flex-wrap gap-2">
      @for (opt of options(); track opt.valor) {
        @if (showLabel()) {
          <button
            type="button"
            class="flex items-center gap-2 px-3 py-2 rounded-lg border transition-all duration-150
                   focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-indigo-500"
            [class]="control().value == opt.valor
              ? 'border-gray-400 bg-gray-800 text-gray-100 shadow-[0_0_10px_var(--glow)]'
              : 'border-gray-700 bg-gray-900 text-gray-400 hover:border-gray-600 hover:text-gray-300'"
            [style.--glow]="opt.glow ?? 'transparent'"
            [title]="opt.label"
            (click)="control().setValue(opt.valor)"
          >
            <span class="w-4 h-4 rounded-full border border-white/10 shrink-0" [style.background]="opt.hex"></span>
            <span class="text-sm font-medium">{{ opt.label }}</span>
          </button>
        } @else {
          <button
            type="button"
            class="w-8 h-8 rounded-full border-2 transition-all duration-150
                   focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-indigo-500"
            [class]="control().value == opt.valor
              ? 'border-white scale-110'
              : 'border-transparent opacity-50 hover:opacity-90 hover:scale-105'"
            [style.background]="opt.hex"
            [title]="opt.label"
            [attr.aria-label]="opt.label"
            (click)="control().setValue(opt.valor)"
          ></button>
        }
      }
    </div>
  `,
})
export class ColorSwatchGroupComponent {
  options   = input.required<ColorSwatchOption[]>();
  control   = input.required<AbstractControl>();
  showLabel = input<boolean>(true);
}
