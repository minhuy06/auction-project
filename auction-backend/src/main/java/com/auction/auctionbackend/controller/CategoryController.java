package com.auction.auctionbackend.controller;

import com.auction.auctionbackend.dto.request.CategoryRequest;
import com.auction.auctionbackend.dto.response.ApiResponse;
import com.auction.auctionbackend.dto.response.CategoryResponse;
import com.auction.auctionbackend.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategoryTree(){
        return ResponseEntity.ok(ApiResponse.success(categoryService.getCategoryTree(), "Lấy danh sách thành công"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id){
        return  ResponseEntity.ok(ApiResponse.success(categoryService.getCategoryById(id), "Lấy chi tiết danh mục thành công"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(categoryService.createCategory(request), "Tạo danh mục thành công"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request){
        return ResponseEntity.ok(ApiResponse.success(categoryService.updateCategory(id, request), "Cập nhật danh mục thành công"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa danh mục thành công"));
    }
}
