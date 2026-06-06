import { Component, input, output } from '@angular/core';
import { ButtonComponent } from "../../../../shared/components/button/button";
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { SelectFieldComponent } from "../../../../shared/components/select-field/select-field";

@Component({
  selector: 'app-pedido-laminas',
  imports: [ReactiveFormsModule, SelectFieldComponent],
  templateUrl: './pedido-laminas.html',
  styleUrl: './pedido-laminas.css',
})
export class PedidoLaminas {
  form = input.required<FormGroup>();
}
