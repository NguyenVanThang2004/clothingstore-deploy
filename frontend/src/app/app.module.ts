import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToastrModule } from 'ngx-toastr';

import { SlickCarouselModule } from 'ngx-slick-carousel';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { JwtHelperService, JWT_OPTIONS } from '@auth0/angular-jwt';
import { AppRoutingModule } from './app-routing.module';
import { HomeComponent } from './components/home/home.component';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { ProductDetailComponent } from './components/product-detail/product-detail.component';
import { ShopingCartComponent } from './components/shoping-cart/shoping-cart.component';
import { CheckoutComponent } from './components/checkout/checkout.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ShopComponent } from './components/shop/shop.component';
import { BlogComponent } from './components/blog/blog.component';
import { LayoutClientComponent } from './components/layout-client/layout-client.component';
import { MainComponent } from './main/main.component';
import { BlogDetailComponent } from './components/blog-detail/blog-detail.component';
import { LayoutAdminComponent } from './components/admin/layout-admin/layout-admin.component';
import { ProductsComponent } from './components/admin/products/products.component';
import { OrdersComponent } from './components/admin/orders/orders.component';
import { UsersComponent } from './components/admin/users/users.component';
import { ReportsComponent } from './components/admin/reports/reports.component';
import { SettingsComponent } from './components/admin/settings/settings.component';
import { BaseModalComponent } from './shared/base-modal/base-modal.component';
import { TokenInterceptor } from './interceptors/token.interceptor';
import { ProfileComponent } from './components/profile/profile.component';
import { MyOrdersComponent } from './components/my-order/my-order.component';
import { PaymentResultComponent } from './components/payment-result/payment-result.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { ForbiddenComponent } from './components/forbidden/forbidden.component';
import { NotFoundComponent } from './components/not-found/not-found.component';


@NgModule({
  declarations: [

    HomeComponent,
    HeaderComponent,
    FooterComponent,
    ProductDetailComponent,
    ShopingCartComponent,
    CheckoutComponent,
    LoginComponent,
    RegisterComponent,
    ShopComponent,
    BlogComponent,
    MainComponent,
    BlogDetailComponent,
    LayoutClientComponent,
    LayoutAdminComponent,
    ProductsComponent,
    OrdersComponent,
    UsersComponent,
    ReportsComponent,
    SettingsComponent,
    BaseModalComponent,
    ProfileComponent,
    MyOrdersComponent,
    PaymentResultComponent,
    ForgotPasswordComponent,
    ForbiddenComponent,
    NotFoundComponent

  ],
  imports: [
    CommonModule,
    BrowserModule,
    AppRoutingModule,
    SlickCarouselModule,
    FormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot({
      timeOut: 3000,
      positionClass: 'toast-bottom-right',
      preventDuplicates: true,
    })

  ],
  providers: [
    { provide: JWT_OPTIONS, useValue: JWT_OPTIONS },
    JwtHelperService,
    { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true },
  ],
  bootstrap: [MainComponent]
})
export class AppModule { }
