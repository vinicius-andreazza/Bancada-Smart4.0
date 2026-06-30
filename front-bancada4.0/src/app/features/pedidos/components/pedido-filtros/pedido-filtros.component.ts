import { Component, input, output } from '@angular/core';
import { ButtonComponent } from '../../../../shared/components/button/button.component';
import { PedidoFiltro } from '../../../../core/service/pedido.service';

interface FiltroOption {
  value: PedidoFiltro;
  label: string;
}


@Component({
  selector: 'app-pedido-filtros',
  imports: [ButtonComponent],
  template: `
    <div class="flex items-center gap-1 flex-wrap">
      @for (filtro of filtros; track filtro.value) {
        <app-button
          [type]="'button'"
          [size]="'md'"
          [variant]="filtroAtual() === filtro.value ? 'secondary' : 'ghost'"
          (click)="filtroChange.emit(filtro.value)"
        >{{ filtro.label }}</app-button>
      }
    </div>
  `,
})
export class PedidoFiltrosComponent {
  readonly filtroAtual = input.required<PedidoFiltro>();
  readonly filtroChange = output<PedidoFiltro>();

  protected readonly filtros: FiltroOption[] = [
    { value: 'TODOS',     label: 'Todos' },
    { value: 'PENDENTE',  label: 'Pendente' },
    { value: 'PRODUCAO',  label: 'Produção' },
    { value: 'CONCLUIDO', label: 'Concluído' },
    { value: 'CANCELADO', label: 'Cancelado' },
  ];
}
