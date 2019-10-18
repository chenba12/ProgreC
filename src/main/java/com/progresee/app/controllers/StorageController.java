package com.progresee.app.controllers;

import java.net.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.progresee.app.services.StorageServiceImpl;

@RestController
@RequestMapping("/storage")
public class StorageController {
	
	private StorageServiceImpl storageService;

    @Autowired
    StorageController(StorageServiceImpl amazonClient) {
        this.storageService = amazonClient;
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestPart(value = "file") MultipartFile file) {
        return storageService.uploadFile(file);
    }

    @DeleteMapping("/deleteFile")
    public String deleteFile(@RequestPart(value = "url") String fileUrl) {
        return storageService.deleteFileFromS3Bucket(fileUrl);
    }
    
    @GetMapping("/getFile")
    public URL getFile(@RequestPart("url") String fileUrl) {
    	return storageService.getFile(fileUrl);
    }

}
