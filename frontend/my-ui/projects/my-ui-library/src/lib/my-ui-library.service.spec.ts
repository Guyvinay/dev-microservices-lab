import { TestBed } from '@angular/core/testing';

import { MyUiLibraryService } from './my-ui-library.service';

describe('MyUiLibraryService', () => {
  let service: MyUiLibraryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MyUiLibraryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
