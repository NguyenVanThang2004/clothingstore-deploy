import { Component, HostListener } from '@angular/core';

@Component({
  selector: 'app-layout-client',
  templateUrl: './layout-client.component.html',
  styleUrls: ['./layout-client.component.css']
})
export class LayoutClientComponent {
  isShow: boolean = false;

  // lắng nghe scroll để show/hide button
  @HostListener('window:scroll', [])
  onWindowScroll() {
    this.isShow = window.pageYOffset > 100;
  }

  scrollToTop() {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
}
