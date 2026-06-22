import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

import { ConexaoService } from '../../../../core/service/conexao.service';
import { Navbar } from '../../../../layout/navbar/navbar.component';
import { Footer } from '../../../../layout/footer/footer.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';

@Component({
  selector: 'app-home',
  imports: [Navbar, Footer, ButtonComponent],
  template: `
    <div class="flex min-h-screen flex-col bg-bg-base">
      <app-navbar [minimal]="true" [showStatus]="false" />

      <main class="flex flex-1 flex-col items-center justify-center gap-16 px-6 py-12">
        
        <div class="flex flex-col items-center gap-12 md:flex-row md:gap-28">
          <img
            src="assets/bancada/Smart40.png"
            alt="Bancada Smart 4.0"
            class="w-96 max-w-lg drop-shadow-[0_0_32px_rgba(91,192,248,0.25)] md:w-[40rem]"
          />

          <div class="flex max-w-lg flex-col gap-5 text-center md:text-left">
            <h1 class="font-rajdhani text-5xl font-bold tracking-widest text-bright sm:text-6xl">
              BANCADA <span class="text-accent">SMART 4.0</span>
            </h1>
            <p class="text-base leading-relaxed text-muted">
              Sistema de controle e monitoramento de bancada industrial.
              Configure a conexão com os dispositivos CLP e gerencie pedidos em tempo real.
            </p>
          </div>
        </div>

        
        <app-button variant="primary" size="lg" (click)="acessar()">
          Acessar
        </app-button>
      </main>

      <app-footer />
    </div>
  `,
})
export class Home {
  private readonly conexao = inject(ConexaoService);
  private readonly router = inject(Router);

  acessar(): void {
    this.router.navigate([this.conexao.isConnected() ? '/dashboard' : '/configuracao']);
  }
}
