import { Component, computed, DestroyRef, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
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
import { forkJoin } from 'rxjs';
import { ToastNotifications } from "../../../../shared/components/toast-notifications/toast-notifications.component";
import { flashSignal } from '../../../../shared/utils/flash-signal';

@Component({
  selector: 'app-dashboard',
  imports: [RefreshTimer, EstoquePanel, ExpedicaoPanel, Navbar, Footer, ToastNotifications],
  templateUrl: './dashboard.component.html',
})
export class Dashboard implements OnInit, OnDestroy {
  private readonly estoqueService = inject(EstoqueService);
  private readonly expedicaoService = inject(ExpedicaoService);
  private readonly destroyRef = inject(DestroyRef);

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
  readonly changeCount = computed(() => this.pendingChanges().size);

  readonly cellColorMap = computed(() => {
    const map = new Map<number, CorBloco>();
    for (const estoque of this.estoquePositions()) {
      map.set(estoque.posicao, this.getEffectiveColor(estoque));
    }
    return map;
  });

  private refreshInterval?: ReturnType<typeof setInterval>;

  ngOnInit(): void {
    this.refreshAll();

    this.refreshInterval = setInterval(() => {
      if (this.refreshCountdown() <= 1) {
        this.refreshCountdown.set(15);
        this.refreshAll();
        return;
      }
      this.refreshCountdown.update((value) => value - 1);
    }, 1000);
  }

  ngOnDestroy(): void {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
    }
  }

  refreshAll(): void {
    this.refreshEstoque();
    this.refreshExpedicao();
  }

  refreshEstoque(): void {
    this.estoqueService
      .getEstoque()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((estoque) => this.estoquePositions.set(estoque));
  }

  refreshExpedicao(): void {
    this.expedicaoService
      .getExpedicao()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((expedicao) => this.expedicaoPositions.set(expedicao));
  }

  openCellDetail(position: Estoque): void {
    this.selectedPosition.set(position);
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
    const changes = this.pendingChanges();
    if (changes.size === 0) return;

    this.isSaving.set(true);
    this.saveSuccess.set(false);
    this.saveError.set(false);

    const estoqueList = this.estoquePositions();
    const requests = this.estoqueService.saveChanges(estoqueList, changes);

    forkJoin(requests).subscribe({
      next: () => {
        this.isSaving.set(false);
        this.exitEditMode();
        this.refreshEstoque();
        flashSignal(this.saveSuccess, 3000);
      },
      error: () => {
        this.isSaving.set(false);
        flashSignal(this.saveError, 4000);
      },
    });
  }

  onDiscardEstoque(): void {
    this.discardChanges();
    this.refreshEstoque();
  }

  discardChanges(): void {
    this.pendingChanges.set(new Map());
    this.editMode.set(false);
  }
}
