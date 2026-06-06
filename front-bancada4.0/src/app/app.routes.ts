import { Routes } from '@angular/router';

export const routes: Routes = [
    {
    path: 'dashboard',
    loadChildren: () =>
      import('./features/dashboard/dashboard.routes')
        .then(m => m.DASHBOARD_ROUTES)
  },

  {
    path: 'pedido',
    loadChildren: () =>
      import('./features/pedidos/novo-pedido.routes')
        .then(m => m.NOVO_PEDIDO_ROUTES)
  },

  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'pedidoNovo',
    redirectTo: 'pedido',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  },
  
];
