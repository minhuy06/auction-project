package com.auction.auctionbackend.service;

import com.auction.auctionbackend.dto.request.CategoryRequest;
import com.auction.auctionbackend.dto.response.CategoryResponse;
import com.auction.auctionbackend.entity.Category;
import com.auction.auctionbackend.exception.AppException;
import com.auction.auctionbackend.exception.ErrorCode;
import com.auction.auctionbackend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryTree(){
        return categoryRepository.findByParentIsNull()
                .stream()
                .map(CategoryResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        return CategoryResponse.fromEntity(category);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request){
        if(categoryRepository.existsByName(request.name().trim())){
            throw new AppException(ErrorCode.CATEGORY_NAME_EXISTS);
        }

        Category parent = null;
        if(request.parentId() != null){
            parent = categoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        }

        Category category = Category.builder()
                .name(request.name().trim())
                .description(request.description())
                .parent(parent)
                .build();

        return CategoryResponse.fromEntity(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        String normalizedName = request.name().trim();
        if(!category.getName().equalsIgnoreCase(normalizedName) && categoryRepository.existsByName(normalizedName)){
            throw new AppException(ErrorCode.CATEGORY_NAME_EXISTS);
        }

        Category parent = null;
        if(request.parentId() != null){
            if(request.parentId().equals(id)){
                throw new AppException(ErrorCode.INVALID_CATEGORY_PARENT);
            }
            parent = categoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

            // Kiểm tra chống chu trình (parent mới không được là con/cháu của category hiện tại)
            Category currentAncestor = parent.getParent();
            while(currentAncestor != null){
                if(currentAncestor.getId().equals(id)){
                    throw new AppException(ErrorCode.INVALID_CATEGORY_PARENT);
                }
                currentAncestor = currentAncestor.getParent();
            }
        }

        category.setName(request.name().trim());
        category.setDescription(request.description());
        category.setParent(parent);

        return CategoryResponse.fromEntity(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        categoryRepository.delete(category);
    }
}
