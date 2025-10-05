// src/app/app-routing.module.ts
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

// Layout & pages (client)
import { LayoutClientComponent } from './components/layout-client/layout-client.component';
import { HomeComponent } from './components/home/home.component';
import { ShopComponent } from './components/shop/shop.component';
import { ShopingCartComponent } from './components/shoping-cart/shoping-cart.component';
import { BlogComponent } from './components/blog/blog.component';
import { BlogDetailComponent } from './components/blog-detail/blog-detail.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { CheckoutComponent } from './components/checkout/checkout.component';
import { ProductDetailComponent } from './components/product-detail/product-detail.component';
import { ProfileComponent } from './components/profile/profile.component';
import { MyOrdersComponent } from './components/my-order/my-order.component';
import { PaymentResultComponent } from './components/payment-result/payment-result.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';

// Layout & pages (admin)
import { LayoutAdminComponent } from './components/admin/layout-admin/layout-admin.component';
import { ProductsComponent } from './components/admin/products/products.component';
import { OrdersComponent } from './components/admin/orders/orders.component';
import { UsersComponent } from './components/admin/users/users.component';
import { ReportsComponent } from './components/admin/reports/reports.component';
import { SettingsComponent } from './components/admin/settings/settings.component';

// Guards
import { adminMatchGuard } from './guards/admin-match.guard';
import { RoleGuard } from './guards/role.guard';

// Standalone system pages
import { ForbiddenComponent } from './components/forbidden/forbidden.component';
import { NotFoundComponent } from './components/not-found/not-found.component';
import { authGuard } from './guards/auth.guard';

const routes: Routes = [
  {
    path: '',
    component: LayoutClientComponent,
    children: [
      { path: '', component: HomeComponent },
      { path: 'shop', component: ShopComponent },
      { path: 'shoping-cart', component: ShopingCartComponent },
      { path: 'blog', component: BlogComponent },
      { path: 'blog-detail', component: BlogDetailComponent },
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      { path: 'forgot-password', component: ForgotPasswordComponent },

      // cần đăng nhập
      { path: 'checkout', component: CheckoutComponent, canActivate: [authGuard] },
      { path: 'product-detail/:id', component: ProductDetailComponent },
      { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
      { path: 'my-order', component: MyOrdersComponent, canActivate: [authGuard] },
      { path: 'payment-result', component: PaymentResultComponent, canActivate: [authGuard] },
    ]
  },
  {
    path: 'admin',
    component: LayoutAdminComponent,
    canMatch: [adminMatchGuard],
    children: [
      { path: '', redirectTo: 'products', pathMatch: 'full' },
      { path: 'products', component: ProductsComponent },
      { path: 'orders', component: OrdersComponent },
      { path: 'users', component: UsersComponent },
      { path: 'settings', component: SettingsComponent },
      { path: 'reports', component: ReportsComponent }
    ]
  },
  { path: '403', component: ForbiddenComponent },
  { path: '**', component: NotFoundComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    scrollPositionRestoration: 'enabled',
    anchorScrolling: 'enabled'
  })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
