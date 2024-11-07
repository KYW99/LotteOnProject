package com.lotteon.repository.user;

import com.lotteon.entity.User.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findById(long id);
    Optional<Seller> findByUserUid(String userUid); // UserUid로 조회

    void deleteAllByIdIn(List<Long> sellerIds);

}

