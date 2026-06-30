import { Component, computed, inject, input, signal } from '@angular/core';
import { NavLinkComponent } from '../../shared/components/nav-link/nav-link.component'
import { StatusBadgeComponent } from '../../shared/components/status-badges/status-badges.component'
import { NavigationEnd, Router } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { filter, map } from 'rxjs';
import { ConexaoService } from '../../core/service/conexao.service';

interface NavItem {
  readonly label: string;
  readonly route: string;
  readonly ariaLabel: string;
}

@Component({
  selector: 'app-navbar',
  imports: [NavLinkComponent, StatusBadgeComponent],
  templateUrl: './navbar.component.html',
})
export class Navbar {
  private readonly router = inject(Router);
  private readonly conexao = inject(ConexaoService);

  readonly minimal = input(false);
  readonly showStatus = input(true);

  readonly systemStatus = computed<'online' | 'offline'>(() =>
    this.conexao.isConnected() ? 'online' : 'offline',
  );

  readonly navItems: NavItem[] = [
    {
      label: 'Home',
      route: '/home',
      ariaLabel: 'Ir para a Home',
    },
    {
      label: 'Dashboard',
      route: '/dashboard',
      ariaLabel: 'Ir para o Dashboard',
    },
    {
      label: 'Monitoramento',
      route: '/monitoramento',
      ariaLabel: 'Ir para Monitoramento',
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
    {
      label: 'Configuração',
      route: '/configuracao',
      ariaLabel: 'Ir para Configuração',
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

  menuOpen = signal(false);
  toggleMenu() { this.menuOpen.update(v => !v); }
  closeMenu() { this.menuOpen.set(false); }
}
