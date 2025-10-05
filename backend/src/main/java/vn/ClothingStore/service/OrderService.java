package vn.ClothingStore.service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.ClothingStore.domain.Category;
import vn.ClothingStore.domain.Order;
import vn.ClothingStore.domain.OrderDetail;
import vn.ClothingStore.domain.Product;
import vn.ClothingStore.domain.ProductVariant;
import vn.ClothingStore.domain.User;
import vn.ClothingStore.domain.request.order.ReqOrderDTO;
import vn.ClothingStore.domain.request.order.ReqUpdateOrderStatusDTO;
import vn.ClothingStore.domain.response.ResultPaginationDTO;
import vn.ClothingStore.domain.response.order.ResOrderDTO;
import vn.ClothingStore.repository.CategoryRepository;
import vn.ClothingStore.repository.OrderDetailRepository;
import vn.ClothingStore.repository.OrderRepository;
import vn.ClothingStore.repository.ProductRepository;
import vn.ClothingStore.repository.ProductVariantRepository;
import vn.ClothingStore.repository.UserRepository;
import vn.ClothingStore.specifications.OrderSpecs;
import vn.ClothingStore.util.constant.OrderStatusEnum;
import vn.ClothingStore.util.error.IdInvalidException;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CategoryRepository categoryRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
            ProductVariantRepository productVariantRepository, CategoryRepository categoryRepository,
            OrderDetailRepository orderDetailRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.categoryRepository = categoryRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.userRepository = userRepository;
    }

    public ResOrderDTO convertToResOrderDTO(Order order) {
        ResOrderDTO res = new ResOrderDTO();
        res.setId(order.getId());
        res.setFullname(order.getFullname());
        res.setEmail(order.getEmail());
        res.setPhoneNumber(order.getPhoneNumber());
        res.setAddress(order.getAddress());
        res.setNote(order.getNote());
        res.setOrderDate(order.getOrderDate());
        res.setStatus(order.getStatus());
        res.setTotalMoney(order.getTotalMoney());
        res.setShippingMethod(order.getShippingMethod());
        res.setShippingAddress(order.getShippingAddress());
        res.setShippingDate(order.getShippingDate());
        res.setTrackingNumber(order.getTrackingNumber());
        res.setPaymentMethod(order.getPaymentMethod());
        res.setActive(order.isActive());

        // User
        if (order.getUser() != null) {
            ResOrderDTO.UserDTO userDTO = new ResOrderDTO.UserDTO();
            userDTO.setId(order.getUser().getId());
            userDTO.setName(order.getUser().getFullName());
            userDTO.setEmail(order.getUser().getEmail());
            res.setUser(userDTO);
        }

        // OrderDetails
        if (order.getOrderDetails() != null) {
            List<ResOrderDTO.OrderDetailDTO> detailDTOs = order.getOrderDetails().stream().map(detail -> {
                ResOrderDTO.OrderDetailDTO dto = new ResOrderDTO.OrderDetailDTO();
                dto.setId(detail.getId());

                // Product
                if (detail.getProduct() != null) {
                    dto.setProductId(detail.getProduct().getId());
                    dto.setProductName(detail.getProduct().getName());
                }

                // ProductVariant
                if (detail.getProductVariant() != null) {
                    dto.setVariantId(detail.getProductVariant().getId());
                    dto.setSize(detail.getProductVariant().getSize().name());
                    dto.setColor(detail.getProductVariant().getColor().name());
                }
                // Category
                Category category = detail.getCategory();
                if (category != null) {
                    dto.setCategoryId(category.getId());
                    dto.setCategoryName(category.getName());
                }

                dto.setPrice(detail.getPrice());
                dto.setNumberOfProducts(detail.getNumberOfProducts());
                dto.setTotalMoney(detail.getTotalMoney());
                return dto;
            }).toList();

            res.setOrderDetails(detailDTOs);
        }

        return res;
    }

    public ResultPaginationDTO fetchAllOrder(Specification<Order> spec, Pageable pageable) {
        Sort defaultSort = Sort.by(Sort.Order.desc("orderDate").nullsLast());
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().isSorted() ? pageable.getSort() : defaultSort

        );

        Page<Order> pageOrder = this.orderRepository.findAll(spec, sortedPageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageOrder.getTotalPages());
        mt.setTotal(pageOrder.getTotalElements());

        rs.setMeta(mt);

        // remove sensitive data
        List<ResOrderDTO> listOrder = pageOrder.getContent()
                .stream().map(item -> this.convertToResOrderDTO(item))
                .collect(Collectors.toList());

        rs.setResult(listOrder);

        return rs;
    }

    public Page<ResOrderDTO> fetchAllOrderByUserId(int userId, Pageable pageable) throws IdInvalidException {
        userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy user với id " + userId));

        Specification<Order> spec = Specification.where(OrderSpecs.getOrderByUserId(userId));

        Sort defaultSort = Sort.by(Sort.Order.desc("orderDate").nullsLast());
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().isSorted() ? pageable.getSort() : defaultSort

        );

        Page<Order> page = orderRepository.findAll(spec, sortedPageable);
        return page.map(this::convertToResOrderDTO);
    }

    public Page<ResOrderDTO> filterStatusOrder(OrderStatusEnum status, Pageable pageable) {
        Specification<Order> spec = Specification
                .where(OrderSpecs.hasOrderStatus(status));

        Page<Order> page = orderRepository.findAll(spec, pageable);

        return page.map(this::convertToResOrderDTO);
    }

    @Transactional
    public Order createOrder(ReqOrderDTO req) throws IdInvalidException {

        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy user với id " + req.getUserId()));

        Order order = new Order();
        order.setUser(user);
        order.setFullname(req.getFullname());
        order.setEmail(req.getEmail());
        order.setPhoneNumber(req.getPhoneNumber());
        order.setAddress(req.getAddress());
        order.setNote(req.getNote());
        order.setShippingMethod(req.getShippingMethod());
        order.setShippingAddress(req.getShippingAddress());
        order.setPaymentMethod(req.getPaymentMethod());
        order.setStatus(OrderStatusEnum.PENDING);
        order.setActive(true);

        order = orderRepository.save(order);

        float totalMoney = 0f;
        List<OrderDetail> details = new ArrayList<>();

        for (ReqOrderDTO.ReqOrderDetail d : req.getOrderDetails()) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);

            Product product = productRepository.findById(d.getProductId())
                    .orElseThrow(() -> new IdInvalidException("Không tìm thấy sản phẩm với id " + d.getProductId()));
            detail.setProduct(product);

            ProductVariant variant = productVariantRepository.findById(d.getVariantId())
                    .orElseThrow(() -> new IdInvalidException("Không tìm thấy biến thể với id " + d.getVariantId()));
            detail.setProductVariant(variant);

            Category category = categoryRepository.findById(d.getCategoryId())
                    .orElseThrow(() -> new IdInvalidException("Không tìm thấy danh mục với id " + d.getCategoryId()));
            detail.setCategory(category);

            // set thông tin giá, số lượng
            detail.setPrice(d.getPrice());
            detail.setNumberOfProducts(d.getNumberOfProducts());
            detail.setTotalMoney(d.getPrice() * d.getNumberOfProducts());

            if (variant.getStockQuantity() < d.getNumberOfProducts()) {
                throw new IdInvalidException("Sản phẩm " + product.getName() + " (" + variant.getColor() + "/"
                        + variant.getSize() + ") không đủ hàng trong kho!");
            }

            // trừ tồn kho
            variant.setStockQuantity(variant.getStockQuantity() - d.getNumberOfProducts());
            productVariantRepository.save(variant);

            // lưu OrderDetail
            orderDetailRepository.save(detail);
            details.add(detail);

            totalMoney += detail.getTotalMoney();
        }

        order.setOrderDetails(details);
        order.setTotalMoney(totalMoney);

        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrderStatus(int orderId, ReqUpdateOrderStatusDTO req) throws IdInvalidException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy đơn hàng với id " + orderId));

        OrderStatusEnum currentStatus = order.getStatus();
        OrderStatusEnum newStatus = req.getStatus();

        // Validate flow hợp lệ
        if (!isValidTransition(currentStatus, newStatus)) {
            throw new IllegalStateException("Không thể cập nhật từ " + currentStatus + " sang " + newStatus);
        }
        switch (newStatus) {
            case PENDING -> {
                order.setStatus(OrderStatusEnum.PENDING);
            }
            case PROCESSING -> {
                order.setStatus(OrderStatusEnum.PROCESSING);
            }
            case SHIPPED -> {
                order.setStatus(OrderStatusEnum.SHIPPED);
                order.setShippingDate(Instant.now());
                order.setTrackingNumber(generateTrackingNumber(order));
            }
            case DELIVERED -> {
                order.setStatus(OrderStatusEnum.DELIVERED);
            }
            case CANCELLED -> {
                order.setStatus(OrderStatusEnum.CANCELLED);
                // hoàn trả tồn kho
                for (OrderDetail detail : order.getOrderDetails()) {
                    ProductVariant variant = detail.getProductVariant();
                    if (variant != null) {
                        variant.setStockQuantity(
                                variant.getStockQuantity() + detail.getNumberOfProducts());
                        productVariantRepository.save(variant);
                    }
                }
            }
        }

        return orderRepository.save(order);
    }

    private boolean isValidTransition(OrderStatusEnum current, OrderStatusEnum next) {
        if (current == OrderStatusEnum.PENDING) {
            return next == OrderStatusEnum.PROCESSING || next == OrderStatusEnum.CANCELLED;
        }
        if (current == OrderStatusEnum.PROCESSING) {
            return next == OrderStatusEnum.SHIPPED || next == OrderStatusEnum.CANCELLED;
        }
        if (current == OrderStatusEnum.SHIPPED) {
            return next == OrderStatusEnum.DELIVERED;
        }
        if (current == OrderStatusEnum.CANCELLED) {
            return false;
        }

        return false;
    }

    private String generateTrackingNumber(Order order) {
        String prefix = "CS"; // ClothingStore
        String orderIdPart = String.valueOf(order.getId());
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .withZone(java.time.ZoneId.systemDefault())
                .format(Instant.now());
        return prefix + "-" + orderIdPart + "-" + timestamp;
    }

}
