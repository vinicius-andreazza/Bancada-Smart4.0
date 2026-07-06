import { Component, input, output } from '@angular/core';
import { Estoque } from '../../../../core/models/estoque.model';

@Component({
  selector: 'app-cell-detail-model',
  imports: [],
  templateUrl: './cell-detail-model.component.html',
})
export class CellDetailModel {
  opened = input.required<boolean>();

  position = input<Estoque | null>(null);

  fechar = output<void>();
}
