import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { ListaPedidos } from './lista-pedidos.component';
import { ConfigService } from '../../../../core/service/config.service';

describe('ListaPedidos', () => {
  let component: ListaPedidos;
  let fixture: ComponentFixture<ListaPedidos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListaPedidos],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ConfigService, useValue: { apiUrl: 'http://test' } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ListaPedidos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
