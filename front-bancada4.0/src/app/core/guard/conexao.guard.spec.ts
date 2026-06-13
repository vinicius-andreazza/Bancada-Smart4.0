import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, provideRouter } from '@angular/router';

import { conexaoGuard } from './conexao.guard';
import { ConexaoService } from '../service/conexao.service';

describe('conexaoGuard', () => {
  let conexao: ConexaoService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    });
    conexao = TestBed.inject(ConexaoService);
  });

  const run = () =>
    TestBed.runInInjectionContext(() =>
      conexaoGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot),
    );

  it('redirects to /configuracao when offline', () => {
    const result = run();
    expect(result).toBeInstanceOf(UrlTree);
    expect((result as UrlTree).toString()).toBe('/configuracao');
  });

  it('allows activation when connected', () => {
    conexao.isConnected.set(true);
    expect(run()).toBe(true);
  });
});
