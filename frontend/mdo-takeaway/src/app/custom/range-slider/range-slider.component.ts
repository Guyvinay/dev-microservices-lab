import { Component, Input , ViewChild, ViewEncapsulation, Renderer2 } from '@angular/core';

@Component({
  selector: 'app-range-slider',
  templateUrl: './range-slider.component.html',
  styleUrl: './range-slider.component.scss'
})
export class RangeSliderComponent {
  @Input() min: number = 0;
  @Input() max: number = 100;
  @Input() step: number = 1;
  @Input() value: number[] = [20, 80];
  @Input() tickInterval: number = 5;
  @Input() thumbLabel: boolean = true;
  @Input() invert: boolean = false;
  @Input() vertical: boolean = false;
  @Input() disabled: boolean = false;
  @Input() hidePointerLabels: boolean = false;
  @Input() round: boolean = false;
  @Input() snapToTicks: boolean = false;
}
