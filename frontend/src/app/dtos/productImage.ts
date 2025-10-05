export interface ProductImageDTO {
    id: number;
    url: string;
    thumbnail: boolean;
}

export interface ProductImageUpload extends ProductImageDTO {
    file?: File; // thêm field phụ cho FE
}