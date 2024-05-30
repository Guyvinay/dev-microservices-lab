import { Component } from '@angular/core';

@Component({
  selector: 'app-material',
  templateUrl: './material.component.html',
  styleUrl: './material.component.scss'
})
export class MaterialComponent {
  openNewConnectionFlow() {
    console.log('Opening new connection flow');
    // Add your logic to open the new connection flow
  }

  openTableViewSettings(setting: string) {
    console.log(`Opening table view settings for: ${setting}`);
    // Add your logic to open the table view settings based on the provided setting
  }
}
