import { Component, computed, inject, signal } from '@angular/core';
import { RefreshTimer } from "../../components/refresh-timer/refresh-timer.component";
import { EstoquePanel } from "../../components/estoque-panel/estoque-panel.component";
import { ExpedicaoPanel } from "../../components/expedicao-panel/expedicao-panel.component";
import { CellDetailModel } from "../../components/cell-detail-model/cell-detail-model.component";
import { Estoque } from '../../../../core/models/estoque.model';
import { Expedicao } from '../../../../core/models/expedicao.model';
import { EstoqueEditChange, EstoqueService } from '../../../../core/service/estoque.service';
import { ExpedicaoService } from '../../../../core/service/expedicao.service';
import { Navbar } from '../../../../layout/navbar/navbar.component'
import { Footer } from "../../../../layout/footer/footer.component";
import { CorBloco } from '../../../../core/models/enums/corbloco.enum';
import { catchError, forkJoin, of } from 'rxjs';
import { ToastNotifications } from "../../../../shared/components/toast-notifications/toast-notifications.component";

@Component({
  selector: 'app-dashboard',
  imports: [RefreshTimer, EstoquePanel, ExpedicaoPanel, CellDetailModel, Navbar, Footer, ToastNotifications],
  templateUrl: './dashboard.component.html',
})
export class Dashboard {
  private readonly estoqueService = inject(EstoqueService);
  private readonly expedicaoService = inject(ExpedicaoService);
 
  readonly estoquePositions = signal<Estoque[]>([]);
  readonly expedicaoPositions = signal<Expedicao[]>([]);
  readonly refreshCountdown = signal(15);
  readonly modalOpen = signal(false);
  readonly selectedPosition = signal<Estoque | null>(null);
  readonly saveSuccess = signal(false);
  readonly saveError = signal(false);
  readonly isSaving = signal(false);

  readonly editMode = signal(false);
  readonly selectedColor = signal<CorBloco>(CorBloco.AZUL);
  readonly pendingChanges = signal<Map<number, EstoqueEditChange>>(new Map());
 
  readonly hasChanges = computed(() => this.pendingChanges().size > 0);
  readonly changeCount = computed(() => this.pendingChanges().size)
  
  readonly cellColorMap = computed(() => {
    const map = new Map<number, CorBloco>();
    for (const estoque of this.estoquePositions()) {
      map.set(estoque.posicao, this.getEffectiveColor(estoque));
    }
    return map;
  });

  private refreshInterval: any;
  
  ngOnInit(): void {
    this.refreshAll();

    this.refreshInterval = setInterval(() => {

      const current =
        this.refreshCountdown();

      if (current <= 1) {

        this.refreshCountdown.set(15);

        this.refreshAll();

        return;
      }

      this.refreshCountdown.update(
        value => value - 1
      );

    }, 1000);
  }

  constructor() {
    this.refreshAll();
  }

  refreshAll() {
    this.refreshEstoque();
    this.refreshExpedicao();
  }

  refreshEstoque() {
     this.estoqueService
    .getEstoque()
    .subscribe({
      
      next: (estoque: Estoque[]) => {
        console.log(estoque);
        this.estoquePositions.set(estoque);
      },

      error: (error: any) => {
        console.error(error);
      }

    });
  }
  refreshExpedicao() {
    this.expedicaoService
    .getExpedicao()
    .subscribe({

      next: (expedicao: Expedicao[]) => {
        this.expedicaoPositions.set(expedicao);
      }

    });
  }

  openCellDetail(
    position: Estoque
  ) {
    this.selectedPosition.set(position);

    this.modalOpen.set(true);
  }

  closeCellModal() {
    this.modalOpen.set(false);
  }

  enterEditMode(): void {
    this.editMode.set(true);
    this.pendingChanges.set(new Map());
  }
 
  exitEditMode(): void {
    this.editMode.set(false);
    this.pendingChanges.set(new Map());
  }
 
  selectColor(cor: CorBloco): void {
    this.selectedColor.set(cor);
  }
 
  applyColorToCell(estoque: Estoque): void {
    if (!this.editMode()) return;
 
    const newCor = this.selectedColor();
    const changes = new Map(this.pendingChanges());
 
    const existing = changes.get(estoque.posicao);
    if (existing) {
      if (existing.originalCor === newCor) {
        changes.delete(estoque.posicao);
      } else {
        changes.set(estoque.posicao, { ...existing, newCor });
      }
    } else {
      changes.set(estoque.posicao, {
        posicao: estoque.posicao,
        originalCor: estoque.cor,
        newCor,
      });
    }
 
    this.pendingChanges.set(changes);
  }
 
  getEffectiveColor(estoque: Estoque): CorBloco {
    const change = this.pendingChanges().get(estoque.posicao);
    return change ? change.newCor : estoque.cor;
  }

  onSaveEstoque(): void {
    const changes = this.pendingChanges()
    if (changes.size === 0) return;
 
    this.isSaving.set(true);
    this.saveSuccess.set(false);
    this.saveError.set(false);
 
    const estoqueList = this.estoquePositions();
    const requests = this.estoqueService.saveChanges(estoqueList, changes);
 
    forkJoin(requests).subscribe({
      next: () => {
        this.isSaving.set(false);
        this.saveSuccess.set(true);
        this.exitEditMode();
 
        this.refreshEstoque();
 
        setTimeout(() => this.saveSuccess.set(false), 3000);
      },
      error: () => {
        this.isSaving.set(false);
        this.saveError.set(true);
        setTimeout(() => this.saveError.set(false), 4000);
      },
    });
  }
 
  onDiscardEstoque(): void {
    this.refreshEstoque();
  }

  discardChanges(): void {
    this.pendingChanges.set(new Map());
    this.editMode.set(false);
  }

  ngOnDestroy() {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
    }
  }


}
