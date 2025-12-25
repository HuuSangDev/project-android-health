package com.SelfCare.SelftCare.Entity;

import com.SelfCare.SelftCare.Enum.Platform;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "device_tokens", 
       uniqueConstraints = @UniqueConstraint(columnNames = "token"))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(nullable = false, unique = true, length = 500)
    String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Platform platform;

    @Column(nullable = false)
    @Builder.Default
    Boolean isActive = true;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;
}
