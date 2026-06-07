import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Expedicao } from '../models/expedicao.model';
import { ConfigService } from './config.service';

@Injectable({
  providedIn: 'root',
})
export class ExpedicaoService {

  private readonly http = inject(HttpClient); 
  private readonly config = inject(ConfigService);
  getExpedicao() {
     return this.http.get<Expedicao[]>( `${this.config.apiUrl}/api/expedicao` ); 
  }
}
