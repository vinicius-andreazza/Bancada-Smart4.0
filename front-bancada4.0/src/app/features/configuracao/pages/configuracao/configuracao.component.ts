import { Component, inject, isDevMode, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { BancadaConfig } from '../../../../core/models/bancada-config.model';
import { ConexaoService } from '../../../../core/service/conexao.service';
import { Navbar } from '../../../../layout/navbar/navbar.component';
import { Footer } from '../../../../layout/footer/footer.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';
import { InputFieldComponent } from '../../../../shared/components/input-field/input-field.component';
import { ToastNotifications } from '../../../../shared/components/toast-notifications/toast-notifications.component';

// IPv4 (0-255 por octeto)
const IPV4 =
  /^((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)\.){3}(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)$/;

@Component({
  selector: 'app-configuracao',
  imports: [
    ReactiveFormsModule,
    Navbar,
    Footer,
    InputFieldComponent,
    ButtonComponent,
    ToastNotifications,
  ],
  templateUrl: './configuracao.component.html',
})
export class Configuracao {
  private readonly fb = inject(FormBuilder);
  private readonly conexao = inject(ConexaoService);
  private readonly router = inject(Router);

  readonly isConnecting   = signal(false);
  readonly conexaoErro    = signal(false);
  readonly ativandoLeitura = signal(false);
  readonly erroLeitura    = signal(false);
  readonly possuiSeletor  = signal(false);
  protected readonly isConnected = this.conexao.isConnected;
  protected readonly modoLeitura = this.conexao.modoLeitura;

  /** Modo teste só existe em build de desenvolvimento; nunca em produção. */
  protected readonly modoTesteDisponivel = isDevMode();

  protected readonly erroIp = { pattern: 'IP inválido (ex.: 192.168.0.10).' };

  readonly form = this.fb.group({
    estoqueIp: ['', [Validators.required, Validators.pattern(IPV4)]],
    processoIp: ['', [Validators.required, Validators.pattern(IPV4)]],
    montagemIp: ['', [Validators.required, Validators.pattern(IPV4)]],
    expedicaoIp: ['', [Validators.required, Validators.pattern(IPV4)]],
    seletorTampaIp: [''],
  });

  constructor() {
    const salva = this.conexao.bancadaConfig();
    if (salva) {
      this.form.patchValue(salva);
      if (salva.endpointSeletorTampa) this.possuiSeletor.set(true);
    }
  }

  toggleSeletor(): void {
    const ativo = !this.possuiSeletor();
    this.possuiSeletor.set(ativo);
    if (!ativo) this.form.controls.seletorTampaIp.reset();
  }

  conectar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const v = this.form.getRawValue();
    const config: BancadaConfig = {
      estoqueIp: v.estoqueIp!,
      processoIp: v.processoIp!,
      montagemIp: v.montagemIp!,
      expedicaoIp: v.expedicaoIp!,
      endpointSeletorTampa: v.seletorTampaIp?.trim() ? v.seletorTampaIp : null,
    };

    this.isConnecting.set(true);
    this.conexaoErro.set(false);

    this.conexao.connect(config).subscribe({
      next: () => {
        console.log(config);
        this.isConnecting.set(false);
        this.router.navigate(['/dashboard']);
      },
      error: () => {
        console.log(config);
        this.isConnecting.set(false);
        this.conexaoErro.set(true);
        setTimeout(() => this.conexaoErro.set(false), 4000);
      },
    });
  }

  desconectar(): void {
    this.conexao.desconectarLocal();
    this.conexao.desconectar().subscribe({ error: () => {} });
  }

  /** Entra no sistema em modo de testes, ignorando a validação dos IPs e o backend. */
  entrarModoTeste(): void {
    this.conexao.ativarModoTeste();
    this.router.navigate(['/dashboard']);
  }

  ativarModoLeitura(): void {
    this.ativandoLeitura.set(true);
    this.erroLeitura.set(false);
    this.conexao.ativarModoLeitura().subscribe({
      next: () => this.ativandoLeitura.set(false),
      error: () => {
        this.ativandoLeitura.set(false);
        this.erroLeitura.set(true);
        setTimeout(() => this.erroLeitura.set(false), 4000);
      },
    });
  }

  desativarModoLeitura(): void {
    this.ativandoLeitura.set(true);
    this.conexao.desativarModoLeitura().subscribe({
      next: () => this.ativandoLeitura.set(false),
      error: () => this.ativandoLeitura.set(false),
    });
  }
}
