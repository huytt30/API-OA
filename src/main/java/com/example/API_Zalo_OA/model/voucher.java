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

    // Đảm bảo ánh xạ đúng kiểu enum (STRING hoặc ORDINAL)
    private String status;

    public voucher() {

    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public voucher(Long id, String code, String status) {
        this.id = id;
        this.code = code;
        this.status = status;
    }
}
