import { Component, computed, DestroyRef, inject, input, output, signal } from "@angular/core";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule } from "@angular/forms";
import { Subscription } from "rxjs";
import { ButtonComponent } from "../../../../shared/components/button/button.component";
import { ModalComponent } from "../../../../shared/components/modal/modal.component";
import { StatusPedido } from "../../../../core/models/enums/statuspedido.enum";
import { PedidoDetalheBlocoComponent } from "./pedido-detalhe-bloco/pedido-detalhe-bloco.component";
import { PedidoCard } from "../pedido-card/pedido-card.component";
import { Pedido } from "../../../../core/models/pedido.model";
import { STATUS_LABEL, TIPO_LABEL, TAMPA_LABEL } from "../../shared/utils/pedido-labels";
import { criarBlocoForm, criarBlocoVazio } from "../../shared/pedido-form.factory";
import { Pedido3dPreviewComponent } from "../pedido-3d-preview/pedido-3d-preview.component";
import {
  PedidoPreviewConfig,
  BlocoConfig,
  LaminaConfig,
  CorBloco as CorBlocoViewer,
  CorLamina as CorLaminaViewer,
  PadraoLamina as PadraoLaminaViewer,
  PosicaoLamina as PosicaoLaminaViewer,
  CorTampa as CorTampaViewer,
} from "../pedido-3d-preview/bloco-3d-viewer.component";

const STATUS_BADGE_CLASS: Record<StatusPedido, string> = {
  [StatusPedido.PENDENTE]:  'bg-amber-500/10 text-amber-400 border border-amber-500/20',
  [StatusPedido.PRODUCAO]:  'bg-blue-500/10  text-blue-400  border border-blue-500/20',
  [StatusPedido.CONCLUIDO]: 'bg-green-500/10 text-green-400 border border-green-500/20',
  [StatusPedido.CANCELADO]: 'bg-red-500/10   text-red-400   border border-red-500/20',
};

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
  readonly retirar        = output<Pedido>();
  readonly salvar         = output<Pedido>();
  readonly reiniciar      = output<Pedido>();

  protected readonly StatusPedido = StatusPedido;

  protected readonly editMode = signal(false);
  protected editForm: FormGroup | null = null;

  protected readonly podeEditar = computed(() => this.pedido().status === StatusPedido.PENDENTE);

  protected readonly statusLabel      = computed(() => STATUS_LABEL[this.pedido().status]);
  protected readonly statusBadgeClass = computed(() => STATUS_BADGE_CLASS[this.pedido().status]);

  protected readonly metaInfo = computed(() => [
    { label: 'Tipo',  value: TIPO_LABEL[this.pedido().tipoPedido]  },
    { label: 'Tampa', value: TAMPA_LABEL[this.pedido().corTampa]   },
    { label: 'Data',  value: this.pedido().dataCriacao             },
  ]);

  protected readonly pedidoPreviewConfig = computed<PedidoPreviewConfig>(() => {
    const p = this.pedido();
    const blocos: BlocoConfig[] = p.blocos.map(b => ({
      andar:    b.andar,
      corBloco: b.corBloco as unknown as CorBlocoViewer,
      laminas:  b.laminas.map((l): LaminaConfig => ({
        corLamina:     l.corLamina     as unknown as CorLaminaViewer,
        padraoLamina:  l.padraoLamina  as unknown as PadraoLaminaViewer,
        posicaoLamina: l.posicaoLamina as unknown as PosicaoLaminaViewer,
      })),
    }));
    return {
      blocos,
      corTampa: p.corTampa as unknown as CorTampaViewer,
    };
  });

  protected entrarEdicao(): void {
    const pedido = this.pedido();

    this.editForm = this.formBuilder.group({
      codPedido:  this.formBuilder.control(String(pedido.codPedido)),
      status:     this.formBuilder.control(String(pedido.status)),
      tipoPedido: this.formBuilder.control(String(pedido.tipoPedido)),
      corTampa:   this.formBuilder.control(String(pedido.corTampa)),
      blocos:     this.formBuilder.array(pedido.blocos.map(bloco => criarBlocoForm(this.formBuilder, bloco))),
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
      corTampa:   Number(v.corTampa),
      blocos,
    };

    this.salvar.emit(payload);
  }
}
