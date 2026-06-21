import { Component, computed, input } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormArray, FormControl } from '@angular/forms';
import { ButtonComponent } from '../../../../shared/components/button/button.component';
import { PedidoLaminas } from '../pedido-laminas/pedido-laminas.component';
import { SectionHeaderComponent } from '../../../../shared/components/section-header/section-header.component';
import { ColorSwatchGroupComponent, ColorSwatchOption } from '../../../../shared/components/color-swatch-group/color-swatch-group.component';

@Component({
  selector: 'app-pedido-blocos',
  imports: [ReactiveFormsModule, ButtonComponent, PedidoLaminas, SectionHeaderComponent, ColorSwatchGroupComponent],
  templateUrl: './pedido-blocos.component.html',
})
export class PedidoBlocos {
  form     = input.required<FormGroup>();
  corTampa = input<number | null>(null);

  get laminas(): FormArray {
    return this.form().get('laminas') as FormArray;
  }

  corTampaValue = computed(() => this.corTampa());
  andarValue    = computed(() => +(this.form().get('andar')?.value ?? 1));

  protected readonly corBlocoOpcoes: ColorSwatchOption[] = [
    { valor: '1', label: 'Preto',    hex: '#1c1c1c', glow: 'rgba(160,160,160,0.4)' },
    { valor: '2', label: 'Vermelho', hex: '#c41e1e', glow: 'rgba(229,62,62,0.4)'   },
    { valor: '3', label: 'Azul',     hex: '#1a4dcc', glow: 'rgba(59,130,246,0.4)'  },
  ];

  addLamina(): void {
    this.laminas.push(
      new FormGroup({
        corLamina:     new FormControl('1'),
        padraoLamina:  new FormControl('0'),
        posicaoLamina: new FormControl(String(this.laminas.length + 1 > 3 ? 1 : this.laminas.length + 1)),
      }),
    );
  }

  removerLamina(): void {
    this.laminas.removeAt(this.laminas.length - 1);
  }
}
