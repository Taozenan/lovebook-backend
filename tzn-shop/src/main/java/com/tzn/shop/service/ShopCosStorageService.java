package com.tzn.shop.service;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ShopCosStorageService {
  private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
  private final String bucket;
  private final COSClient cosClient;
  private final boolean enabled;

  public ShopCosStorageService(
      @Value("${shop.cos.enabled:false}") boolean enabled,
      @Value("${shop.cos.secret-id:}") String secretId,
      @Value("${shop.cos.secret-key:}") String secretKey,
      @Value("${shop.cos.region:ap-shanghai}") String region,
      @Value("${shop.cos.bucket:}") String bucket) {
    this.bucket = bucket;
    this.enabled = enabled && hasText(secretId) && hasText(secretKey) && hasText(bucket);
    if (this.enabled) {
      COSCredentials credentials = new BasicCOSCredentials(secretId, secretKey);
      ClientConfig clientConfig = new ClientConfig(new Region(region));
      this.cosClient = new com.qcloud.cos.COSClient(credentials, clientConfig);
    } else {
      this.cosClient = null;
    }
  }

  public boolean isEnabled() {
    return enabled;
  }

  public String upload(long userId, MultipartFile file) {
    validate(file);
    String key =
        "images/" + userId + "/" + UUID.randomUUID().toString().replace("-", "") + extension(file);
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(file.getSize());
    metadata.setContentType(file.getContentType());
    try (InputStream inputStream = file.getInputStream()) {
      PutObjectRequest request = new PutObjectRequest(bucket, key, inputStream, metadata);
      PutObjectResult ignored = cosClient.putObject(request);
      return stableUrl(key);
    } catch (IOException exception) {
      throw new IllegalArgumentException("图片上传失败");
    }
  }

  public Resource getObject(String key) {
    if (!enabled || !hasText(key) || key.contains("..")) {
      return null;
    }
    try {
      return new InputStreamResource(cosClient.getObject(bucket, key).getObjectContent());
    } catch (RuntimeException exception) {
      return null;
    }
  }

  public String contentType(String key) {
    if (!enabled || !hasText(key)) {
      return null;
    }
    try {
      ObjectMetadata metadata = cosClient.getObjectMetadata(bucket, key);
      return metadata.getContentType();
    } catch (RuntimeException exception) {
      return null;
    }
  }

  private String stableUrl(String key) {
    return "/api/shop/upload/object?key="
        + java.net.URLEncoder.encode(key, java.nio.charset.StandardCharsets.UTF_8);
  }

  private void validate(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("请选择图片");
    }
    if (file.getSize() > MAX_FILE_SIZE) {
      throw new IllegalArgumentException("图片不能超过5MB");
    }
    String contentType =
        file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
    if (!contentType.startsWith("image/")) {
      throw new IllegalArgumentException("只能上传图片文件");
    }
  }

  private String extension(MultipartFile file) {
    String name = file.getOriginalFilename();
    if (name != null && name.lastIndexOf('.') >= 0) {
      String extension = name.substring(name.lastIndexOf('.')).toLowerCase(Locale.ROOT);
      if (extension.matches("\\.(jpg|jpeg|png|gif|webp|bmp)")) {
        return extension;
      }
    }
    return ".jpg";
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }
}
