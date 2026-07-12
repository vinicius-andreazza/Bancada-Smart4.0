import { Component, computed, input, output } from '@angular/core';
import { Estoque } from '../../../../core/models/estoque.model';
import { CorBloco } from '../../../../core/models/enums/corbloco.enum';
import { EstoqueEditChange } from '../../../../core/service/estoque.service';
import { ButtonComponent } from '../../../../shared/components/button/button.component';
import { EstoqueCellComponent } from './estoque-cell/estoque-cell.component';
import { EstoqueEditToolbarComponent } from './estoque-edit-toolbar/estoque-edit-toolbar.component';

@Component({
  selector: 'app-estoque-panel',
  imports: [ButtonComponent, EstoqueCellComponent, EstoqueEditToolbarComponent],
  templateUrl: './estoque-panel.component.html',
})
export class EstoquePanel {
  readonly positions      = input.required<Estoque[]>();
  readonly editMode       = input.required<boolean>();
  readonly selectedColor  = input.required<CorBloco>();
  readonly pendingChanges = input.required<Map<number, EstoqueEditChange>>();
  readonly cellColorMap   = input.required<Map<number, CorBloco>>();

  readonly hasChanges  = computed(() => this.pendingChanges().size > 0);
  readonly changeCount = computed(() => this.pendingChanges().size);

  readonly cellClick     = output<Estoque>();
  readonly applyColor    = output<Estoque>();
  readonly enterEditMode = output<void>();
  readonly selectColor   = output<CorBloco>();
  readonly save          = output<void>();
  readonly discard       = output<void>();
  readonly refresh       = output<void>();

  protected getCellColor(pos: Estoque): CorBloco {
    return this.cellColorMap().get(pos.posicao) ?? pos.cor;
  }

  protected isCellChanged(pos: Estoque): boolean {
    return this.pendingChanges().has(pos.posicao);
  }

  protected onCellClick(pos: Estoque): void {
    if (this.editMode()) {
      this.applyColor.emit(pos);
    } else {
      this.cellClick.emit(pos);
    }
  }
}