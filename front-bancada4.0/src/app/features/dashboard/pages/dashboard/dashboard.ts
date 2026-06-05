import { Component, inject, signal } from '@angular/core';
import { RefreshTimer } from "../../components/refresh-timer/refresh-timer";
import { EstoquePanel } from "../../components/estoque-panel/estoque-panel";
import { ExpedicaoPanel } from "../../components/expedicao-panel/expedicao-panel";
import { CellDetailModel } from "../../components/cell-detail-model/cell-detail-model";
import { Estoque } from '../../../../core/models/estoque';
import { Expedicao } from '../../../../core/models/expedicao';
import { EstoqueService } from '../../../../core/service/estoque';
import { ExpedicaoService } from '../../../../core/service/expedicao';
import { Navbar } from '../../../../layout/navbar/navbar'
import { RouterLink } from "@angular/router";
import { Footer } from "../../../../layout/footer/footer";
import { CorBloco } from '../../../../core/models/enums/corbloco';

@Component({
  selector: 'app-dashboard',
  imports: [RefreshTimer, EstoquePanel, ExpedicaoPanel, CellDetailModel, Navbar, RouterLink, Footer],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard {
  private readonly estoqueService =
    inject(EstoqueService);

  private readonly expedicaoService =
    inject(ExpedicaoService);

  readonly estoquePositions =
    signal<Estoque[]>([]);

  readonly expedicaoPositions =
    signal<Expedicao[]>([]);

  readonly refreshCountdown =
    signal(15);

  readonly modalOpen =
    signal(false);

  readonly selectedPosition =
    signal<Estoque | null>(null);

  ngOnInit(): void {
    this.refreshAll();

    setInterval(() => {

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
    console.log(position)
    this.selectedPosition.set(position);

    this.modalOpen.set(true);
    console.log(this.modalOpen())
  }

  closeCellModal() {

    this.modalOpen.set(false);

  }

}
