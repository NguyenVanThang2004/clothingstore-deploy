export interface ResOrderDTO {
    id: number;
    fullname: string;
    email: string;
    phoneNumber: string;
    address: string;
    note: string;
    orderDate: string;
    status: string;
    totalMoney: number;
    shippingMethod: string;
    shippingAddress: string;
    shippingDate: string;
    trackingNumber: string;
    paymentMethod: string;
    active: boolean;
    user: UserDTO;
    orderDetails: OrderDetailDTO[];
}

export interface UserDTO {
    id: number;
    name: string;
    email: string;
}

export interface OrderDetailDTO {
    id: number;
    productId: number;
    productName: string;
    variantId: number;
    size: string;
    color: string;
    categoryId: number;
    categoryName: string;
    price: number;
    numberOfProducts: number;
    totalMoney: number;
}

/**
 * Dùng cho POST /orders
 */
export interface ReqOrderDTO {
    fullname: string;
    email: string;
    phoneNumber: string;
    address: string;
    note?: string;
    shippingMethod: string;
    shippingAddress: string;
    paymentMethod: string;
    userId: number;
    orderDetails: {
        productId: number;
        variantId: number;
        categoryId: number;
        numberOfProducts: number;
        price: number;
    }[];
}


/**
 * Dùng cho PUT /orders/{id}
 */
export interface ReqUpdateOrderStatusDTO {
    status: string;
}
