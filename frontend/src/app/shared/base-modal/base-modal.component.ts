import { Component, Input, Output, EventEmitter, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import 'bootstrap';


@Component({
  selector: 'app-base-modal',
  templateUrl: './base-modal.component.html',
})
export class BaseModalComponent {
  @Input() modalTitle: string = 'Modal title';
  @Input() contentButton: string = 'Submit';
  @Output() submit = new EventEmitter<void>();

  isOpen: boolean = false;

  openModal() {
    this.isOpen = true;
  }

  closeModal() {
    this.isOpen = false;
  }

  handleButtonFunc() {
    this.submit.emit();
    this.closeModal();
  }
}
