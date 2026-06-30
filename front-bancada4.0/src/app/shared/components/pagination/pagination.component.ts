import { Component, computed, input, output } from '@angular/core';
import { ButtonComponent } from '../button/button.component';

@Component({
  selector: 'app-pagination',
  imports: [ButtonComponent],
  template: `
    <div
      class="flex flex-wrap items-center justify-between gap-3 px-5 py-4 border-t border-gray-800"
    >
      <div class="text-xs font-mono tracking-[0.06em] text-gray-500">
        @if (totalElements() > 0) {
          Página {{ pageIndex() + 1 }} de {{ totalPages() }} · {{ totalElements() }} pedidos
        } @else {
          Nenhum pedido
        }
      </div>

      <div class="flex items-center gap-2">
        <app-button
          [type]="'button'"
          [size]="'sm'"
          [variant]="'ghost'"
          [disabled]="isFirst()"
          (click)="anterior()"
        >
          <i class="fa-solid fa-chevron-left" aria-hidden="true"></i>
          Anterior
        </app-button>

        <app-button
          [type]="'button'"
          [size]="'sm'"
          [variant]="'ghost'"
          [disabled]="isLast()"
          (click)="proxima()"
        >
          Próxima
          <i class="fa-solid fa-chevron-right" aria-hidden="true"></i>
        </app-button>
      </div>
    </div>
  `,
})
export class PaginationComponent {
  readonly pageIndex = input(0);          // 0-based
  readonly totalPages = input(0);
  readonly totalElements = input(0);

  /** Estados de extremo vindos do `Page` do Spring; derivados quando não informados. */
  readonly first = input<boolean | undefined>(undefined);
  readonly last = input<boolean | undefined>(undefined);

  readonly pageChange = output<number>();

  protected readonly isFirst = computed(() => this.first() ?? this.pageIndex() <= 0);
  protected readonly isLast = computed(
    () => this.last() ?? this.pageIndex() >= this.totalPages() - 1,
  );

  anterior(): void {
    if (!this.isFirst()) {
      this.pageChange.emit(this.pageIndex() - 1);
    }
  }

  proxima(): void {
    if (!this.isLast()) {
      this.pageChange.emit(this.pageIndex() + 1);
    }
  }
}
