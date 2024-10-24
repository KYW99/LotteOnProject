package com.lotteon.repository.custom;

import com.lotteon.dto.product.PageRequestDTO;
import com.lotteon.dto.product.ProductWithDTO;
import com.lotteon.entity.product.Product;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.QPageRequest;


public interface ProductRepositoryCustom {
    //admin List
    public Page<Tuple> selectProductBySellerIdForList(String sellerId, PageRequestDTO pageRequest,Pageable pageable );

    //main list
    public Page<Tuple> selectProductForList( PageRequestDTO pageRequest,Pageable pageable );
    public Page<ProductWithDTO> selectProductForListByCategory(PageRequestDTO pageRequest, Pageable pageable );




}
