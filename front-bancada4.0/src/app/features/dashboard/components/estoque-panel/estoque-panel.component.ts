import { Component, computed, inject, input, Input, output } from '@angular/core';
import { Estoque } from '../../../../core/models/estoque.model';
import { CorBloco } from '../../../../core/models/enums/corbloco.enum';
import { EstoqueEditChange, EstoqueService } from '../../../../core/service/estoque.service';
import { ColorPickerComponent } from "../color-pick/color-pick.component";

@Component({
  selector: 'app-estoque-panel',
  imports: [ColorPickerComponent],
  templateUrl: './estoque-panel.component.html',
  styleUrl: './estoque-panel.component.css',
})
export class EstoquePanel {
 
  readonly positions = input.required<Estoque[]>();
  readonly editMode = input.required<boolean>();
  readonly selectedColor = input.required<CorBloco>();
  readonly pendingChanges = input.required<Map<number, EstoqueEditChange>>();
  readonly cellColorMap = input.required<Map<number, CorBloco>>();

  readonly hasChanges = computed(() => this.pendingChanges().size > 0);
  readonly changeCount = computed(() => this.pendingChanges().size);

  readonly cellClick = output<Estoque>();       
  readonly applyColor = output<Estoque>();       
  readonly enterEditMode = output<void>();
  readonly selectColor = output<CorBloco>();
  readonly save = output<void>();
  readonly discard = output<void>();
  readonly refresh = output<void>();

  getCellColor(pos: Estoque): CorBloco {
    return this.cellColorMap().get(pos.posicao) ?? pos.cor;
  }
 
 getCellClass(pos: Estoque): string {
    const cor = this.cellColorMap().get(pos.posicao) ?? pos.cor;
    const changed = this.pendingChanges().has(pos.posicao);
    const base = 'grid-cell';
    const changedClass = changed && this.editMode() ? 'cell-changed' : '';

    switch (cor) {
      case CorBloco.PRETO:    return `${base} cell-preto ${changedClass}`;
      case CorBloco.VERMELHO: return `${base} cell-vermelho ${changedClass}`;
      case CorBloco.AZUL:     return `${base} cell-azul ${changedClass}`;
      default:                return `${base} cell-vazio ${changedClass}`;
    }
  }

  getColorLabel(cor: CorBloco): string {
    switch (cor) {
      case CorBloco.PRETO:    return 'PRETO';
      case CorBloco.VERMELHO: return 'VERMELHO';
      case CorBloco.AZUL:     return 'AZUL';
      default:                return 'VAZIO';
    }
  }


  onCellClick(pos: Estoque): void {
    if (this.editMode()) {
      this.applyColor.emit(pos);
    } else {
      this.cellClick.emit(pos);
    }
  }

  onEnterEditMode(): void {
    this.enterEditMode.emit();
  }

  onColorSelect(cor: CorBloco): void {
    this.selectColor.emit(cor);
  }

  onSave(): void {
    this.save.emit();
  }

  onDiscard(): void {
    this.discard.emit();
  }

}
