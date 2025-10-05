export interface ReviewDTO {
    id: number;
    rating: number;
    comment: string;
    createdAt: string;
    user: {
        id: number;
        name: string;
    };
    orderDetail: {
        id: number;
        nameProduct: string;
        color: string;
        size: string;
    };
}


export interface ReqReviewDTO {
    rating: number;
    comment: string;
    userId: number;
    productId: number;
    orderDetailId: number;
}

export interface ReqUpdateReviewDTO {
    rating: number;
    comment: string;
}