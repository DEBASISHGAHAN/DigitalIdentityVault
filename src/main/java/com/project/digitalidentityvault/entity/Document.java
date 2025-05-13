package com.project.digitalidentityvault.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Setter
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 4)
    private String type;
    @Column(name = "file_path", length = 50)
    private String filePath;
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        return builder.append("Document [id=").append(id).append(", type=").append(type)
                .append(", filePath=").append(filePath).append(", uploadedAt=").append(uploadedAt).toString();
    }
}

