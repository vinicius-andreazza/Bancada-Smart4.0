import { Component, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { ConfigService } from './core/service/config.service';

@Component({
  selector: 'app-root',
  template: `
    @if (configFalhou()) {
      <div class="flex min-h-screen flex-col items-center justify-center gap-4 bg-bg-base px-6 text-center">
        <i class="fa-solid fa-triangle-exclamation text-4xl text-status-erro" aria-hidden="true"></i>
        <h1 class="font-heading text-xl font-semibold text-text-primary">
          Falha ao carregar a configuração
        </h1>
        <p class="max-w-md text-sm text-text-secondary">
          Não foi possível carregar o arquivo <code>config/config.json</code>. Verifique se o
          arquivo existe e está acessível no servidor e tente novamente.
        </p>
        <button
          type="button"
          class="mt-2 rounded-lg border border-border-subtle bg-bg-card px-4 py-2 text-sm
                 font-medium text-text-primary transition-colors hover:bg-bg-base"
          (click)="recarregar()"
        >
          <i class="fa-solid fa-rotate-right" aria-hidden="true"></i>
          Tentar novamente
        </button>
      </div>
    } @else {
      <router-outlet />
    }
  `,
  imports: [RouterOutlet]
})
export class App {
  protected readonly title = signal('front-bancada4.0');
  protected readonly configFalhou = inject(ConfigService).loadFailed;

  protected recarregar(): void {
    location.reload();
  }
}
