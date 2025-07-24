package com.cedacri.emailnotification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "processed_events")
@NoArgsConstructor
@Data
public class ProcessedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String messageId;

    @Column(nullable = false, unique = true)
    private String productId;

    public ProcessedEvent(String productId, String messageId) {
        this.productId = productId;
        this.messageId = messageId;
    }
}
