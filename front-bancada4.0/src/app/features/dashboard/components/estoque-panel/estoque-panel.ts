import { Component, input, Input, output } from '@angular/core';
import { Estoque } from '../../../../core/models/estoque';

@Component({
  selector: 'app-estoque-panel',
  imports: [],
  templateUrl: './estoque-panel.html',
  styleUrl: './estoque-panel.css',
})
export class EstoquePanel {
  positions = input.required<Estoque[]>()

  refresh = output<void>();

  cellClick = output<Estoque>();
}
