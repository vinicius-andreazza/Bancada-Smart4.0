import { Component, input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ToggleGroupComponent, ToggleOption } from '../../../../shared/components/toggle-group/toggle-group.component';
import { ColorSwatchGroupComponent, ColorSwatchOption } from '../../../../shared/components/color-swatch-group/color-swatch-group.component';

@Component({
  selector: 'app-pedido-laminas',
  imports: [ReactiveFormsModule, ToggleGroupComponent, ColorSwatchGroupComponent],
  templateUrl: './pedido-laminas.component.html',
})
export class PedidoLaminas {
  form = input.required<FormGroup>();

  protected readonly corLaminaOpcoes: ColorSwatchOption[] = [
    { valor: '1', label: 'Vermelho', hex: '#e53e3e' },
    { valor: '2', label: 'Azul',     hex: '#2b6cb0' },
    { valor: '3', label: 'Amarelo',  hex: '#d69e2e' },
    { valor: '4', label: 'Verde',    hex: '#276749' },
    { valor: '5', label: 'Preto',    hex: '#171923' },
    { valor: '6', label: 'Branco',   hex: '#e2e8f0' },
  ];

  protected readonly padraoOpcoes = [
    { valor: '0', label: 'Nenhum',  icon: 'fa-ban'   },
    { valor: '1', label: 'Casa',    icon: 'fa-house'  },
    { valor: '2', label: 'Navio',   icon: 'fa-ship'   },
    { valor: '3', label: 'Estrela', icon: 'fa-star'   },
  ];

  protected readonly posicaoOpcoes: ToggleOption[] = [
    { valor: '1', label: 'Esquerda' },
    { valor: '2', label: 'Frente'   },
    { valor: '3', label: 'Direita'  },
  ];
}
