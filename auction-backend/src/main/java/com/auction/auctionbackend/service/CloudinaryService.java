package com.auction.auctionbackend.service;

import com.auction.auctionbackend.exception.AppException;
import com.auction.auctionbackend.exception.ErrorCode;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file){
        if(file == null || file.isEmpty()){
            throw new AppException(ErrorCode.INVALID_FILE_TYPE, "File ảnh không được để trống");
        }

        String contentType = file.getContentType();
        if(contentType == null || !contentType.startsWith("images/")){
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "auction_products")
            );
            return uploadResult.get("secure_url").toString();
        } catch (IOException ex){
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    public List<String> uploadImages(List<MultipartFile> files){
        if(files == null || files.isEmpty()){
            return List.of();
        }

        List<String> imageUrls = new ArrayList<>();
        for(MultipartFile file : files){
            if(!file.isEmpty()){
                imageUrls.add(uploadImage(file));
            }
        }
        return imageUrls;
    }
}
