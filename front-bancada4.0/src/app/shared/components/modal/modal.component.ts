import { Component, computed, input, output } from '@angular/core';

@Component({
  selector: 'app-modal',
  imports: [],
  template: `
    <div
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm"
      role="dialog"
      aria-modal="true"
      [attr.aria-labelledby]="labelledBy()"
      (click)="fecharSeBackdrop($event)"
    >
      <div
        class="relative w-full flex flex-col rounded-xl border border-gray-800 bg-gray-950 shadow-2xl"
        [class]="maxWidthClass()"
        [style.max-height]="maxHeight()"
        (click)="$event.stopPropagation()"
      >

        <div class="flex items-start justify-between gap-4 px-6 py-4 border-b border-gray-800 flex-shrink-0">
          <ng-content select="[slot=header]" />

          <button
            type="button"
            class="flex-shrink-0 flex items-center justify-center w-8 h-8 rounded-lg
                   text-gray-500 hover:text-gray-300 hover:bg-gray-800
                   transition-colors duration-150
                   focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-indigo-500"
            aria-label="Fechar modal"
            (click)="fechar.emit()"
          >
            <i class="fa-solid fa-xmark text-sm" aria-hidden="true"></i>
          </button>
        </div>

        <div class="flex-1 overflow-y-auto">
          <ng-content select="[slot=body]" />
        </div>

        @if (hasFooter()) {
          <div class="flex flex-wrap items-center justify-end gap-2 px-6 py-4 border-t border-gray-800 flex-shrink-0">
            <ng-content select="[slot=footer]" />
          </div>
        }

      </div>
    </div>
  `,
})
export class ModalComponent {
  readonly labelledBy = input<string>('');
  readonly maxHeight  = input<string>('90vh');
  readonly maxWidth   = input<'sm' | 'md' | 'lg' | 'xl' | '2xl'>('2xl');
  readonly hasFooter  = input<boolean>(true);
  readonly fechar     = output<void>();

  protected readonly maxWidthClass = computed(() => ({
    sm:  'max-w-sm',
    md:  'max-w-md',
    lg:  'max-w-lg',
    xl:  'max-w-xl',
    '2xl': 'max-w-2xl',
  })[this.maxWidth()]);

  protected fecharSeBackdrop(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.fechar.emit();
    }
  }
}