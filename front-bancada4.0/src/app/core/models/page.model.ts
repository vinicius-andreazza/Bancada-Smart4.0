/**
 * Espelha o `Page` padrão do Spring Data (serialização padrão, sem
 * `spring.data.web.pageable.serialization` configurado no backend).
 */
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;          // página atual (0-based)
  size: number;
  numberOfElements: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}
