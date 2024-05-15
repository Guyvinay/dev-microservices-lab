import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-drawer',
  templateUrl: './drawer.component.html',
  styleUrl: './drawer.component.scss'
})
export class DrawerComponent {

  @Input() width: string = '250px';
  @Input() position: 'start' | 'end' = 'start';
  @Input() opened: boolean = false;
  @Output() closed = new EventEmitter<void>();

  onCloseDrawer() {
    this.closed.emit();
  }

}
