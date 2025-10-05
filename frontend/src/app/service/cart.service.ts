import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface CartItem {
    productId: number;
    variantId?: number;
    categoryId: number;
    name: string;
    price: number;
    size: string;
    color: string;
    quantity: number;
    stockQuantity: number; // tồn hiện tại của variant
    image?: string;
    selected?: boolean;
}

@Injectable({
    providedIn: 'root'
})
export class CartService {
    private storageKey = 'cart_items';
    private checkoutKey = 'checkout_items';
    //  phát số lượng giỏ hàng realtime
    private cartCountSubject = new BehaviorSubject<number>(this.calcCount());
    cartCount$ = this.cartCountSubject.asObservable();

    // ------------------------
    // Storage helpers
    // ------------------------
    private saveCart(items: CartItem[]): void {
        localStorage.setItem(this.storageKey, JSON.stringify(items));
        this.cartCountSubject.next(this.calcCount());
    }

    private loadCart(): CartItem[] {
        const data = localStorage.getItem(this.storageKey);
        return data ? JSON.parse(data) : [];
    }

    getItems(): CartItem[] {
        return this.loadCart();
    }

    clearCart(): void {
        localStorage.removeItem(this.storageKey);
    }

    getTotal(): number {
        return this.getItems().reduce((sum, item) => sum + item.price * item.quantity, 0);
    }

    setCheckoutItems(items: CartItem[]): void {
        localStorage.setItem(this.checkoutKey, JSON.stringify(items));
    }

    getCheckoutItems(): CartItem[] {
        const data = localStorage.getItem(this.checkoutKey);
        return data ? JSON.parse(data) : [];
    }
    private calcCount(): number {
        return this.getItems().reduce((sum, item) => sum + item.quantity, 0);
    }
    // ------------------------
    // Core logic (chặn vượt tồn)
    // ------------------------

    /**
     * Hai dòng giỏ hàng được xem là cùng một item nếu:
     * - cùng productId
     * - và nếu có variantId thì phải cùng variantId;
     *   nếu không có variantId thì so thêm color + size để phân biệt
     */
    private isSameLineItem(a: CartItem, b: CartItem): boolean {
        if (a.productId !== b.productId) return false;

        if (a.variantId != null && b.variantId != null) {
            return a.variantId === b.variantId;
        }

        // Trường hợp không có variantId, fallback so sánh theo color+size
        return a.variantId == null && b.variantId == null
            && a.color === b.color
            && a.size === b.size;
    }

    /**
     * Thêm vào giỏ: cộng dồn số lượng nhưng KHÔNG vượt stock
     */
    addItem(newItem: CartItem): void {
        const items = this.loadCart();

        // Đảm bảo số lượng tối thiểu là 1
        if (!newItem.quantity || newItem.quantity < 1) {
            newItem.quantity = 1;
        }
        // Clamp theo tồn hiện tại
        newItem.quantity = Math.min(newItem.quantity, newItem.stockQuantity);

        const idx = items.findIndex(i => this.isSameLineItem(i, newItem));

        if (idx >= 0) {
            const current = items[idx];
            const mergedQty = current.quantity + newItem.quantity;

            // Cập nhật lại stockQuantity nếu FE vừa fetch tồn mới hơn
            current.stockQuantity = newItem.stockQuantity ?? current.stockQuantity;

            // Không vượt tồn
            current.quantity = Math.min(mergedQty, current.stockQuantity);
            // (tùy bạn) cập nhật các field có thể thay đổi theo variant
            current.price = newItem.price;
            current.image = newItem.image ?? current.image;
            current.name = newItem.name ?? current.name;
            items[idx] = current;
        } else {
            items.push(newItem);
        }

        this.saveCart(items);
    }

    /**
     * Cập nhật số lượng: clamp 1..stock
     */
    updateQuantity(productId: number, variantId: number | undefined, q: number): void {
        const items = this.loadCart();
        const idx = items.findIndex(i =>
            i.productId === productId &&
            ((variantId != null && i.variantId === variantId) ||
                (variantId == null && i.variantId == null))
        );
        if (idx === -1) return;

        const item = items[idx];
        let qty = Number(q) || 1;
        if (qty < 1) qty = 1;
        if (qty > item.stockQuantity) qty = item.stockQuantity;

        item.quantity = qty;
        items[idx] = item;
        this.saveCart(items);
    }

    /**
     * Cập nhật tồn kho mới cho một item trong giỏ (khi FE refetch variant trước checkout).
     * Nếu quantity > stock mới, sẽ clamp lại.
     */
    updateStock(productId: number, variantId: number | undefined, newStock: number): void {
        const items = this.loadCart();
        const idx = items.findIndex(i =>
            i.productId === productId &&
            ((variantId != null && i.variantId === variantId) ||
                (variantId == null && i.variantId == null))
        );
        if (idx === -1) return;

        const item = items[idx];
        item.stockQuantity = Math.max(0, Number(newStock) || 0);
        if (item.quantity > item.stockQuantity) {
            item.quantity = item.stockQuantity;
        }
        items[idx] = item;
        this.saveCart(items);
    }

    removeItem(productId: number, variantId: number | undefined): void {
        let items = this.loadCart();
        items = items.filter(i => !(i.productId === productId &&
            ((variantId != null && i.variantId === variantId) ||
                (variantId == null && i.variantId == null))));
        this.saveCart(items);
    }

    getItemCount(): number {
        return this.getItems().reduce((sum, item) => sum + item.quantity, 0);
    }
}
