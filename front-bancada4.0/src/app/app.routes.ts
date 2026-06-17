import { Routes } from '@angular/router';
import { conexaoGuard } from './core/guard/conexao.guard';

export const routes: Routes = [
  {
    path: 'configuracao',
    loadChildren: () =>
      import('./features/configuracao/configuracao.routes')
        .then(m => m.CONFIGURACAO_ROUTES)
  },

    {
    path: 'dashboard',
    canActivate: [conexaoGuard],
    loadChildren: () =>
      import('./features/dashboard/dashboard.routes')
        .then(m => m.DASHBOARD_ROUTES)
  },

  {
    path: 'monitoramento',
    canActivate: [conexaoGuard],
    loadChildren: () =>
      import('./features/monitoramento/monitoramento.routes')
        .then(m => m.MONITORAMENTO_ROUTES)
  },

  {
    path: 'pedido/novo',
    canActivate: [conexaoGuard],
    loadChildren: () =>
      import('./features/pedidos/novo-pedido.routes')
        .then(m => m.NOVO_PEDIDO_ROUTES)
  },

  {
    path: 'pedidos',
    canActivate: [conexaoGuard],
    loadChildren: () =>
      import('./features/pedidos/lista-pedido.routes')
        .then(m => m.LISTA_PEDIDO_ROUTES)
  },

  {
    path: '',
    redirectTo: 'configuracao',
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
    redirectTo: 'configuracao'
  },
  
];
