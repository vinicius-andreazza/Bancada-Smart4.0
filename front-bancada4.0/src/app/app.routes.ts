import { Routes } from '@angular/router';

export const routes: Routes = [
    {
    path: 'dashboard',
    loadChildren: () =>
      import('./features/dashboard/dashboard.routes')
        .then(m => m.DASHBOARD_ROUTES)
  },

  {
    path: 'pedido/novo',
    loadChildren: () =>
      import('./features/pedidos/novo-pedido.routes')
        .then(m => m.NOVO_PEDIDO_ROUTES)
  },

  {
    path: 'pedidos',
    loadChildren: () =>
      import('./features/pedidos/lista-pedido.routes')
        .then(m => m.LISTA_PEDIDO_ROUTES)
  },

  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },

  {
    path: 'pedidoNovo',
    redirectTo: 'pedido/novo',
    pathMatch: 'full'
  },

  {
    path: 'pedido',
    redirectTo: 'pedidos',
    pathMatch: 'full'
  },
  
  {
    path: '**',
    redirectTo: 'dashboard'
  },
  
];
