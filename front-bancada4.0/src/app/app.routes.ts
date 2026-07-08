import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'home',
    loadChildren: () =>
      import('./features/home/home.routes')
        .then(m => m.HOME_ROUTES)
  },

  {
    path: 'configuracao',
    loadChildren: () =>
      import('./features/configuracao/configuracao.routes')
        .then(m => m.CONFIGURACAO_ROUTES)
  },

  {
    path: 'dashboard',
    loadChildren: () =>
      import('./features/dashboard/dashboard.routes')
        .then(m => m.DASHBOARD_ROUTES)
  },

  {
    path: 'monitoramento',
    loadChildren: () =>
      import('./features/monitoramento/monitoramento.routes')
        .then(m => m.MONITORAMENTO_ROUTES)
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
    redirectTo: 'home',
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
    redirectTo: 'home'
  },
  
];
