import { Component, input } from '@angular/core';

@Component({
  selector: 'app-toast-notifications',
  imports: [],
  template: `
    <div
      role="alert"
      aria-live="assertive"
      class="flex items-center gap-3 px-4 py-3 rounded-lg border
             text-sm font-medium animate-[toast-slide-in_0.2s_ease-out]"
      [class]="containerClasses[variant()]"
    >
      <i class="fa-solid text-base" [class]="iconClasses[variant()]" aria-hidden="true"></i>
      <span>{{ text() }}</span>
    </div>
  `,
  styles: [`
    @keyframes toast-slide-in {
      from { opacity: 0; transform: translateY(-8px); }
      to   { opacity: 1; transform: translateY(0); }
    }
  `],
})
export class ToastNotifications {
  text    = input.required<string>();
  variant = input.required<'success' | 'error' | 'info'>();

  containerClasses: Record<string, string> = {
    success: 'bg-emerald-500/10 border-emerald-500/30 text-emerald-200',
    error:   'bg-red-500/10    border-red-500/30    text-red-300',
    info:    'bg-blue-500/10   border-blue-500/30   text-blue-300',
  };

  iconClasses: Record<string, string> = {
    success: 'fa-circle-check',
    error:   'fa-triangle-exclamation',
    info:    'fa-spinner fa-spin',
  };
}