import { WritableSignal } from '@angular/core';


export function flashSignal(signal: WritableSignal<boolean>, durationMs: number): void {
  signal.set(true);
  setTimeout(() => signal.set(false), durationMs);
}
