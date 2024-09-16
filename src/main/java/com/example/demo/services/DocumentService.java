package com.example.demo.services;

import com.example.demo.entities.AlevelResult;
import com.example.demo.entities.DocumentEntity;
import com.example.demo.repositories.DocumentRepository;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;
import org.jsoup.nodes.Document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import org.jsoup.Jsoup;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Transactional
    public Long uploadDocument(MultipartFile file) throws IOException {
        // Save the document in the database
        documentRepository.saveDocument(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getBytes()
        );

        // Fetch the document ID (assuming you have a method to get the latest document ID)
        Long documentId = documentRepository.findLatestDocumentId();

        return documentId;
    }

    @Transactional
    public String getDocumentAsHtml(Long documentId) throws IOException {
        byte[] fileData = documentRepository.findDocumentById(documentId);
        if (fileData == null) {
            throw new IOException("Document not found");
        }

        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(fileData))) {
            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<html><head><style>");
            htmlBuilder.append("body { font-family: Arial, sans-serif; line-height: 1.6; margin: 20px; }");
            htmlBuilder.append("p { margin: 0 0 10px 0; }");
            htmlBuilder.append("table { border-collapse: collapse; width: 100%; margin-top: 20px; }");
            htmlBuilder.append("td, th { border: 1px solid #ddd; padding: 8px; }");
            htmlBuilder.append("</style></head><body>");

            List<Object> contentList = new ArrayList<>();
            // Add paragraphs to contentList
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                contentList.add(paragraph);
            }

            // Add tables to contentList
            for (XWPFTable table : document.getTables()) {
                contentList.add(table);
            }

            // Process and append each content element
            for (Object content : contentList) {
                if (content instanceof XWPFParagraph) {
                    XWPFParagraph paragraph = (XWPFParagraph) content;
                    StringBuilder paragraphBuilder = new StringBuilder();
                    String style = paragraph.getStyle();
                    String text = getFormattedText(paragraph);

                    // Process images within the paragraph
                    for (XWPFRun run : paragraph.getRuns()) {
                        for (XWPFPicture picture : run.getEmbeddedPictures()) {
                            XWPFPictureData pictureData = picture.getPictureData();
                            String base64Image = Base64.getEncoder().encodeToString(pictureData.getData());
                            String imgTag = "<img src=\"data:image/" + pictureData.suggestFileExtension() + ";base64," + base64Image + "\" style=\"max-width:200px;\" alt=\"Image\"/>";
                            paragraphBuilder.append("<div style=\"text-align:center;\">").append(imgTag).append("</div>");
                        }
                    }

                    // Append paragraph content
                    if (style != null) {
                        switch (style) {
                            case "Heading1":
                                paragraphBuilder.append("<h1>").append(text).append("</h1>");
                                break;
                            case "Heading2":
                                paragraphBuilder.append("<h2>").append(text).append("</h2>");
                                break;
                            case "Heading3":
                                paragraphBuilder.append("<h3>").append(text).append("</h3>");
                                break;
                            default:
                                paragraphBuilder.append("<p>").append(text.replace("\n", "<br/>")).append("</p>");
                                break;
                        }
                    } else {
                        paragraphBuilder.append("<p>").append(text.replace("\n", "<br/>")).append("</p>");
                    }

                    htmlBuilder.append(paragraphBuilder.toString());
                } else if (content instanceof XWPFTable) {
                    XWPFTable table = (XWPFTable) content;
                    StringBuilder tableBuilder = new StringBuilder();
                    tableBuilder.append("<table>");
                    for (XWPFTableRow row : table.getRows()) {
                        tableBuilder.append("<tr>");
                        for (XWPFTableCell cell : row.getTableCells()) {
                            tableBuilder.append("<td>").append(cell.getText()).append("</td>");
                        }
                        tableBuilder.append("</tr>");
                    }
                    tableBuilder.append("</table>");
                    htmlBuilder.append(tableBuilder.toString());
                }
            }

            htmlBuilder.append("</body></html>");
            return htmlBuilder.toString();
        } catch (Exception e) {
            throw new IOException("Error converting document to HTML", e);
        }
    }

    private String getFormattedText(XWPFParagraph paragraph) {
        StringBuilder textBuilder = new StringBuilder();
        for (XWPFRun run : paragraph.getRuns()) {
            if (run.isBold()) {
                textBuilder.append("<b>");
            }
            if (run.isItalic()) {
                textBuilder.append("<i>");
            }
            if (run.getUnderline() != UnderlinePatterns.NONE) {
                textBuilder.append("<u>");
            }
            if (run.getFontSize() > 0) {
                textBuilder.append("<span style=\"font-size:").append(run.getFontSize()).append("px;\">");
            }
            if (run.getColor() != null) {
                textBuilder.append("<span style=\"color:#").append(run.getColor()).append(";\">");
            }

            textBuilder.append(run.text().replace("\n", "<br/>")); // Handle line breaks

            if (run.getColor() != null) {
                textBuilder.append("</span>");
            }
            if (run.getFontSize() > 0) {
                textBuilder.append("</span>");
            }
            if (run.getUnderline() != UnderlinePatterns.NONE) {
                textBuilder.append("</u>");
            }
            if (run.isItalic()) {
                textBuilder.append("</i>");
            }
            if (run.isBold()) {
                textBuilder.append("</b>");
            }
        }
        return textBuilder.toString();
    }




    @Transactional
    public void updateDocumentContent (Long documentId, String htmlContent) throws IOException {
        byte[] fileData = documentRepository.findDocumentById(documentId);

        if (fileData == null) {
            throw new IOException("Document not found");
        }

        try (XWPFDocument wordDocument = new XWPFDocument(new ByteArrayInputStream(fileData))) {
            // Clear existing paragraphs
            List<XWPFParagraph> paragraphs = wordDocument.getParagraphs();
            for (int i = paragraphs.size() - 1; i >= 0; i--) {
                wordDocument.removeBodyElement(wordDocument.getPosOfParagraph(paragraphs.get(i)));
            }

            // Parse HTML content using Jsoup
            Document htmlDoc = Jsoup.parse(htmlContent);
            Elements bodyElements = htmlDoc.body().children();

            // Traverse and map HTML tags to Word elements
            for (Element element : bodyElements) {
                if (element.tagName().equals("p")) {
                    XWPFParagraph paragraph = wordDocument.createParagraph();
                    XWPFRun run = paragraph.createRun();
                    run.setText(element.text());
                } else if (element.tagName().equals("b")) {
                    XWPFParagraph paragraph = wordDocument.createParagraph();
                    XWPFRun run = paragraph.createRun();
                    run.setBold(true);
                    run.setText(element.text());
                } else if (element.tagName().equals("i")) {
                    XWPFParagraph paragraph = wordDocument.createParagraph();
                    XWPFRun run = paragraph.createRun();
                    run.setItalic(true);
                    run.setText(element.text());
                } else if (element.tagName().equals("u")) {
                    XWPFParagraph paragraph = wordDocument.createParagraph();
                    XWPFRun run = paragraph.createRun();
                    run.setUnderline(UnderlinePatterns.SINGLE);
                    run.setText(element.text());
                } else if (element.tagName().equals("img")) {
                    String base64Image = element.attr("src").split(",")[1]; // Get the base64 image data
                    byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                    int imageFormat = XWPFDocument.PICTURE_TYPE_JPEG; // Can change depending on img type
                    wordDocument.addPictureData(imageBytes, imageFormat);
                } else if (element.tagName().equals("ul") || element.tagName().equals("ol")) {
                    for (Element li : element.getElementsByTag("li")) {
                        XWPFParagraph paragraph = wordDocument.createParagraph();
                        XWPFRun run = paragraph.createRun();
                        run.setText("• " + li.text()); // Assuming a bullet point list
                    }
                }
                // Add other mappings (tables, headings) as necessary
            }

            // Save updated content back to the repository
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            wordDocument.write(outputStream);
            documentRepository.updateDocumentContent(documentId, outputStream.toByteArray());
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public Long getLatestDocumentId() {
        return documentRepository.findLatestDocumentId();
    }

    public ResponseEntity<byte[]> downloadWordDocument(Long documentId) {
        try {
            // Fetch the document entity from the database
            DocumentEntity document = documentRepository.downloadDocumentById(documentId);

            // Convert the byte array (fileData) into an Apache POI Word document (optional)
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(document.getFileData());
                 XWPFDocument wordDocument = new XWPFDocument(inputStream);
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                // If you're making modifications to the document, they would go here.
                // Example: wordDocument.getParagraphs(), etc.

                // Write the document to an output stream (optional step)
                wordDocument.write(outputStream);

                // Prepare the response with the file for download
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(document.getContentType()));
                headers.setContentDispositionFormData("attachment", document.getName());

                // Return the modified document or just the original fileData
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(outputStream.toByteArray()); // Send the modified/streamed Word document
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing the Word document for download", e);
        }
    }

    @Transactional
    public byte[] getDocumentAsWord(Long documentId) throws IOException {
        byte[] fileData = documentRepository.findDocumentById(documentId);

        if (fileData == null) {
            throw new IOException("Document not found");
        }

        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(fileData))) {
            // Optionally, you can modify the document if needed here

            // Convert XWPFDocument to a byte array to send it as a response
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.write(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }

    @Transactional
    public byte[] getAllDocumentAsWord(Long documentId) throws IOException {
        byte[] fileData = documentRepository.findAllDocuments(documentId);

        if (fileData == null) {
            throw new IOException("Document not found");
        }

        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(fileData))) {
            // Optionally, you can modify the document if needed here

            // Convert XWPFDocument to a byte array to send it as a response
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.write(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }
}

