import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { Router, provideRouter } from '@angular/router';

import { Configuracao } from './configuracao.component';
import { ConexaoService } from '../../../../core/service/conexao.service';

describe('Configuracao', () => {
  let component: Configuracao;
  let fixture: ComponentFixture<Configuracao>;

  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [Configuracao],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(Configuracao);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('starts invalid while station IPs are empty', () => {
    expect(component.form.invalid).toBe(true);
  });

  it('becomes valid with 4 valid station IPs (cap selector optional)', () => {
    component.form.patchValue({
      estoqueIp: '192.168.0.1',
      processoIp: '192.168.0.2',
      montagemIp: '192.168.0.3',
      expedicaoIp: '192.168.0.4',
    });
    expect(component.form.valid).toBe(true);
  });

  it('rejects an invalid IP format', () => {
    component.form.patchValue({
      estoqueIp: '999.999.0.1',
      processoIp: '192.168.0.2',
      montagemIp: '192.168.0.3',
      expedicaoIp: '192.168.0.4',
    });
    expect(component.form.invalid).toBe(true);
  });

  it('entrarModoTeste() connects ignoring validation and navigates to dashboard', () => {
    const router = TestBed.inject(Router);
    const conexao = TestBed.inject(ConexaoService);
    const navSpy = vi.spyOn(router, 'navigate').mockResolvedValue(true);

    // form vazio/inválido — modo de testes deve ignorar isso
    expect(component.form.invalid).toBe(true);
    component.entrarModoTeste();

    expect(conexao.isConnected()).toBe(true);
    expect(navSpy).toHaveBeenCalledWith(['/dashboard']);
  });
});
