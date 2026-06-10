import { Component, computed, input } from '@angular/core';
import { ReactiveFormsModule, FormControl, AbstractControl } from '@angular/forms';

@Component({
  selector: 'app-select-field',
  imports: [ReactiveFormsModule],
  template: `
    <div class="flex flex-col gap-1.5">
      <label
        [for]="id()"
        class="text-sm font-medium text-gray-600 dark:text-gray-400"
      >
        {{ label() }}
      </label>
      <select
        [id]="id()"
        [formControl]="formControl()"
        class="w-full rounded-lg border border-gray-700
               bg-gray-900 text-gray-100
               px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500
               transition-all duration-200"
      >
        <ng-content />
      </select>
    </div>
  `,
})
export class SelectFieldComponent {
  id      = input.required<string>();
  label   = input.required<string>();
  control = input.required<AbstractControl>();

  formControl = computed(() => this.control() as FormControl);
}