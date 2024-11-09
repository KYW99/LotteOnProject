package com.lotteon.entity.order;

import com.lotteon.dto.order.DeliveryStatus;
import com.lotteon.dto.product.ProductDTO;
import com.lotteon.entity.User.Seller;
import com.lotteon.entity.product.Product;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@Builder
@Entity
@Table(name="orderItem")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    @ToString.Exclude
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id")
    @ToString.Exclude
    private Product product;
    private long savedPrice;   //주문시 제품 가격
    private long orderPrice;   //결제 가격(할인적용)
    private long savedDiscount;  //할인적용내용(할인금액)
    private long point;
    private String sellerUid;

    private String optionDesc;
    private long optionId;
    private String combination; // 콤비네이션 내용
    private long combinationId; //
    private long stock;
    private long price;
    private String traceNumber;  // 배송넘버
    private long shippingFees;

    private String customerId;  //  구매자
    private String customerName; //구매자이름


    @Builder.Default
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status = DeliveryStatus.PREPARING;  // 배송상태
}
