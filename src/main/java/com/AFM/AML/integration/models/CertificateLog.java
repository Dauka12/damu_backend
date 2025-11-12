package com.AFM.AML.integration.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "certificate_log")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CertificateLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer userId;
    private Integer courseId;

    private String certificateNumber;
    private String status; // SENT / FAILED / PENDING
    private String responseJson;

    private LocalDateTime sendTime;
}
