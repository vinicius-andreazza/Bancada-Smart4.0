import { Component, input } from '@angular/core';

@Component({
  selector: 'app-table-empty-state',
  template: `
    <div class="flex flex-col items-center justify-center gap-2 py-14 px-4 text-center">
      <i
        [class]="icon() + ' text-3xl text-gray-600'"
        aria-hidden="true"
      ></i>
      <p class="text-sm font-medium text-gray-300 mt-1">{{ title() }}</p>
      <p class="text-xs text-gray-500">{{ description() }}</p>
    </div>
  `,
})
export class TableEmptyStateComponent {
  readonly icon        = input.required<string>();
  readonly title       = input.required<string>();
  readonly description = input<string>('');
}