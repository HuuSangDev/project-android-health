package com.SelfCare.SelftCare.Entity;

import com.SelfCare.SelftCare.Enum.Gender;
import com.SelfCare.SelftCare.Enum.Goal;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfile {

    @Id
     Long id; //phai trung voi userid
     String avatarUrl;
     LocalDate dateOfBirth;
     Gender gender;
     Double height;
     Double weight;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Goal healthGoal;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    User user;

}
