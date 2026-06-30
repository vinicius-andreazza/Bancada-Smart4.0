import { Component, input } from '@angular/core';
import { BadgeComponent } from '../badge/badge.component';

@Component({
  selector: 'app-section-header',
  imports: [BadgeComponent],
  template: `
    <div class="flex items-center gap-2">
      <h3 class="text-lg font-semibold text-gray-100">
        {{ title() }}
      </h3>
      @if (badge()) {
        <app-badge [variant]="badgeVariant()">{{ badge() }}</app-badge>
      }
    </div>
  `,
})
export class SectionHeaderComponent {
  title        = input.required<string>();
  badge        = input<string>('');
  badgeVariant = input<'blue' | 'green' | 'red' | 'gray'>('blue');
}