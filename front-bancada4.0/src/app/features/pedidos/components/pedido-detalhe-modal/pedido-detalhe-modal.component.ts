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
import { Bloco } from "../../../../core/models/bloco.model";
import { Lamina } from "../../../../core/models/lamina.model";
import { CorTampa } from "../../../../core/models/enums/cortampa.enum";
import { TipoPedido } from "../../../../core/models/enums/tipopedido.enum";

const STATUS_LABEL: Record<StatusPedido, string> = {
  [StatusPedido.PENDENTE]:  'Pendente',
  [StatusPedido.PRODUCAO]:  'Em Produção',
  [StatusPedido.CONCLUIDO]: 'Concluído',
};

const STATUS_BADGE_CLASS: Record<StatusPedido, string> = {
  [StatusPedido.PENDENTE]:  'bg-amber-500/10 text-amber-400 border border-amber-500/20',
  [StatusPedido.PRODUCAO]:  'bg-blue-500/10  text-blue-400  border border-blue-500/20',
  [StatusPedido.CONCLUIDO]: 'bg-green-500/10 text-green-400 border border-green-500/20',
};

const TIPO_PEDIDO_LABEL: Record<TipoPedido, string> = {
  [TipoPedido.SIMPLES]: 'Simples',
  [TipoPedido.DUPLO]:   'Duplo',
  [TipoPedido.TRIPLO]:  'Triplo',
};

const COR_TAMPA_LABEL: Record<CorTampa, string> = {
  [CorTampa.PRETO]:    'Preto',
  [CorTampa.VERMELHO]: 'Vermelho',
  [CorTampa.AZUL]:     'Azul',
};

@Component({
  selector: 'app-pedido-detalhe-modal',
  imports: [ModalComponent, ButtonComponent, PedidoDetalheBlocoComponent, PedidoCard, ReactiveFormsModule],
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

  protected readonly StatusPedido = StatusPedido;

  protected readonly editMode = signal(false);
  protected editForm: FormGroup | null = null;

  protected readonly podeEditar = computed(() => this.pedido().status === StatusPedido.PENDENTE);

  protected readonly statusLabel      = computed(() => STATUS_LABEL[this.pedido().status]);
  protected readonly statusBadgeClass = computed(() => STATUS_BADGE_CLASS[this.pedido().status]);

  protected readonly metaInfo = computed(() => [
    { label: 'Tipo',  value: TIPO_PEDIDO_LABEL[this.pedido().tipoPedido] },
    { label: 'Tampa', value: COR_TAMPA_LABEL[this.pedido().corTampa]     },
    { label: 'Data',  value: this.pedido().dataCriacao                   },
  ]);

  protected entrarEdicao(): void {
    const pedido = this.pedido();

    this.editForm = this.formBuilder.group({
      codPedido:  this.formBuilder.control(String(pedido.codPedido)),
      status:     this.formBuilder.control(String(pedido.status)),
      tipoPedido: this.formBuilder.control(String(pedido.tipoPedido)),
      corTampa:   this.formBuilder.control(String(pedido.corTampa)),
      clpIp:      this.formBuilder.control(pedido.clpIp),
      blocos:     this.formBuilder.array(pedido.blocos.map(bloco => this.criarBlocoForm(bloco))),
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
        while (blocos.length < total) blocos.push(this.criarBlocoVazio(blocos.length + 1));
        while (blocos.length > total) blocos.removeAt(blocos.length - 1);
      });
  }

  private criarBlocoVazio(andar: number): FormGroup {
    return this.formBuilder.group({
      id:         this.formBuilder.control(null),
      andar:      this.formBuilder.control(andar),
      corBloco:   this.formBuilder.control('1'),
      posEstoque: this.formBuilder.control(''),
      laminas:    this.formBuilder.array([] as FormGroup[]),
    });
  }

  protected confirmarEdicao(): void {
    if (!this.editForm) {
      return;
    }

    const v = this.editForm.value;

    const payload: Pedido = {
      ...this.pedido(),
      id:         this.pedido().id,
      codPedido:  Number(v.codPedido),
      status:     Number(v.status),
      tipoPedido: Number(v.tipoPedido),
      corTampa:   Number(v.corTampa),
      clpIp:      v.clpIp,
      blocos: v.blocos.map((b: any) => ({
        ...b,
        corBloco:   Number(b.corBloco),
        posEstoque: Number(b.posEstoque),
        laminas: b.laminas.map((l: any) => ({
          ...l,
          corLamina:     Number(l.corLamina),
          padraoLamina:  Number(l.padraoLamina),
          posicaoLamina: Number(l.posicaoLamina),
        })),
      })),
    };

    this.salvar.emit(payload);
  }

  private criarBlocoForm(bloco: Bloco): FormGroup {
    return this.formBuilder.group({
      id:         this.formBuilder.control(bloco.id),
      andar:      this.formBuilder.control(bloco.andar),
      corBloco:   this.formBuilder.control(String(bloco.corBloco)),
      posEstoque: this.formBuilder.control(bloco.posEstoque),
      laminas:    this.formBuilder.array(bloco.laminas.map(lamina => this.criarLaminaForm(lamina))),
    });
  }

  private criarLaminaForm(lamina: Lamina): FormGroup {
    return this.formBuilder.group({
      id:            this.formBuilder.control(lamina.id),
      corLamina:     this.formBuilder.control(String(lamina.corLamina)),
      padraoLamina:  this.formBuilder.control(String(lamina.padraoLamina)),
      posicaoLamina: this.formBuilder.control(String(lamina.posicaoLamina)),
    });
  }
}
