import { TestBed } from '@angular/core/testing';
import { FormArray } from '@angular/forms';
import { of, throwError } from 'rxjs';

import { NovoPedido } from './novo-pedido.component';
import { PedidoService } from '../../../../core/service/pedido.service';
import { Pedido } from '../../../../core/models/pedido.model';

const PEDIDO_FAKE = { id: 1, codPedido: 123 } as unknown as Pedido;

describe('NovoPedido', () => {
  let component: NovoPedido;
  let pedidoServiceMock: {
    postPedido: ReturnType<typeof vi.fn>;
    postEnviarPedido: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    pedidoServiceMock = {
      postPedido: vi.fn(() => of(PEDIDO_FAKE)),
      postEnviarPedido: vi.fn(() => of({})),
    };

    await TestBed.configureTestingModule({
      imports: [NovoPedido],
      providers: [{ provide: PedidoService, useValue: pedidoServiceMock }],
    }).compileComponents();

    component = TestBed.createComponent(NovoPedido).componentInstance;
  });

  const blocos = () => component.pedidoForm.get('blocos') as FormArray;

  it('should create with one bloco in the form', () => {
    expect(component).toBeTruthy();
    expect(blocos().length).toBe(1);
  });

  it('syncs the number of blocos with tipoPedido', () => {
    component.pedidoForm.get('tipoPedido')!.setValue('3');
    expect(blocos().length).toBe(3);

    component.pedidoForm.get('tipoPedido')!.setValue('2');
    expect(blocos().length).toBe(2);
  });

  describe('confirmarPedido (postPedido)', () => {
    it('stores the saved pedido and opens the produção modal on success', () => {
      component.abrirConfirmacao();
      component.confirmarPedido();

      expect(pedidoServiceMock.postPedido).toHaveBeenCalledWith(component.pedidoForm.value);
      expect(component.savedPedido()).toBe(PEDIDO_FAKE);
      expect(component.showConfirmModal()).toBe(false);
      expect(component.showProducaoModal()).toBe(true);
      expect(component.isSaving()).toBe(false);
      expect(component.saveError()).toBe(false);
    });

    it('flashes saveError and keeps the produção modal closed on failure', () => {
      pedidoServiceMock.postPedido.mockReturnValue(throwError(() => new Error('falha')));

      component.confirmarPedido();

      expect(component.saveError()).toBe(true);
      expect(component.isSaving()).toBe(false);
      expect(component.showProducaoModal()).toBe(false);
      expect(component.savedPedido()).toBeNull();
    });
  });

  describe('enviarParaProducao (postEnviarPedido)', () => {
    it('does nothing without a saved pedido', () => {
      component.enviarParaProducao();
      expect(pedidoServiceMock.postEnviarPedido).not.toHaveBeenCalled();
    });

    it('resets the form and flashes saveSuccess on success', () => {
      component.confirmarPedido();
      component.pedidoForm.get('tipoPedido')!.setValue('3');

      component.enviarParaProducao();

      expect(pedidoServiceMock.postEnviarPedido).toHaveBeenCalledWith(PEDIDO_FAKE);
      expect(component.showProducaoModal()).toBe(false);
      expect(component.savedPedido()).toBeNull();
      expect(component.saveSuccess()).toBe(true);
      expect(component.isSendingToProducao()).toBe(false);
      expect(component.pedidoForm.get('codPedido')!.value).toBe('000');
      expect(blocos().length).toBe(1);
    });

    it('flashes saveError and keeps the pedido on failure', () => {
      component.confirmarPedido();
      pedidoServiceMock.postEnviarPedido.mockReturnValue(throwError(() => new Error('falha')));

      component.enviarParaProducao();

      expect(component.saveError()).toBe(true);
      expect(component.isSendingToProducao()).toBe(false);
      expect(component.savedPedido()).toBe(PEDIDO_FAKE);
    });
  });

  it('salvarApenas closes the modal and resets the form without sending', () => {
    component.confirmarPedido();

    component.salvarApenas();

    expect(pedidoServiceMock.postEnviarPedido).not.toHaveBeenCalled();
    expect(component.showProducaoModal()).toBe(false);
    expect(component.savedPedido()).toBeNull();
    expect(component.saveSuccess()).toBe(true);
  });
});
