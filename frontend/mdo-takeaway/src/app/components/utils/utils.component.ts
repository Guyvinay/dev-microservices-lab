import { Component, ElementRef, ViewChild } from '@angular/core';

@Component({
  selector: 'app-utils',
  templateUrl: './utils.component.html',
  styleUrl: './utils.component.scss'
})
export class UtilsComponent {

  inputValue: string = '';
  @ViewChild('contentElement') contentElement!: ElementRef;

  toggleVisibility(): void {
    const contentDiv: HTMLDivElement = this.contentElement.nativeElement;
    contentDiv.style.display = (contentDiv.style.display === 'none') ? 'block' : 'none';
  }

  showInputValue(value: string): void {
    this.inputValue = value;
  }
}
