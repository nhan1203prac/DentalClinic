package com.dentalclinic.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// Frontend tải ảnh lên Cloudinary rồi gửi link; backend chỉ nhận link thuộc đúng cloud của phòng khám
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.cloudinary")
public class CloudinaryProperties {
    private String cloudName = "";

    // Link hợp lệ: https://res.cloudinary.com/<cloud-name>/...
    public boolean isValidImageUrl(String url) {
        if (cloudName == null || cloudName.isBlank()) {
            return false;
        }
        return url.startsWith("https://res.cloudinary.com/" + cloudName + "/") && url.length() <= 255;
    }
}
