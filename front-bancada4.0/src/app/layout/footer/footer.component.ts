import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-footer',
  imports: [CommonModule],
  host: {
    class: 'block w-full',
    role: 'contentinfo',
  },
  templateUrl: './footer.component.html'
})
export class Footer {
  readonly currentDate = computed(() => ' 2026');

  readonly footerStyles = computed(() => ({
    background: 'var(--color-bg-surface, #0c1018)',
    borderTop: '1px solid var(--color-border-subtle, #1a2540)',
  }));

  readonly scanlineStyles = computed(() => ({
    backgroundImage: 'repeating-linear-gradient(0deg, transparent, transparent 2px, rgba(91,192,248,0.03) 2px, rgba(91,192,248,0.03) 4px)',
  }));

  readonly borderGlowStyles = computed(() => ({
    background: 'linear-gradient(90deg, transparent 0%, var(--color-text-accent, #5bc0f8) 50%, transparent 100%)',
    opacity: '0.25',
  }));

  readonly pingStyles = computed(() => ({
    backgroundColor: 'var(--color-text-accent, #5bc0f8)',
  }));

  readonly dotStyles = computed(() => ({
    backgroundColor: 'var(--color-text-accent, #5bc0f8)',
  }));

  readonly systemNameStyles = computed(() => ({
    color: 'var(--color-text-primary, #dde6f0)',
    letterSpacing: '0.2em',
  }));

  readonly exxerStyles = computed(() => ({
    color: 'var(--color-text-accent, #5bc0f8)',
    textShadow: 'var(--shadow-glow-blue, 0 0 10px rgba(59,130,246,0.40))',
  }));

  readonly mobileDividerStyles = computed(() => ({
    background: 'var(--color-border-subtle, #1a2540)',
  }));

  readonly devInfoStyles = computed(() => ({
    color: 'var(--color-text-secondary, #7a8fa8)',
  }));

  readonly labelStyles = computed(() => ({
    color: 'var(--color-text-accent, #5bc0f8)',
    opacity: '0.6',
  }));

  readonly dateStyles = computed(() => ({
    color: 'var(--color-text-secondary, #7a8fa8)',
    opacity: '0.7',
  }));
}