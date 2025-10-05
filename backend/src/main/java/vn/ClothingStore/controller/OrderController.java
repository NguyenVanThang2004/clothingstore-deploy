package vn.ClothingStore.controller;

import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.ClothingStore.domain.Order;
import vn.ClothingStore.domain.Product;
import vn.ClothingStore.util.constant.OrderStatusEnum;

import vn.ClothingStore.domain.request.order.ReqOrderDTO;
import vn.ClothingStore.domain.request.order.ReqUpdateOrderStatusDTO;
import vn.ClothingStore.domain.response.ResultPaginationDTO;
import vn.ClothingStore.domain.response.order.ResOrderDTO;
import vn.ClothingStore.service.OrderService;
import vn.ClothingStore.util.annotation.ApiMessage;
import vn.ClothingStore.util.error.IdInvalidException;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/orders")
    @ApiMessage("get orders success")
    public ResponseEntity<ResultPaginationDTO> getAllOrder(
            @Filter Specification<Order> spec,
            @PageableDefault(size = 10, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(this.orderService.fetchAllOrder(spec, pageable));
    }

    @GetMapping("/orders/{userId}")
    @ApiMessage("get orders success")
    public Page<ResOrderDTO> getAllOrderByUserId(
            @PathVariable int userId,
            @PageableDefault(size = 10, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable)
            throws IdInvalidException {
        return orderService.fetchAllOrderByUserId(userId, pageable);
    }

    @GetMapping("/orders/filter")
    public Page<ResOrderDTO> filterStatusOrder(
            @RequestParam(required = false) OrderStatusEnum status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return orderService.filterStatusOrder(status, pageable);
    }

    @PostMapping("/orders")
    @ApiMessage("create order success")
    ResponseEntity<ResOrderDTO> createOrder(@RequestBody ReqOrderDTO req) throws IdInvalidException {
        Order order = this.orderService.createOrder(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.orderService.convertToResOrderDTO(order));
    }

    @PutMapping("/orders/{id}")
    @ApiMessage("update order success")
    public ResponseEntity<ResOrderDTO> updateOrderStatus(@PathVariable int id,
            @RequestBody ReqUpdateOrderStatusDTO req) throws IdInvalidException {
        Order order = this.orderService.updateOrderStatus(id, req);
        return ResponseEntity.ok().body(this.orderService.convertToResOrderDTO(order));
    }

}
