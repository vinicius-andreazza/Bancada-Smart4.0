import { Component, input, output } from '@angular/core';
import { Estoque } from '../../../../core/models/estoque';

@Component({
  selector: 'app-cell-detail-model',
  imports: [],
  templateUrl: './cell-detail-model.html',
  styleUrl: './cell-detail-model.css',
})
export class CellDetailModel {
  opened = input.required<boolean>();

  position = input<Estoque | null>(null);

  close = output<void>();
}
