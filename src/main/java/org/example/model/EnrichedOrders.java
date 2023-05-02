package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrichedOrders {
    private Integer orderId;
    private Date orderDate;
    private String customerName;
    private BigDecimal price;
    private Integer productId;
    private Boolean orderStatus;
    private String productName;
    private String productDescription;
    private Integer shipmentId;
    private String origin;
    private String destination;
    private Boolean isArrived;
}
