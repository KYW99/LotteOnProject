package com.lotteon.repository.product;
/*
    최영진:      Optional<Product> findById(long productId);추가

*/
import com.lotteon.entity.product.Product;
import com.lotteon.entity.product.ProductCategory;
import com.lotteon.entity.product.QProductCategory;
import com.lotteon.repository.custom.ProductRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> , ProductRepositoryCustom {
//    QProductCategory productCategory = QProductCategory.productCategory;


    Page<Product> findBySellerId(String sellerId, Pageable pageable);

    List<Product> findAllBySellerId(String sellerId); // 페이지 없이 모든 상품 반환

    public Optional<Product> findByProductId(Long productId);

    Optional<Product> findById(long productId);
    Page<Product> findByProductNameContaining(String keyword,Pageable pageable);
    Page<Product> findByProductNameContainingAndSellerId(String keyword,String sellerUid,Pageable pageable);
    Page<Product> findByProductCodeContaining(String keyword,Pageable pageable);
    Page<Product> findByProductCodeContainingAndSellerId(String keyword,String sellerUid,Pageable pageable);
    Page<Product> findBySellerIdContaining(String keyword,Pageable pageable);
    Page<Product> findBySellerIdContainingAndSellerId(String keyword,String sellerUid,Pageable pageable);
    Page<Product> findByProductDetailsContaining(String keyword,Pageable pageable);
    Page<Product> findByProductDetailsContainingAndSellerId(String keyword,String sellerUid,Pageable pageable);


    @Query("SELECT p FROM Product p JOIN ProductCategory c " +
            "WHERE c = :category OR " +
            "c.parent = :category OR " +
            "c.parent.parent = :category")
    List<Product> findAllProductsByCategoryOrParents(@Param("category") ProductCategory category);


    @Modifying
    @Query("UPDATE Product p SET p.stock = :stock WHERE p.productId = :productId")
    void updateProductQuantity(@Param("stock") long stock, @Param("productId") long productId);

//    @EntityGraph(attributePaths = {
//            "productDetails",
//            "optionGroups.optionItems",
//            "optionCombinations",
//            "files",
//            "reviews"
//    })
//    Optional<Product> findByProductId(Long productId);

//    //    int deleteAllByProductIdIn(List<Long> productIds);
//    @Query("SELECT p FROM Product p " +
//            "LEFT JOIN FETCH p.optionGroups og " +                 // Join optionGroups
//            "LEFT JOIN FETCH og.optionItems oi " +                 // Join optionItems within each optionGroup
//            "LEFT JOIN FETCH p.optionCombinations " +              // Join optionCombinations
//            "WHERE p.productId = :productId")
//    Product findProductWithOptions(@Param("productId") Long productId);

}
