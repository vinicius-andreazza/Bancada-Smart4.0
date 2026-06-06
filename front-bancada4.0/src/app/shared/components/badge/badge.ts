import { Component, input } from '@angular/core';

@Component({
  selector: 'app-badge',
  template: `
    <span
      class="rounded-md text-sm font-semibold px-2.5 py-0.5"
      [class]="variantClasses[variant()]"
    >
      <ng-content />
    </span>
  `,
})
export class BadgeComponent {
  variant = input<'blue' | 'green' | 'red' | 'gray'>('blue');

  variantClasses: Record<string, string> = {
    blue:  'bg-blue-100  dark:bg-blue-900  text-blue-700  dark:text-blue-300',
    green: 'bg-green-100 dark:bg-green-900 text-green-700 dark:text-green-300',
    red:   'bg-red-100   dark:bg-red-900   text-red-700   dark:text-red-300',
    gray:  'bg-gray-100  dark:bg-gray-800  text-gray-700  dark:text-gray-300',
  };
}