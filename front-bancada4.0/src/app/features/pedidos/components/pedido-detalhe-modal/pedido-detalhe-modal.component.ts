import { Component, computed, DestroyRef, inject, input, output, signal } from "@angular/core";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule } from "@angular/forms";
import { Subscription } from "rxjs";
import { ButtonComponent } from "../../../../shared/components/button/button.component";
import { ModalComponent } from "../../../../shared/components/modal/modal.component";
import { StatusPedido } from "../../../../core/models/enums/statuspedido.enum";
import { CorTampa } from "../../../../core/models/enums/cortampa.enum";
import { PedidoDetalheBlocoComponent } from "./pedido-detalhe-bloco/pedido-detalhe-bloco.component";
import { PedidoCard } from "../pedido-card/pedido-card.component";
import { Pedido } from "../../../../core/models/pedido.model";
import {
  STATUS_LABEL,
  STATUS_BADGE_CLASS,
  STATUS_ICON,
  TIPO_LABEL,
  TIPO_BADGE_CLASS,
  tampaLabelNullable,
} from "../../shared/utils/pedido-labels";
import { formatarDataHora, formatarDuracao } from "../../shared/utils/pedido-datas";
import { criarBlocoForm, criarBlocoVazio } from "../../shared/pedido-form.factory";
import { Pedido3dPreviewComponent } from "../pedido-3d-preview/pedido-3d-preview.component";
import {
  PedidoPreviewConfig,
  BlocoConfig,
  LaminaConfig,
} from "../pedido-3d-preview/bloco-3d-viewer.component";

interface LaminaFormValue {
  id?: number | null;
  corLamina: string;
  padraoLamina: string;
  posicaoLamina: string;
}

interface BlocoFormValue {
  id?: number | null;
  andar: number;
  corBloco: string;
  laminas: LaminaFormValue[];
}

interface EditFormValue {
  codPedido: string;
  status: string;
  tipoPedido: string;
  possuiTampa: string;
  corTampa: string;
  blocos: BlocoFormValue[];
}

@Component({
  selector: 'app-pedido-detalhe-modal',
  imports: [ModalComponent, ButtonComponent, PedidoDetalheBlocoComponent, PedidoCard, ReactiveFormsModule, Pedido3dPreviewComponent],
  templateUrl: './pedido-detalhe-modal.component.html',
})
export class PedidoDetalheModalComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);

  private tipoSub?: Subscription;

  readonly pedido         = input.required<Pedido>();
  readonly fechar         = output<void>();
  readonly enviarProducao = output<Pedido>();
  readonly refazer        = output<Pedido>();
  readonly salvar         = output<Pedido>();
  readonly reiniciar      = output<Pedido>();

  protected readonly StatusPedido = StatusPedido;

  protected readonly editMode = signal(false);
  protected editForm: FormGroup | null = null;

  protected readonly podeEditar = computed(() => this.pedido().status === StatusPedido.PENDENTE);

  protected readonly statusLabel      = computed(() => STATUS_LABEL[this.pedido().status]);
  protected readonly statusBadgeClass = computed(() => STATUS_BADGE_CLASS[this.pedido().status]);
  protected readonly statusIcon       = computed(() => STATUS_ICON[this.pedido().status]);

  protected readonly metaInfo = computed<
    { label: string; icon: string; value: string; badgeClass?: string }[]
  >(() => {
    const p = this.pedido();
    const duracao = formatarDuracao(p.dataInicio, p.dataEntrada);
    const producao = duracao ?? (p.status === StatusPedido.PRODUCAO ? 'Em andamento' : '—');

    return [
      { label: 'Tipo',      icon: 'fa-solid fa-layer-group',   value: TIPO_LABEL[p.tipoPedido], badgeClass: TIPO_BADGE_CLASS[p.tipoPedido] },
      { label: 'Tampa',     icon: 'fa-solid fa-fill-drip',     value: tampaLabelNullable(p.corTampa) },
      { label: 'Criação',   icon: 'fa-solid fa-calendar-plus', value: formatarDataHora(p.dataCriacao) },
      { label: 'Produção',  icon: 'fa-solid fa-stopwatch',     value: producao                     },
      { label: 'Conclusão', icon: 'fa-solid fa-flag-checkered', value: formatarDataHora(p.dataEntrada) },
    ];
  });

  protected readonly pedidoPreviewConfig = computed<PedidoPreviewConfig>(() => {
    const p = this.pedido();
    const blocos: BlocoConfig[] = p.blocos.map(b => ({
      andar:    b.andar,
      corBloco: b.corBloco,
      laminas:  b.laminas.map((l): LaminaConfig => ({
        corLamina:     l.corLamina,
        padraoLamina:  l.padraoLamina,
        posicaoLamina: l.posicaoLamina,
      })),
    }));
    return {
      blocos,
      corTampa: p.corTampa,
    };
  });

  protected entrarEdicao(): void {
    const pedido = this.pedido();

    this.editForm = this.formBuilder.group({
      codPedido:   this.formBuilder.control(String(pedido.codPedido)),
      status:      this.formBuilder.control(String(pedido.status)),
      tipoPedido:  this.formBuilder.control(String(pedido.tipoPedido)),
      possuiTampa: this.formBuilder.control(pedido.corTampa !== null ? 'true' : 'false'),
      corTampa:    this.formBuilder.control(pedido.corTampa !== null ? String(pedido.corTampa) : '1'),
      blocos:      this.formBuilder.array(pedido.blocos.map(bloco => criarBlocoForm(this.formBuilder, bloco))),
    });

    this.sincronizarBlocosComTipo(this.editForm);

    this.editMode.set(true);
  }

  protected descartar(): void {
    this.tipoSub?.unsubscribe();
    this.tipoSub = undefined;
    this.editMode.set(false);
    this.editForm = null;
  }

  private sincronizarBlocosComTipo(form: FormGroup): void {
    this.tipoSub?.unsubscribe();
    const blocos = form.get('blocos') as FormArray;

    this.tipoSub = form.get('tipoPedido')!.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(tipo => {
        const total = +tipo;
        while (blocos.length < total) blocos.push(criarBlocoVazio(this.formBuilder, blocos.length + 1, true));
        while (blocos.length > total) blocos.removeAt(blocos.length - 1);
      });
  }

  protected confirmarEdicao(): void {
    if (!this.editForm) {
      return;
    }

    const v = this.editForm.value as EditFormValue;

    const blocos = v.blocos.map((b) => ({
      ...b,
      corBloco: Number(b.corBloco),
      laminas: b.laminas.map((l) => ({
        ...l,
        corLamina:     Number(l.corLamina),
        padraoLamina:  Number(l.padraoLamina),
        posicaoLamina: Number(l.posicaoLamina),
      })),
    })) as Pedido['blocos'];

    const payload: Pedido = {
      ...this.pedido(),
      codPedido:  Number(v.codPedido),
      status:     Number(v.status),
      tipoPedido: Number(v.tipoPedido),
      corTampa:   v.possuiTampa === 'true' ? Number(v.corTampa) as CorTampa : null,
      blocos,
    };

    this.salvar.emit(payload);
  }
}
