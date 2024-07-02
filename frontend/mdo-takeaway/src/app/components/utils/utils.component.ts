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
  downloadTextFile() {
    const data = 'hii';
    const blob = new Blob([data], {type:'text/plain'});
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `SampleTextDownload.txt`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
  }
}
