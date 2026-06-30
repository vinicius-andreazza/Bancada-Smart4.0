import { Component, input, output } from '@angular/core';
import { AbstractControl } from '@angular/forms';

export interface ToggleOption {
  valor: string;
  label: string;
}

@Component({
  selector: 'app-toggle-group',
  imports: [],
  template: `
    <div class="flex gap-2">
      @for (opt of options(); track opt.valor) {
        <button
          type="button"
          [class]="btnClass(opt.valor)"
          (click)="select(opt.valor)"
        >
          {{ opt.label }}
        </button>
      }
    </div>
  `,
})
export class ToggleGroupComponent {
  options     = input.required<ToggleOption[]>();
  control     = input.required<AbstractControl>();
  size        = input<'sm' | 'md'>('md');
  valueChange = output<string>();

  protected btnClass(valor: string): string {
    const base = 'flex-1 border font-medium transition-all duration-150 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-indigo-500';
    const size  = this.size() === 'sm'
      ? 'px-2 py-1.5 rounded-md text-xs'
      : 'px-3 py-2 rounded-lg text-sm';
    const state = this.control().value == valor
      ? 'bg-indigo-600/20 border-indigo-500 text-indigo-300'
      : 'bg-gray-800 border-gray-700 text-gray-400 hover:border-gray-600 hover:text-gray-300';
    return `${base} ${size} ${state}`;
  }

  protected select(valor: string): void {
    this.control().setValue(valor);
    this.valueChange.emit(valor);
  }
}
