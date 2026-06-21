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
import { criarBlocoVazio } from '../../shared/pedido-form.factory';
import { flashSignal } from '../../../../shared/utils/flash-signal';

@Component({
  selector: 'app-novo-pedido',
  imports: [ReactiveFormsModule, Navbar, ButtonComponent, Footer, PedidoCard, ToastNotifications, Pedido3dPreviewComponent],
  templateUrl: './novo-pedido.component.html',
})
export class NovoPedido {
  private readonly formBuilder = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);
  private readonly pedidoService = inject(PedidoService);

  readonly saveSuccess = signal(false);
  readonly saveError = signal(false);
  readonly isSaving = signal(false);

  readonly jsonOutput = signal('');

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

  gerarJson(): void {
    this.jsonOutput.set(JSON.stringify(this.pedidoForm.value, null, 2));
  }

  enviarPedido(): void {
    this.isSaving.set(true);
    this.saveSuccess.set(false);
    this.saveError.set(false);

    this.pedidoService.postPedido(this.pedidoForm.value).subscribe({
      next: () => {
        this.isSaving.set(false);
        flashSignal(this.saveSuccess, 2000);
      },
      error: () => {
        this.isSaving.set(false);
        flashSignal(this.saveError, 3000);
      },
    });
  }
}
