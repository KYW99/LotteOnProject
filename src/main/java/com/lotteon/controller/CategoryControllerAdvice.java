package com.lotteon.controller;


import com.lotteon.dto.product.ProductCategoryDTO;
import com.lotteon.entity.product.ProductCategory;
import com.lotteon.service.product.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;


@Log4j2
@RequiredArgsConstructor
@ControllerAdvice
public class CategoryControllerAdvice {


   private final ProductCategoryService productCategoryService;
    private final ModelMapper modelMapper;

    @Cacheable(value = "categories")
    @ModelAttribute("categories")
    public List<ProductCategoryDTO> populateCategories() {
        log.info("Fetching category data from the database...");
        List<ProductCategory> categories = productCategoryService.getCategoryHierarchy();
        List<ProductCategoryDTO> categoryDTOs = new ArrayList<>();
        categories.forEach(category -> categoryDTOs.add(convertToDto(category)));
        return categoryDTOs;
    }

    @CacheEvict(value = "categories", allEntries = true)
    public void updateCategoryData() {
        // Logic to update category data
    }

    private ProductCategoryDTO convertToDto(ProductCategory category) {
        ProductCategoryDTO categoryDTO = modelMapper.map(category, ProductCategoryDTO.class);
        // 재귀적으로 children도 매핑
        if (category.getChildren() != null) {
            List<ProductCategoryDTO> childrenDTOs = new ArrayList<>();
            for (ProductCategory child : category.getChildren()) {
                childrenDTOs.add(convertToDto(child));
            }
            categoryDTO.setChildren(childrenDTOs);
        }
        return categoryDTO;
    }

}
