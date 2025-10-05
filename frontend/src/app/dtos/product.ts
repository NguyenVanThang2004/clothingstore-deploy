import { CategoryDTO } from "./category";

export interface ProductDTO {
    id: number;
    name: string;
    price: number;
    description: string;
    createdAt: string;
    updatedAt: string;
    category: CategoryDTO;

}

export interface ProductPayload {
    name: string;
    price: number;
    description: string;
    categoryId: number;
}
