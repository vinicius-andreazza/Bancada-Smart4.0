import { Component, computed, input } from '@angular/core';

// ── Public types ──────────────────────────────────────────────────────────────
export type SystemStatus = 'online' | 'offline' | 'maintenance';

interface StatusConfig {
  readonly defaultLabel: string;
  readonly dot:  string;
  readonly ring: string;
  readonly text: string; 
  readonly glow: string; 
}

const STATUS_CONFIG: Record<SystemStatus, StatusConfig> = {
  online: {
    defaultLabel: 'SIS. ATIVO',
    dot:  'bg-emerald-400',
    ring: 'bg-emerald-400/25',
    text: 'text-emerald-400',
    glow: '0 0 6px rgba(52,211,153,0.7)',
  },
  offline: {
    defaultLabel: 'OFFLINE',
    dot:  'bg-red-400',
    ring: 'bg-red-400/25',
    text: 'text-red-400',
    glow: '0 0 6px rgba(248,113,113,0.7)',
  },
  maintenance: {
    defaultLabel: 'MANUTENÇÃO',
    dot:  'bg-amber-400',
    ring: 'bg-amber-400/25',
    text: 'text-amber-400',
    glow: '0 0 6px rgba(251,191,36,0.7)',
  },
};

@Component({
  selector: 'app-status-badge',
  template: `
    <div
      class="inline-flex items-center gap-2 px-3 py-1.5 bg-card border border-line rounded"
      role="status"
      [attr.aria-label]="ariaLabel()"
    >
      <!-- Pulsing dot -->
      <span class="relative flex items-center justify-center w-3.5 h-3.5 shrink-0" aria-hidden="true">
        <span
          class="absolute w-full h-full rounded-full animate-pulse-ring"
          [class]="cfg().ring"
        ></span>
        <span
          class="relative z-10 w-2 h-2 rounded-full"
          [class]="cfg().dot"
          [style.box-shadow]="cfg().glow"
        ></span>
      </span>

      <!-- Label -->
      <span
        class="font-mono-tech text-[0.7rem] tracking-[0.12em]"
        [class]="cfg().text"
      >
        {{ resolvedLabel() }}
      </span>
    </div>
  `,
})
export class StatusBadgeComponent {
  readonly status = input<SystemStatus>('online');
  readonly label  = input('');

  readonly cfg           = computed(() => STATUS_CONFIG[this.status()]);
  readonly resolvedLabel = computed(() => this.label() || this.cfg().defaultLabel);
  readonly ariaLabel     = computed(() => `Status do sistema: ${this.resolvedLabel().toLowerCase()}`);
}