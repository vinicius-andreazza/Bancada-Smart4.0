import { Component, computed, input } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule, FormControl, AbstractControl } from '@angular/forms';
import { startWith, switchMap } from 'rxjs';

const MENSAGENS_PADRAO: Record<string, string> = {
  required: 'Campo obrigatório.',
  pattern: 'Formato inválido.',
};

@Component({
  selector: 'app-select-field',
  imports: [ReactiveFormsModule],
  template: `
    <div class="flex flex-col gap-1.5">
      <label
        [for]="id()"
        class="text-sm font-medium text-gray-600 dark:text-gray-400"
      >
        {{ label() }}
      </label>
      <select
        [id]="id()"
        [formControl]="formControl()"
        [class.border-status-erro]="exibirErro()"
        [attr.aria-invalid]="exibirErro() || null"
        [attr.aria-describedby]="exibirErro() ? id() + '-erro' : null"
        class="w-full rounded-lg border border-gray-700
               bg-gray-900 text-gray-100
               px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500
               transition-all duration-200"
      >
        <ng-content />
      </select>
      @if (exibirErro()) {
        <span [id]="id() + '-erro'" class="text-xs text-status-erro">{{ mensagemErro() }}</span>
      }
    </div>
  `,
})
export class SelectFieldComponent {
  id      = input.required<string>();
  label   = input.required<string>();
  control = input.required<AbstractControl>();

  /** Mensagens por chave de erro do validator (ex.: { required: 'Selecione uma opção.' }). */
  erroCustom = input<Record<string, string>>({});

  formControl = computed(() => this.control() as FormControl);

  // App zoneless: o estado do control não é signal-reativo por si só; escuta
  // control.events para recomputar invalid/touched a cada mudança.
  private readonly controlEvents = toSignal(
    toObservable(this.control).pipe(
      switchMap((c) => c.events.pipe(startWith(null))),
    ),
  );

  readonly exibirErro = computed(() => {
    this.controlEvents();
    const c = this.control();
    return c.invalid && c.touched;
  });

  readonly mensagemErro = computed(() => {
    this.controlEvents();
    const erros = this.control().errors;
    if (!erros) return '';
    const custom = this.erroCustom();
    for (const chave of Object.keys(erros)) {
      const mensagem = custom[chave] ?? MENSAGENS_PADRAO[chave];
      if (mensagem) return mensagem;
    }
    return 'Valor inválido.';
  });
}
