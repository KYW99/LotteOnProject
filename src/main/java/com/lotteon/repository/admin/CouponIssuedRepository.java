package com.lotteon.repository.admin;

import com.lotteon.entity.User.Member;
import com.lotteon.entity.admin.Coupon;
import com.lotteon.entity.admin.CouponIssued;
import com.lotteon.repository.custom.CouponRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponIssuedRepository extends JpaRepository<CouponIssued, String>{

    Page<CouponIssued> findBySellerCompany(String sellerCompany, Pageable pageable);

    List<CouponIssued> findByMemberId(Long memberId);
}
