import { Component, inject } from '@angular/core';
import { NavLinkComponent } from '../../shared/components/nav-link/nav-link'
import { StatusBadgeComponent } from '../../shared/components/status-badges/status-badges'
import { NavigationEnd, Router } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { filter, map } from 'rxjs';

interface NavItem {
  readonly label: string;
  readonly route: string;
  readonly ariaLabel: string;
}

@Component({
  selector: 'app-navbar',
  imports: [NavLinkComponent, StatusBadgeComponent],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  private readonly router = inject(Router);
 
  readonly navItems: NavItem[] = [
    {
      label: 'Dashboard',
      route: '/dashboard',
      ariaLabel: 'Ir para o Dashboard',
    },
    {
      label: 'Criar Pedido',
      route: '/pedido/novo',
      ariaLabel: 'Ir para Criar Pedido',
    },
    {
      label: 'Ver Pedidos',
      route: '/pedidos',
      ariaLabel: 'Ir para Ver Pedidos',
    },
  ];
 
  readonly currentUrl = toSignal(
    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd),
      map((event) => event.urlAfterRedirects),
    ),
    { initialValue: this.router.url },
  );
 
  isActive(route: string): boolean {
    return this.currentUrl()?.startsWith(route) ?? false;
  }
}
