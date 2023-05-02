package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Shipments {
    private Integer shipmentId;
    private Integer orderId;
    private String origin;
    private String destination;
    private Boolean isArrived;
}
