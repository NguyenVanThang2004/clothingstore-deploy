package vn.ClothingStore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import vn.ClothingStore.domain.OrderDetail;
import vn.ClothingStore.domain.Product;
import vn.ClothingStore.domain.Review;
import vn.ClothingStore.domain.User;
import vn.ClothingStore.domain.request.review.ReqReviewDTO;
import vn.ClothingStore.domain.request.review.ReqUpdateReviewDTO;
import vn.ClothingStore.domain.response.review.ResReviewDTO;
import vn.ClothingStore.repository.OrderDetailRepository;
import vn.ClothingStore.repository.OrderRepository;
import vn.ClothingStore.repository.ProductRepository;
import vn.ClothingStore.repository.ReviewRepository;
import vn.ClothingStore.repository.UserRepository;
import vn.ClothingStore.util.constant.OrderStatusEnum;
import vn.ClothingStore.util.error.IdInvalidException;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository,
            ProductRepository productRepository, OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }
    // get review by product

    public List<Review> getReviewsByProductId(int productId) throws IdInvalidException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy sản phẩm với id " + productId));
        return reviewRepository.findByProductId(productId);
    }

    public ResReviewDTO convertToResReviewDTO(Review review) {
        ResReviewDTO res = new ResReviewDTO();

        res.setId(review.getId());
        res.setRating(review.getRating());
        res.setComment(review.getComment());
        res.setCreatedAt(review.getCreatedAt());
        res.setUpdatedAt(review.getUpdatedAt());

        // user
        if (review.getUser() != null) {
            ResReviewDTO.UserDTO userDTO = new ResReviewDTO.UserDTO();
            userDTO.setId(review.getUser().getId());
            userDTO.setName(review.getUser().getFullName());

            res.setUser(userDTO);
        }
        // order detail

        if (review.getOrderDetail() != null) {
            ResReviewDTO.OrderDetailDTO orderDetailDTO = new ResReviewDTO.OrderDetailDTO();
            orderDetailDTO.setId(review.getOrderDetail().getId());
            orderDetailDTO.setNameProduct(review.getOrderDetail().getProduct().getName());
            orderDetailDTO.setColor(review.getOrderDetail().getProductVariant().getColor());
            orderDetailDTO.setSize(review.getOrderDetail().getProductVariant().getSize());

            res.setOrderDetail(orderDetailDTO);

        }

        return res;
    }

    // create
    public Review createReview(ReqReviewDTO req) throws IdInvalidException {

        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy user với id " + req.getUserId()));

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy sản phẩm với id " + req.getProductId()));
        OrderDetail orderDetail = orderDetailRepository.findById(req.getOrderDetailId())
                .orElseThrow(
                        () -> new IdInvalidException("Không tìm thấy orderDetail với id " + req.getOrderDetailId()));

        boolean hasDeliveredOrder = orderRepository.existsByUserIdAndStatusAndOrderDetailsProductId(
                user.getId(), OrderStatusEnum.DELIVERED, product.getId());

        if (!hasDeliveredOrder) {
            throw new IdInvalidException("Bạn chỉ có thể đánh giá khi đơn hàng đã được giao thành công");
        }
        if (reviewRepository.existsByOrderDetailId(req.getOrderDetailId())) {
            throw new IdInvalidException("Đơn hàng này đã được đánh giá rồi");
        }

        Review review = new Review();
        review.setRating(req.getRating());
        review.setComment(req.getComment());
        review.setUser(user);
        review.setProduct(product);
        review.setOrderDetail(orderDetail);

        return reviewRepository.save(review);

    }

    // update
    public Review updatReview(int reviewId, ReqUpdateReviewDTO req) throws IdInvalidException {

        Review currentReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy review với id " + reviewId));
        currentReview.setRating(req.getRating());
        currentReview.setComment(req.getComment());
        return reviewRepository.save(currentReview);

    }

    public void deleteReview(int reviewId) throws IdInvalidException {
        Review review = this.reviewRepository.findById(reviewId).orElse(null);
        if (review == null) {
            throw new IdInvalidException("Review với id = " + reviewId + " không tồn tại");
        }
        this.reviewRepository.delete(review);
    }

}
