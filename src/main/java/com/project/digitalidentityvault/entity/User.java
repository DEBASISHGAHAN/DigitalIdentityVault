package com.project.digitalidentityvault.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Setter
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 30)
    private String email;
    @Column(length = 30)
    private String password;
    private Boolean verified;
    private LocalDateTime lastActiveAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Document> documents = new HashSet<>();

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        return builder.append("User [id=").append(id).append(", email=").append(email)
                .append(", password=").append(password).append(", verified=").append(verified)
                .append(", lastActiveAt=").append(lastActiveAt).toString();
    }
}

