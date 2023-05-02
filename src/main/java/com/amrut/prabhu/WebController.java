package com.amrut.prabhu;


import io.awspring.cloud.s3.S3Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@RestController
public class WebController {

    @Value("s3://mybucket/samplefile.txt")
    private Resource s3SampleFile;

    @GetMapping("/data")
    public ResponseEntity<String> getData() {

        try {
            return ResponseEntity.ok(s3SampleFile.getContentAsString(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Could not fetch content");
        }
    }

    @PostMapping("/data")
    public ResponseEntity<String> putData(@RequestBody String data) throws IOException {

        try (OutputStream outputStream = ((S3Resource) s3SampleFile).getOutputStream()) {
            outputStream.write(data.getBytes(StandardCharsets.UTF_8));
        }
        return ResponseEntity.ok(data);
    }
}
