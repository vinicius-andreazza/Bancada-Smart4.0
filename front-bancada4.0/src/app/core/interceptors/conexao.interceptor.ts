import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { ConexaoService } from '../service/conexao.service';

export const conexaoInterceptor: HttpInterceptorFn = (req, next) => {
  const conexao = inject(ConexaoService);

  return next(req).pipe(
    catchError((erro: unknown) => {
      if (erro instanceof HttpErrorResponse) {
        if (erro.status === 409 || erro.status === 0) {
          conexao.desconectar();
        }
      }
      return throwError(() => erro);
    }),
  );
};
