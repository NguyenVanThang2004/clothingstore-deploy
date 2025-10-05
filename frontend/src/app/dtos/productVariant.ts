

export interface ProductVariantDTO {
    id?: number;
    color: string;
    size: string;
    price: number;
    stockQuantity: number;
    product: { id: number };
}
