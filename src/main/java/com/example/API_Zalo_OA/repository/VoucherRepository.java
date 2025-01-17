package com.example.API_Zalo_OA.repository;


import com.example.API_Zalo_OA.model.voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoucherRepository extends JpaRepository<voucher, Long> {

    // Truy vấn với trạng thái là String (không dùng Enum nữa)
    List<voucher> findByStatus(String status);
}