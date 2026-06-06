import { Component, signal, effect, inject } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormArray, FormControl } from '@angular/forms';
import { Navbar } from '../../../../layout/navbar/navbar';
import { ButtonComponent } from '../../../../shared/components/button/button';
import { Footer } from '../../../../layout/footer/footer';
import { PedidoCard } from '../../components/pedido-card/pedido-card';

@Component({
  selector: 'app-novo-pedido',
  imports: [ReactiveFormsModule, Navbar, ButtonComponent, Footer, PedidoCard],
  templateUrl: './novo-pedido.html',
})
export class NovoPedido {
  private formBuilder = inject(FormBuilder);

  jsonOutput = signal('');

  pedidoForm: FormGroup = this.formBuilder.group({
    codPedido:  new FormControl('OP-000'),
    status:     new FormControl('1'),
    tipoPedido: new FormControl('1'),
    corTampa:   new FormControl('1'),
    clpIp:      new FormControl('10.74.241.11'),
    blocos:     this.formBuilder.array([this.criarBloco()]),
  });

  constructor() {
    effect(() => {
      const tipo = +this.pedidoForm.get('tipoPedido')?.value;
      const blocos = this.pedidoForm.get('blocos') as FormArray;

      while (blocos.length < tipo) blocos.push(this.criarBloco());
      while (blocos.length > tipo) blocos.removeAt(blocos.length - 1);
    });

    this.pedidoForm.get('tipoPedido')?.valueChanges.subscribe(tipo => {
      const blocos = this.pedidoForm.get('blocos') as FormArray;
      while (blocos.length < +tipo) blocos.push(this.criarBloco());
      while (blocos.length > +tipo) blocos.removeAt(blocos.length - 1);
    });
  }

  private criarBloco(): FormGroup {
    return this.formBuilder.group({
      corBloco:   new FormControl('1'),
      posEstoque: new FormControl(''),
      laminas:    this.formBuilder.array([]),
    });
  }

  gerarJson(): void {
    this.jsonOutput.set(JSON.stringify(this.pedidoForm.value, null, 2));
  }

  enviarPedido(): void {
    console.log(this.pedidoForm.value);
  }
}