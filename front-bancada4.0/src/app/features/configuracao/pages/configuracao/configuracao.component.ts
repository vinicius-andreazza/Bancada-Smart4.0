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

  readonly isConnecting = signal(false);
  readonly conexaoErro = signal(false);

  /** Modo teste só existe em build de desenvolvimento; nunca em produção. */
  protected readonly modoTesteDisponivel = isDevMode();

  protected readonly erroIp = { pattern: 'IP inválido (ex.: 192.168.0.10).' };

  readonly form = this.fb.group({
    estoqueIp: ['', [Validators.required, Validators.pattern(IPV4)]],
    processoIp: ['', [Validators.required, Validators.pattern(IPV4)]],
    montagemIp: ['', [Validators.required, Validators.pattern(IPV4)]],
    expedicaoIp: ['', [Validators.required, Validators.pattern(IPV4)]],
    seletorTampasIp: [''],
  });

  constructor() {
    // Pré-preenche com a config salva (localStorage) para o usuário apenas revisar e conectar.
    const salva = this.conexao.bancadaConfig();
    if (salva) {
      this.form.patchValue(salva);
    }
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
      seletorTampasIp: v.seletorTampasIp?.trim() ? v.seletorTampasIp : null,
    };

    this.isConnecting.set(true);
    this.conexaoErro.set(false);

    this.conexao.connect(config).subscribe({
      next: () => {
        this.isConnecting.set(false);
        this.router.navigate(['/dashboard']);
      },
      error: () => {
        this.isConnecting.set(false);
        this.conexaoErro.set(true);
        setTimeout(() => this.conexaoErro.set(false), 4000);
      },
    });
  }

  /** Entra no sistema em modo de testes, ignorando a validação dos IPs e o backend. */
  entrarModoTeste(): void {
    this.conexao.ativarModoTeste();
    this.router.navigate(['/dashboard']);
  }
}
