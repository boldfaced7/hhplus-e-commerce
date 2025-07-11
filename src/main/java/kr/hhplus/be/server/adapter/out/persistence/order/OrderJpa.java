package kr.hhplus.be.server.adapter.out.persistence.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderJpa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private UUID uuid;
    private Long userId;
    private Long couponId;
    private Long originalTotalPrice;
    private Long discountTotalPrice;
    private boolean orderFinished;
    private boolean orderSucceeded;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
