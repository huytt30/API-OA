package com.example.API_Zalo_OA.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name="vocher")
public class voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @Enumerated(EnumType.STRING)  // Đảm bảo ánh xạ đúng kiểu enum (STRING hoặc ORDINAL)
    private EStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public EStatus getStatus() {
        return status;
    }

    public void setStatus(EStatus status) {
        this.status = status;
    }

    public voucher(Long id, String code, EStatus status) {
        this.id = id;
        this.code = code;
        this.status = status;
    }

    public voucher() {
    }
}
