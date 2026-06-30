import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { Router, provideRouter } from '@angular/router';

import { Home } from './home.component';
import { ConexaoService } from '../../../../core/service/conexao.service';

describe('Home', () => {
  let component: Home;
  let fixture: ComponentFixture<Home>;

  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [Home],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(Home);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('acessar() navigates to /configuracao when offline', () => {
    const router = TestBed.inject(Router);
    const conexao = TestBed.inject(ConexaoService);
    const navSpy = vi.spyOn(router, 'navigate').mockResolvedValue(true);

    expect(conexao.isConnected()).toBe(false);
    component.acessar();

    expect(navSpy).toHaveBeenCalledWith(['/configuracao']);
  });

  it('acessar() navigates to /dashboard when connected', () => {
    const router = TestBed.inject(Router);
    const conexao = TestBed.inject(ConexaoService);
    const navSpy = vi.spyOn(router, 'navigate').mockResolvedValue(true);

    conexao.isConnected.set(true);
    component.acessar();

    expect(navSpy).toHaveBeenCalledWith(['/dashboard']);
  });
});
