import { Component, OnInit, OnDestroy, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/service/auth.service';
import { CartService } from 'src/app/service/cart.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy, AfterViewInit {

  userName = '';
  isLoggedIn = false;
  cartCount = 0;

  private subs = new Subscription();

  constructor(
    private authService: AuthService,
    private cartService: CartService,
    private router: Router
  ) { }

  ngOnInit(): void {
    //  Lắng nghe login/logout để cập nhật header ngay
    this.subs.add(
      this.authService.loggedIn$.subscribe(isLogged => {
        this.isLoggedIn = isLogged;
        if (isLogged) {
          this.authService.getCurrentUserName().subscribe({
            next: (name: string) => this.userName = name || 'Người dùng',
            error: () => this.userName = 'Người dùng'
          });
        } else {
          this.userName = '';
        }
      })
    );

    // Giỏ hàng
    this.subs.add(
      this.cartService.cartCount$.subscribe(count => this.cartCount = count)
    );
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        this.authService.setLogout();
        this.router.navigate(['/login']);
      },
      error: () => {
        this.authService.setLogout();
        this.router.navigate(['/login']);
      }
    });
  }

  ngAfterViewInit(): void {
    this.initMobileMenu();
  }

  initMobileMenu(): void {
    // Mobile menu toggle
    const btnShowMenu = document.querySelector('.btn-show-menu-mobile');
    const menuMobile = document.querySelector('.menu-mobile');
    const hamburger = document.querySelector('.hamburger');

    if (btnShowMenu && menuMobile && hamburger) {
      btnShowMenu.addEventListener('click', () => {
        menuMobile.classList.toggle('show-menu-mobile');
        hamburger.classList.toggle('is-active');
      });

      // Close menu when clicking on menu items
      const menuItems = menuMobile.querySelectorAll('a');
      menuItems.forEach(item => {
        item.addEventListener('click', () => {
          menuMobile.classList.remove('show-menu-mobile');
          hamburger.classList.remove('is-active');
        });
      });

      // Close menu when clicking outside
      document.addEventListener('click', (e) => {
        const target = e.target as Element;
        if (!target.closest('.menu-mobile') && !target.closest('.btn-show-menu-mobile')) {
          menuMobile.classList.remove('show-menu-mobile');
          hamburger.classList.remove('is-active');
        }
      });
    }
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }
}
