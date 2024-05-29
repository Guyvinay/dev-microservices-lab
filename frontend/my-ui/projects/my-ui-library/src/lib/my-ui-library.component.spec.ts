import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyUiLibraryComponent } from './my-ui-library.component';

describe('MyUiLibraryComponent', () => {
  let component: MyUiLibraryComponent;
  let fixture: ComponentFixture<MyUiLibraryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyUiLibraryComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MyUiLibraryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
