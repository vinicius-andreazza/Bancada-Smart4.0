import {
  afterNextRender,
  Component,
  computed,
  ElementRef,
  input,
  OnDestroy,
  output,
  viewChild,
} from '@angular/core';

const FOCAVEIS_SELECTOR =
  'a[href], button:not([disabled]), input:not([disabled]), select:not([disabled]), textarea:not([disabled]), [tabindex]:not([tabindex="-1"])';

@Component({
  selector: 'app-modal',
  imports: [],
  host: {
    '(document:keydown.escape)': 'fechar.emit()',
  },
  template: `
    <!-- Fechar por teclado é coberto pelo Esc no host; o clique aqui é só o backdrop. -->
    <!-- eslint-disable-next-line @angular-eslint/template/click-events-have-key-events -->
    <div
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm"
      role="dialog"
      aria-modal="true"
      [attr.aria-labelledby]="labelledBy()"
      (click)="fecharSeBackdrop($event)"
      (keydown.tab)="prenderFoco($any($event))"
      (keydown.shift.tab)="prenderFoco($any($event))"
    >
      <div
        #painel
        tabindex="-1"
        class="relative w-full flex flex-col rounded-xl border border-gray-800 bg-gray-950 shadow-2xl focus:outline-none"
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
export class ModalComponent implements OnDestroy {
  readonly labelledBy = input<string>('');
  readonly maxHeight  = input<string>('90vh');
  readonly maxWidth   = input<'sm' | 'md' | 'lg' | 'xl' | '2xl' | '3xl' | '4xl'>('2xl');
  readonly hasFooter  = input<boolean>(true);
  readonly fechar     = output<void>();

  private readonly painel = viewChild.required<ElementRef<HTMLElement>>('painel');

  // O modal é criado/destruído via @if nos consumidores, então o ciclo de vida
  // do componente coincide com abrir/fechar: captura o foco na criação e o
  // devolve na destruição.
  private readonly elementoAnterior =
    document.activeElement instanceof HTMLElement ? document.activeElement : null;

  constructor() {
    afterNextRender(() => {
      this.painel().nativeElement.focus({ preventScroll: true });
    });
  }

  ngOnDestroy(): void {
    this.elementoAnterior?.focus();
  }

  protected readonly maxWidthClass = computed(() => ({
    sm:  'max-w-sm',
    md:  'max-w-md',
    lg:  'max-w-lg',
    xl:  'max-w-xl',
    '2xl': 'max-w-2xl',
    '3xl': 'max-w-3xl',
    '4xl': 'max-w-4xl',
  })[this.maxWidth()]);

  protected fecharSeBackdrop(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.fechar.emit();
    }
  }

  /** Mantém o Tab circulando entre os elementos focáveis do modal. */
  protected prenderFoco(event: KeyboardEvent): void {
    const painel = this.painel().nativeElement;
    const focaveis = painel.querySelectorAll<HTMLElement>(FOCAVEIS_SELECTOR);
    if (focaveis.length === 0) {
      event.preventDefault();
      return;
    }

    const primeiro = focaveis[0];
    const ultimo = focaveis[focaveis.length - 1];
    const ativo = document.activeElement;

    if (event.shiftKey && (ativo === primeiro || ativo === painel)) {
      event.preventDefault();
      ultimo.focus();
    } else if (!event.shiftKey && ativo === ultimo) {
      event.preventDefault();
      primeiro.focus();
    }
  }
}
