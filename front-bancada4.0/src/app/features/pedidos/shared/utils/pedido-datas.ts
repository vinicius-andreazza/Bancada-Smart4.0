const TRACO = '—';

const DATA_HORA_FMT = new Intl.DateTimeFormat('pt-BR', {
  dateStyle: 'short',
  timeStyle: 'short',
});

const DATA_FMT = new Intl.DateTimeFormat('pt-BR', {
  dateStyle: 'short',
});

function parseData(iso: string | null | undefined): Date | null {
  if (!iso) return null;
  const d = new Date(iso);
  return Number.isNaN(d.getTime()) ? null : d;
}

/** Formata uma data ISO como `dd/MM/yyyy HH:mm` (pt-BR). Retorna `—` se ausente/inválida. */
export function formatarDataHora(iso: string | null | undefined): string {
  const d = parseData(iso);
  return d ? DATA_HORA_FMT.format(d) : TRACO;
}

/** Formata uma data ISO como `dd/MM/yyyy` (pt-BR). Retorna `—` se ausente/inválida. */
export function formatarData(iso: string | null | undefined): string {
  const d = parseData(iso);
  return d ? DATA_FMT.format(d) : TRACO;
}

/**
 * Duração humanizada entre início e fim (ex.: `2h 15min`, `45min`, `1d 3h`).
 * Retorna `null` se faltar alguma ponta ou se a diferença for inválida/negativa,
 * deixando o template decidir o texto de fallback ("Em andamento", "—").
 */
export function formatarDuracao(
  inicioIso: string | null | undefined,
  fimIso: string | null | undefined,
): string | null {
  const inicio = parseData(inicioIso);
  const fim = parseData(fimIso);
  if (!inicio || !fim) return null;

  const ms = fim.getTime() - inicio.getTime();
  if (ms < 0) return null;

  const totalMin = Math.floor(ms / 60000);
  const dias = Math.floor(totalMin / 1440);
  const horas = Math.floor((totalMin % 1440) / 60);
  const min = totalMin % 60;

  if (dias > 0) return `${dias}d ${horas}h`;
  if (horas > 0) return `${horas}h ${min}min`;
  return `${min}min`;
}
