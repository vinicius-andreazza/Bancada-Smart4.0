import { Component, input } from '@angular/core';
import { ReactiveFormsModule, FormGroup } from '@angular/forms';
import { PedidoBlocos } from '../pedido-blocos/pedido-blocos';
import { SelectFieldComponent } from "../../../../shared/components/select-field/select-field";
import { InputFieldComponent } from "../../../../shared/components/input-field/input-field";

@Component({
  selector: 'app-pedido-card',
  imports: [ReactiveFormsModule, PedidoBlocos, SelectFieldComponent, InputFieldComponent],
  templateUrl: './pedido-card.html',
})
export class PedidoCard {
  form = input.required<FormGroup>();

  get tipo(): number {
    return +this.form().get('tipoPedido')?.value;
  }

  get blocos() {
    const formArray = this.form().get('blocos');
    return formArray ? (formArray as any).controls : [];
  }

  updateType(newType: number): void {
    this.form().get('tipoPedido')?.setValue(newType);
  }
}