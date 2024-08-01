import { Component, ElementRef, ViewChild } from '@angular/core';
import { AppService } from '../../services/app.service';

@Component({
  selector: 'app-utils',
  templateUrl: './utils.component.html',
  styleUrl: './utils.component.scss'
})
export class UtilsComponent {

  inputValue: string = '';
  @ViewChild('contentElement') contentElement!: ElementRef;

  constructor(private appService : AppService) {}

  toggleVisibility(): void {
    const contentDiv: HTMLDivElement = this.contentElement.nativeElement;
    contentDiv.style.display = (contentDiv.style.display === 'none') ? 'block' : 'none';
  }

  showInputValue(value: string): void {
    // this.inputValue = value;
    this.downloadTextFile(value);
  }
  downloadTextFile(value:string) {
    // const data = 'hii';
    this.appService.downloadZipFile(value).subscribe(data=>{
      const blob = new Blob([data], { type: 'application/zip' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `SampleTextDownload_${value}.zip`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
    })
  }
  fileChange(event: any) {
    if (event && event.target) {
      const file = event.target.files[0] as File;
      if (file) {
        console.log(file);
      }
    }
  }
}
