package com.example.demo.services;

import com.google.api.client.http.FileContent;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.model.Document;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

@Service
public class GoogleService {

    @Autowired
    private Drive driveService;

    @Autowired
    private Docs docsService;

    public String uploadFile(String fileObject) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName("example.docx");
        fileMetadata.setMimeType("application/vnd.google-apps.document");

        java.io.File filePath = new java.io.File(fileObject);
        FileContent mediaContent = new FileContent("application/vnd.openxmlformats-officedocument.wordprocessingml.document", filePath);

        File file = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();

        return file.getId();
    }

    public String readDocument(String documentId) throws IOException {
        Document document = docsService.documents().get(documentId).execute();
        return document.getBody().getContent().toString(); // Adjust based on your needs
    }
}

