import { Component, input, output } from '@angular/core';
import { ButtonComponent } from "../../../../shared/components/button/button.component";
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { SelectFieldComponent } from "../../../../shared/components/select-field/select-field.component";

@Component({
  selector: 'app-pedido-laminas',
  imports: [ReactiveFormsModule, SelectFieldComponent],
  templateUrl: './pedido-laminas.component.html',
})
export class PedidoLaminas {
  form = input.required<FormGroup>();
}
