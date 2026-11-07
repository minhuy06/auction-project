package com.auction.auctionbackend.service;

import com.auction.auctionbackend.dto.request.ProductRequest;
import com.auction.auctionbackend.dto.response.ProductResponse;
import com.auction.auctionbackend.entity.Category;
import com.auction.auctionbackend.entity.Product;
import com.auction.auctionbackend.entity.User;
import com.auction.auctionbackend.exception.AppException;
import com.auction.auctionbackend.exception.ErrorCode;
import com.auction.auctionbackend.repository.CategoryRepository;
import com.auction.auctionbackend.repository.ProductRepository;
import com.auction.auctionbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public ProductResponse createProduct(String username, ProductRequest request, List<MultipartFile> images){
        User seller = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        List<String> imageUrls = cloudinaryService.uploadImages(images);

        Product product = Product.builder()
                .seller(seller)
                .category(category)
                .name(request.name().trim())
                .description(request.description())
                .imageUrls(imageUrls)
                .build();

        Product savedProduct = productRepository.save(product);
        return ProductResponse.fromEntity(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(String username, Long productId, ProductRequest request, List<MultipartFile> images) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!product.getSeller().getUsername().equals(username)) {
            throw new AppException(ErrorCode.NOT_PRODUCT_OWNER);
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setCategory(category);

        if (images != null && !images.isEmpty()) {
            List<String> newImageUrls = cloudinaryService.uploadImages(images);
            if (!newImageUrls.isEmpty()) {
                product.setImageUrls(newImageUrls);
            }
        }

        Product updatedProduct = productRepository.save(product);
        return ProductResponse.fromEntity(updatedProduct);
    }

    @Transactional
    public void deleteProduct(String username, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!product.getSeller().getUsername().equals(username)) {
            throw new AppException(ErrorCode.NOT_PRODUCT_OWNER);
        }

        try {
            productRepository.delete(product);
            productRepository.flush();
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new AppException(ErrorCode.PRODUCT_IN_AUCTION);
        }
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getMyProducts(String username, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findBySellerUsernameOrderByCreatedAtDesc(username, pageable).map(ProductResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable).map(ProductResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return ProductResponse.fromEntity(product);
    }
}
