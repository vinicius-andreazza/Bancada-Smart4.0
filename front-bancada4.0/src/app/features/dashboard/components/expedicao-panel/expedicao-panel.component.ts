import { Component, input, output } from '@angular/core';
import { Expedicao } from '../../../../core/models/expedicao.model';

@Component({
  selector: 'app-expedicao-panel',
  imports: [],
  templateUrl: './expedicao-panel.component.html',
})
export class ExpedicaoPanel {
  positions = input.required<Expedicao[]>();
  refresh = output<void>();
}
