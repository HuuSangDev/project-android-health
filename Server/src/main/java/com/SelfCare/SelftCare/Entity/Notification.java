//package com.SelfCare.SelftCare.Entity;
//import jakarta.persistence.*;
//import lombok.*;
//import lombok.experimental.FieldDefaults;
//
//import java.time.LocalDateTime;
//import java.util.List;
//@Entity
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@FieldDefaults(level = AccessLevel.PRIVATE)
//public class Notification {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//     Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//     User user;
//
//     String title;
//     String message;
//
//     boolean isRead = false;
//
//     LocalDateTime createdAt = LocalDateTime.now();
//
//}
