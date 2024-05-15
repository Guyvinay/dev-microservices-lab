import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Subject } from 'rxjs';

@Component({
  selector: 'c-search',
  templateUrl: './search.component.html',
  styleUrl: './search.component.scss'
})
export class SearchComponent {

  @Output() searchChange = new EventEmitter<string>();
  @Input() placeholder = 'Search';

  searchFieldSub:Subject<string> = new Subject();

  onSearch(event: Event) {
    const target = event.target as HTMLInputElement;
    const value = target.value;
    this.searchChange.emit(value);
  }
}
