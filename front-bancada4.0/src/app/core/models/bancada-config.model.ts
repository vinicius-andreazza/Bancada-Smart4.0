export interface BancadaConfig {
  estoqueIp: string;
  processoIp: string;
  montagemIp: string;
  expedicaoIp: string;
  seletorTampasIp?: string | null; // opcional — bancada pode não ter seletor de tampas
}
