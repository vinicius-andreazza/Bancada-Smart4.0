import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { ConexaoService } from '../service/conexao.service';

export const conexaoGuard: CanActivateFn = () => {
  const conexao = inject(ConexaoService);
  const router = inject(Router);

  return conexao.isConnected() ? true : router.createUrlTree(['/configuracao']);
};
