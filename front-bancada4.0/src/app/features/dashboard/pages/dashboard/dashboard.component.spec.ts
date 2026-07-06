import { TestBed } from '@angular/core/testing';
import { signal } from '@angular/core';

import { Dashboard } from './dashboard.component';
import { EstoqueService } from '../../../../core/service/estoque.service';
import { ExpedicaoService } from '../../../../core/service/expedicao.service';
import { Estoque } from '../../../../core/models/estoque.model';
import { CorBloco } from '../../../../core/models/enums/corbloco.enum';

describe('Dashboard', () => {
  let component: Dashboard;

  const posicao1: Estoque = { posicao: 1, cor: CorBloco.PRETO };
  const posicao2: Estoque = { posicao: 2, cor: CorBloco.VERMELHO };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Dashboard],
      providers: [
        {
          provide: EstoqueService,
          useValue: {
            snapshot: signal<Estoque[] | null>([posicao1, posicao2]),
            connectionStatus: signal('connected'),
            connect: vi.fn(),
            disconnect: vi.fn(),
            saveChanges: vi.fn(() => []),
          },
        },
        {
          provide: ExpedicaoService,
          useValue: {
            snapshot: signal(null),
            connectionStatus: signal('connected'),
            connect: vi.fn(),
            disconnect: vi.fn(),
          },
        },
      ],
    }).compileComponents();

    component = TestBed.createComponent(Dashboard).componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('applyColorToCell', () => {
    it('does nothing outside edit mode', () => {
      component.selectColor(CorBloco.AZUL);
      component.applyColorToCell(posicao1);

      expect(component.pendingChanges().size).toBe(0);
      expect(component.hasChanges()).toBe(false);
    });

    it('registers a pending change with original and new color', () => {
      component.enterEditMode();
      component.selectColor(CorBloco.AZUL);
      component.applyColorToCell(posicao1);

      expect(component.changeCount()).toBe(1);
      expect(component.hasChanges()).toBe(true);
      expect(component.pendingChanges().get(1)).toEqual({
        posicao: 1,
        originalCor: CorBloco.PRETO,
        newCor: CorBloco.AZUL,
      });
      expect(component.getEffectiveColor(posicao1)).toBe(CorBloco.AZUL);
    });

    it('updates newCor keeping originalCor when the same cell is repainted', () => {
      component.enterEditMode();
      component.selectColor(CorBloco.AZUL);
      component.applyColorToCell(posicao1);
      component.selectColor(CorBloco.VERMELHO);
      component.applyColorToCell(posicao1);

      expect(component.changeCount()).toBe(1);
      expect(component.pendingChanges().get(1)).toEqual({
        posicao: 1,
        originalCor: CorBloco.PRETO,
        newCor: CorBloco.VERMELHO,
      });
    });

    it('removes the pending change when the color returns to the original', () => {
      component.enterEditMode();
      component.selectColor(CorBloco.AZUL);
      component.applyColorToCell(posicao1);
      component.selectColor(CorBloco.PRETO);
      component.applyColorToCell(posicao1);

      expect(component.pendingChanges().size).toBe(0);
      expect(component.hasChanges()).toBe(false);
      expect(component.getEffectiveColor(posicao1)).toBe(CorBloco.PRETO);
    });

    it('tracks changes for multiple cells independently', () => {
      component.enterEditMode();
      component.selectColor(CorBloco.AZUL);
      component.applyColorToCell(posicao1);
      component.applyColorToCell(posicao2);

      expect(component.changeCount()).toBe(2);
      expect(component.getEffectiveColor(posicao1)).toBe(CorBloco.AZUL);
      expect(component.getEffectiveColor(posicao2)).toBe(CorBloco.AZUL);
    });
  });

  it('exitEditMode discards pending changes', () => {
    component.enterEditMode();
    component.selectColor(CorBloco.AZUL);
    component.applyColorToCell(posicao1);
    component.exitEditMode();

    expect(component.editMode()).toBe(false);
    expect(component.pendingChanges().size).toBe(0);
    expect(component.getEffectiveColor(posicao1)).toBe(CorBloco.PRETO);
  });
});
