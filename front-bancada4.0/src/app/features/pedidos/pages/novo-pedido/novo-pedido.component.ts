import { Component, signal, inject, DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormArray, FormControl } from '@angular/forms';
import { Navbar } from '../../../../layout/navbar/navbar.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';
import { Footer } from '../../../../layout/footer/footer.component';
import { PedidoCard } from '../../components/pedido-card/pedido-card.component';
import { PedidoService } from '../../../../core/service/pedido.service';
import { ToastNotifications } from '../../../../shared/components/toast-notifications/toast-notifications.component';
import { Pedido3dPreviewComponent } from '../../components/pedido-3d-preview/pedido-3d-preview.component';
import { ModalComponent } from '../../../../shared/components/modal/modal.component';
import { criarBlocoVazio } from '../../shared/pedido-form.factory';
import { flashSignal } from '../../../../shared/utils/flash-signal';
import { Pedido } from '../../../../core/models/pedido.model';

@Component({
  selector: 'app-novo-pedido',
  imports: [
    ReactiveFormsModule,
    Navbar,
    ButtonComponent,
    Footer,
    PedidoCard,
    ToastNotifications,
    Pedido3dPreviewComponent,
    ModalComponent,
  ],
  templateUrl: './novo-pedido.component.html',
})
export class NovoPedido {
  private readonly formBuilder = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);
  private readonly pedidoService = inject(PedidoService);

  readonly saveSuccess          = signal(false);
  readonly saveError            = signal(false);
  readonly isSaving             = signal(false);
  readonly showConfirmModal     = signal(false);
  readonly showProducaoModal    = signal(false);
  readonly isSendingToProducao  = signal(false);
  readonly savedPedido          = signal<Pedido | null>(null);

  readonly pedidoForm: FormGroup = this.formBuilder.group({
    codPedido:  new FormControl('000'),
    status:     new FormControl('1'),
    tipoPedido: new FormControl('1'),
    corTampa:   new FormControl('1'),
    blocos:     this.formBuilder.array([criarBlocoVazio(this.formBuilder, 1)]),
  });

  constructor() {
    this.pedidoForm.get('tipoPedido')!.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((tipo) => {
        const blocos = this.pedidoForm.get('blocos') as FormArray;
        const total = +tipo;
        while (blocos.length < total) blocos.push(criarBlocoVazio(this.formBuilder, blocos.length + 1));
        while (blocos.length > total) blocos.removeAt(blocos.length - 1);
      });
  }

  abrirConfirmacao(): void {
    this.showConfirmModal.set(true);
  }

  fecharConfirmModal(): void {
    this.showConfirmModal.set(false);
  }

  confirmarPedido(): void {
    this.isSaving.set(true);
    this.saveError.set(false);

    this.pedidoService.postPedido(this.pedidoForm.value).subscribe({
      next: (pedido) => {
        this.isSaving.set(false);
        this.savedPedido.set(pedido);
        this.showConfirmModal.set(false);
        this.showProducaoModal.set(true);
      },
      error: () => {
        this.isSaving.set(false);
        flashSignal(this.saveError, 3000);
      },
    });
  }

  enviarParaProducao(): void {
    const pedido = this.savedPedido();
    if (!pedido) return;

    this.isSendingToProducao.set(true);
    this.saveError.set(false);

    this.pedidoService.postEnviarPedido(pedido).subscribe({
      next: () => {
        this.isSendingToProducao.set(false);
        this.showProducaoModal.set(false);
        this.resetarFormulario();
        flashSignal(this.saveSuccess, 2500);
      },
      error: () => {
        this.isSendingToProducao.set(false);
        flashSignal(this.saveError, 3000);
      },
    });
  }

  salvarApenas(): void {
    this.showProducaoModal.set(false);
    this.resetarFormulario();
    flashSignal(this.saveSuccess, 2500);
  }

  private resetarFormulario(): void {
    const blocos = this.pedidoForm.get('blocos') as FormArray;
    while (blocos.length > 0) blocos.removeAt(0);
    blocos.push(criarBlocoVazio(this.formBuilder, 1));
    this.pedidoForm.patchValue({ codPedido: '000', status: '1', tipoPedido: '1', corTampa: '1' });
    this.savedPedido.set(null);
  }
}
