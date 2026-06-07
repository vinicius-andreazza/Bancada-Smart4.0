import { Component, computed, input } from '@angular/core';


export type ButtonVariant = 'primary' | 'secondary' | 'ghost' | 'danger';
export type ButtonSize    = 'sm' | 'md' | 'lg';
export type ButtonType    = 'button' | 'submit' | 'reset';

const VARIANT: Record<ButtonVariant, string> = {
  primary:
    'bg-accent text-base hover:bg-sky-300 focus-visible:outline-accent ' +
    'shadow-[0_0_16px_rgba(91,192,248,0.25)] hover:shadow-[0_0_24px_rgba(91,192,248,0.4)]',

  secondary:
    'bg-panel text-bright border border-rule ' +
    'hover:border-accent/50 hover:text-accent focus-visible:outline-accent',

  ghost:
    'text-muted hover:text-bright hover:bg-panel focus-visible:outline-accent',

  danger:
    'bg-red-500/10 text-red-400 border border-red-500/30 ' +
    'hover:bg-red-500/20 hover:border-red-400 focus-visible:outline-red-400',
};

const SIZE: Record<ButtonSize, string> = {
  sm: 'px-3   py-1.5 text-xs  gap-1.5',
  md: 'px-5   py-2   text-sm  gap-2',
  lg: 'px-7   py-3   text-base gap-2.5',
};

@Component({
  selector: 'app-button',
  template: `
    <button
      [type]="type()"
      [disabled]="disabled() || loading()"
      [class]="buttonClasses()"
      [attr.aria-disabled]="(disabled() || loading()) || null"
      [attr.aria-busy]="loading() || null"
    >
      @if (loading()) {
        <svg
          class="animate-spin h-4 w-4 shrink-0"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          aria-hidden="true"
          focusable="false"
        >
          <circle
            class="opacity-25"
            cx="12" cy="12" r="10"
            stroke="currentColor"
            stroke-width="4"
          />
          <path
            class="opacity-75"
            fill="currentColor"
            d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"
          />
        </svg>
      }

      <span class="inline-flex items-center gap-[inherit]" [class.invisible]="loading()">
        <ng-content />
      </span>

    </button>
  `,
  host: {
    class: 'contents',
  },
})
export class ButtonComponent {
  readonly variant  = input<ButtonVariant>('primary');
  readonly size     = input<ButtonSize>('md');
  readonly type     = input<ButtonType>('button');
  readonly disabled = input(false);
  readonly loading  = input(false);

  readonly buttonClasses = computed(() => {
    const base = [
      'relative inline-flex items-center justify-center',
      'font-rajdhani font-semibold tracking-[0.08em] uppercase rounded',
      'transition-all duration-200 active:scale-[0.97]',
      'focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2',
      'disabled:opacity-50 disabled:cursor-not-allowed disabled:pointer-events-none',
    ].join(' ');

    return `${base} ${SIZE[this.size()]} ${VARIANT[this.variant()]}`;
  });
}