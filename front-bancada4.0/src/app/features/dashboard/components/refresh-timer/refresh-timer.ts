import { Component, input } from '@angular/core';

@Component({
  selector: 'app-refresh-timer',
  imports: [],
  templateUrl: './refresh-timer.html',
  styleUrl: './refresh-timer.css',
})
export class RefreshTimer {

  seconds = input.required<number>();
}
