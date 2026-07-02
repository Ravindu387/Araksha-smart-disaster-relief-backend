package org.example.arakshasmartdisasterreliefbackend.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadFileResponse {
    private Long id;
    private String originalFileName;
    private String storedFileName;
    private String fileType;
    private long fileSize;
    private String fileUrl;
}
