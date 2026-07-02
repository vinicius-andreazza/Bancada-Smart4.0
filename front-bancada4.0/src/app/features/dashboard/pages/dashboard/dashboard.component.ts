import { Component, computed, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { EstoquePanel } from "../../components/estoque-panel/estoque-panel.component";
import { ExpedicaoPanel } from "../../components/expedicao-panel/expedicao-panel.component";
import { Estoque } from '../../../../core/models/estoque.model';
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
  imports: [EstoquePanel, ExpedicaoPanel, Navbar, Footer, ToastNotifications],
  templateUrl: './dashboard.component.html',
})
export class Dashboard implements OnInit, OnDestroy {
  private readonly estoqueService = inject(EstoqueService);
  private readonly expedicaoService = inject(ExpedicaoService);

  readonly estoquePositions = computed(() => this.estoqueService.snapshot() ?? []);
  readonly expedicaoPositions = computed(() => this.expedicaoService.snapshot() ?? []);

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

  ngOnInit(): void {
    this.estoqueService.connect();
    this.expedicaoService.connect();
  }

  ngOnDestroy(): void {
    this.estoqueService.disconnect();
    this.expedicaoService.disconnect();
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
  }

  discardChanges(): void {
    this.pendingChanges.set(new Map());
    this.editMode.set(false);
  }
}
