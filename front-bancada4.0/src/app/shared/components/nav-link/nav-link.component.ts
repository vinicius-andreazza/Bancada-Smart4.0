import { Component, computed, inject, input } from '@angular/core';
import { NavigationEnd, Router, RouterLink } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { filter, map } from 'rxjs';
 
/**
 * NavLinkComponent
 *
 * A single navigation anchor for use inside the app header.
 * Tracks its own active state against the current URL.
 * Displays a glowing underline indicator when active.
 *
 * @example
 * <app-nav-link label="Dashboard" route="/dashboard" />
 * <app-nav-link label="Pedidos"   route="/pedidos"   ariaLabel="Ir para Ver Pedidos" />
 */
@Component({
  selector: 'app-nav-link',
  imports: [RouterLink],
  template: `
    <a
      [routerLink]="route()"
      [class]="linkClasses()"
      [attr.aria-current]="active() ? 'page' : null"
      [attr.aria-label]="ariaLabel() || label()"
    >
      <span
        class="font-rajdhani text-[0.9375rem] font-semibold uppercase tracking-[0.06em] pt-0.5
               transition-[color,text-shadow] duration-200"
        [style.text-shadow]="active() ? '0 0 12px rgba(91,192,248,0.45)' : 'none'"
      >
        {{ label() }}
      </span>
 
      <span
        class="absolute bottom-0 left-1/2 -translate-x-1/2 h-0.5 rounded-t-sm bg-accent
               transition-all duration-300 ease-[cubic-bezier(0.4,0,0.2,1)]"
        [style.width]="active() ? '70%' : '0'"
        [style.opacity]="active() ? '1' : '0'"
        [style.box-shadow]="active() ? '0 0 8px rgba(91,192,248,0.55)' : 'none'"
        aria-hidden="true"
      ></span>
    </a>
  `,
  host: {
    class: 'flex items-center h-full',
  },
})
export class NavLinkComponent {
  private readonly router = inject(Router);
 
  readonly label     = input.required<string>();
  readonly route     = input.required<string>();
  readonly ariaLabel = input('');
 
  // ── Active state ────────────────────────────────────────────
  private readonly currentUrl = toSignal(
    this.router.events.pipe(
      filter((e): e is NavigationEnd => e instanceof NavigationEnd),
      map((e) => e.urlAfterRedirects),
    ),
    { initialValue: this.router.url },
  );
 
  /**
   * True when the current URL matches this link's route.
   * Handles both exact matches and child routes (e.g. /pedidos/123).
   */
  readonly active = computed(() => {
    const url   = this.currentUrl() ?? '';
    const route = this.route();
    return url === route || url.startsWith(route + '/');
  });
 
  // ── Dynamic classes ──────────────────────────────────────────
  /**
   * Classes are computed (not split across static + binding) so that
   * Tailwind JIT can statically analyse every class name in the source.
   */
  readonly linkClasses = computed(() => {
    const base =
      'relative flex flex-col items-center justify-center h-full px-5 no-underline ' +
      'transition-colors duration-200 ' +
      'focus-visible:outline focus-visible:outline-2 focus-visible:outline-accent focus-visible:-outline-offset-2';
 
    return this.active()
      ? `${base} text-accent hover:bg-accent/5`
      : `${base} text-muted  hover:text-bright hover:bg-panel`;
  });
}