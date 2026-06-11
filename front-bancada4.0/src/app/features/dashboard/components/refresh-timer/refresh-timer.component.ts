import { Component, input } from '@angular/core';

@Component({
  selector: 'app-refresh-timer',
  imports: [],
  templateUrl: './refresh-timer.component.html',
})
export class RefreshTimer {

  seconds = input.required<number>();
}
