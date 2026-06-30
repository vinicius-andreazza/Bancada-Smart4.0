import { Component, input, signal } from '@angular/core';
import { ReactiveFormsModule, FormArray, FormGroup } from '@angular/forms';
import { PedidoBlocos } from '../pedido-blocos/pedido-blocos.component';
import { InputFieldComponent } from '../../../../shared/components/input-field/input-field.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';
import { ToggleGroupComponent, ToggleOption } from '../../../../shared/components/toggle-group/toggle-group.component';
import { ColorSwatchGroupComponent, ColorSwatchOption } from '../../../../shared/components/color-swatch-group/color-swatch-group.component';

@Component({
  selector: 'app-pedido-card',
  imports: [ReactiveFormsModule, PedidoBlocos, InputFieldComponent, ButtonComponent, ToggleGroupComponent, ColorSwatchGroupComponent],
  templateUrl: './pedido-card.component.html',
})
export class PedidoCard {
  form = input.required<FormGroup>();

  get blocos(): FormGroup[] {
    return (this.form().get('blocos') as FormArray).controls as FormGroup[];
  }

  protected readonly editingIndex = signal<number | null>(null);

  protected readonly COR_BLOCO_LABEL: Record<string, string> = {
    '0': 'Vazio', '1': 'Preto', '2': 'Vermelho', '3': 'Azul',
  };
  protected readonly COR_BLOCO_HEX: Record<string, string> = {
    '0': '#8b97a8', '1': '#1c1c1c', '2': '#c41e1e', '3': '#1a4dcc',
  };
  protected readonly TAMPA_LABEL: Record<string, string> = { '1': 'Preto', '2': 'Vermelho', '3': 'Azul' };
  protected readonly TAMPA_HEX:   Record<string, string> = { '1': '#1c1c1c', '2': '#c41e1e', '3': '#1a4dcc' };
  protected readonly COR_LAMINA_LABEL: Record<string, string> = {
    '1': 'Vermelho', '2': 'Azul', '3': 'Amarelo', '4': 'Verde', '5': 'Preto', '6': 'Branco',
  };
  protected readonly PADRAO_LABEL: Record<string, string> = {
    '0': 'Nenhum', '1': 'Casa', '2': 'Navio', '3': 'Estrela',
  };
  protected readonly POSICAO_LABEL: Record<string, string> = {
    '1': 'Esq', '2': 'Frente', '3': 'Dir',
  };

  protected readonly tipoOpcoes: ToggleOption[] = [
    { valor: '1', label: 'Simples' },
    { valor: '2', label: 'Duplo'   },
    { valor: '3', label: 'Triplo'  },
  ];

  protected readonly corTampaOpcoes: ColorSwatchOption[] = [
    { valor: '1', label: 'Preto',    hex: '#1c1c1c', glow: 'rgba(160,160,160,0.4)' },
    { valor: '2', label: 'Vermelho', hex: '#c41e1e', glow: 'rgba(229,62,62,0.4)'   },
    { valor: '3', label: 'Azul',     hex: '#1a4dcc', glow: 'rgba(59,130,246,0.4)'  },
  ];

  protected editarBloco(i: number): void  { this.editingIndex.set(i); }
  protected voltarListagem(): void         { this.editingIndex.set(null); }

  protected get topAndar(): number {
    return this.blocos.reduce((max, b) => Math.max(max, +(b.get('andar')?.value ?? 0)), 0);
  }
}
