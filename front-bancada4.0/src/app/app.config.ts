import { ApplicationConfig, inject, provideAppInitializer, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';

import {
  provideHttpClient,
  withInterceptors,
} from '@angular/common/http';
import { conexaoInterceptor } from './core/interceptors/conexao.interceptor';

import { ConfigService } from './core/service/config.service';
import { ConexaoService } from './core/service/conexao.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withInterceptors([conexaoInterceptor])),
    provideAppInitializer(async () => {
      // inject() precisa ser chamado de forma síncrona, antes de qualquer await.
      const config = inject(ConfigService);
      const conexao = inject(ConexaoService);
      // Carrega o apiUrl primeiro (connect depende dele), depois tenta reconectar
      // com a config salva no localStorage (best-effort: não bloqueia o boot se falhar).
      await config.loadConfig();
      await conexao.tryReconnect();
    })
  ]
};
