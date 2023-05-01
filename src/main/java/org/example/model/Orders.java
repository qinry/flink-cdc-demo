package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Orders {
    private Long orderId;
    private LocalDateTime orderDate;
    private String customerName;
    private BigDecimal price;
    private Long productId;
    private Integer orderStatus;
}
