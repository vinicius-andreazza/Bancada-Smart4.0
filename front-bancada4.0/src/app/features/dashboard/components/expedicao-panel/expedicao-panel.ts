import { Component, input, output } from '@angular/core';
import { Expedicao } from '../../../../core/models/expedicao';

@Component({
  selector: 'app-expedicao-panel',
  imports: [],
  templateUrl: './expedicao-panel.html',
  styleUrl: './expedicao-panel.css',
})
export class ExpedicaoPanel {
  positions = input.required<Expedicao[]>();
  refresh = output<void>();
}
