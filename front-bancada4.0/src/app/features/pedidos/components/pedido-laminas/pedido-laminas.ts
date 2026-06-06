import { Component, input, output } from '@angular/core';
import { ButtonComponent } from "../../../../shared/components/button/button";
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-pedido-laminas',
  imports: [ReactiveFormsModule],
  templateUrl: './pedido-laminas.html',
  styleUrl: './pedido-laminas.css',
})
export class PedidoLaminas {
  form = input.required<FormGroup>();
}
