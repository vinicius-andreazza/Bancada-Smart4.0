import { Component, signal, effect, inject } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormArray, FormControl } from '@angular/forms';
import { Navbar } from '../../../../layout/navbar/navbar';
import { ButtonComponent } from '../../../../shared/components/button/button';
import { Footer } from '../../../../layout/footer/footer';
import { PedidoCard } from '../../components/pedido-card/pedido-card';
import { PedidoService } from '../../../../core/service/pedido';
import { Pedido } from '../../../../core/models/pedido';
import { ToastNotifications } from "../../../../shared/components/toast-notifications/toast-notifications";

@Component({
  selector: 'app-novo-pedido',
  imports: [ReactiveFormsModule, Navbar, ButtonComponent, Footer, PedidoCard, ToastNotifications],
  templateUrl: './novo-pedido.html',
})
export class NovoPedido {
  private formBuilder = inject(FormBuilder);

  private readonly pedidoService = inject(PedidoService);

  readonly saveSuccess = signal(false);
  readonly saveError = signal(false);
  readonly isSaving = signal(false);

  jsonOutput = signal('');

  pedidoForm: FormGroup = this.formBuilder.group({
    codPedido:  new FormControl('OP-000'),
    status:     new FormControl('1'),
    tipoPedido: new FormControl('1'),
    corTampa:   new FormControl('1'),
    clpIp:      new FormControl('10.74.241.11'),
    blocos:     this.formBuilder.array([this.criarBloco(1)]),
  });

  constructor() {
    effect(() => {
      const tipo = +this.pedidoForm.get('tipoPedido')?.value;
      const blocos = this.pedidoForm.get('blocos') as FormArray;

      while (blocos.length < tipo) blocos.push(this.criarBloco(blocos.length + 1));
      while (blocos.length > tipo) blocos.removeAt(blocos.length - 1);
    });

    this.pedidoForm.get('tipoPedido')?.valueChanges.subscribe(tipo => {
      const blocos = this.pedidoForm.get('blocos') as FormArray;
      while (blocos.length < +tipo) blocos.push(this.criarBloco(blocos.length + 1));
      while (blocos.length > +tipo) blocos.removeAt(blocos.length - 1);
    });
  }

  private criarBloco(andar: number): FormGroup {
    return this.formBuilder.group({
      andar: new FormControl(andar),
      corBloco:   new FormControl('1'),
      posEstoque: new FormControl(''),
      laminas:    this.formBuilder.array([]),
    });
  }

  gerarJson(): void {
    this.jsonOutput.set(JSON.stringify(this.pedidoForm.value, null, 2));
  }

  enviarPedido(): void {
    this.isSaving.set(true)
    this.saveSuccess.set(false)
    this.saveError.set(false)
    this.pedidoService.postPedido(this.pedidoForm.value).subscribe({
  
      next:()=>{
        this.isSaving.set(false)
        this.saveSuccess.set(true)
        setTimeout(() => this.saveSuccess.set(false), 2000);
      },
      error: () =>{
        this.isSaving.set(false)
        this.saveError.set(true)
        setTimeout(() => this.saveError.set(false), 3000);
      }
    });
  }
}