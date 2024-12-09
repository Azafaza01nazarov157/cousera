package org.example.cursera.util;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

@Slf4j
@Service
public class CertificateGenerator {

    public byte[] generateHorizontalCertificate(String userName, String userEmail, String courseName, String courseCreator) {
        String formattedUserName = capitalizeFully(userName);
        String formattedEmail = userEmail.toLowerCase();

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(PageSize.A4.rotate());
            Document document = new Document(pdfDoc);

            InputStream backgroundStream = getClass().getResourceAsStream("/templates/certificate-template.png");
            if (backgroundStream == null) {
                throw new IllegalStateException("Background image not found in resources/templates");
            }

            Image background = new Image(ImageDataFactory.create(backgroundStream.readAllBytes()));
            background.setFixedPosition(0, 0);
            background.setWidth(pdfDoc.getDefaultPageSize().getWidth());
            background.setHeight(pdfDoc.getDefaultPageSize().getHeight());
            document.add(background);

            Paragraph title = new Paragraph("Certificate of Achievement")
                    .setFont(PdfFontFactory.createFont("fonts/GreatVibes-Regular.ttf"))
                    .setFontSize(67)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(30);
            document.add(title);

            Paragraph recipient = new Paragraph(formattedUserName)
                    .setFont(PdfFontFactory.createFont("fonts/GreatVibes-Regular.ttf"))
                    .setFontSize(30)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20);
            document.add(recipient);

            Paragraph description = new Paragraph("In recognition of outstanding dedication and accomplishment in successfully \n completing the course " + courseName)
                    .setFont(PdfFontFactory.createFont("fonts/Roboto-Regular.ttf"))
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(10);
            document.add(description);

            Paragraph email = new Paragraph(formattedEmail)
                    .setFont(PdfFontFactory.createFont("fonts/Roboto-Regular.ttf"))
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20);
            document.add(email);

            Paragraph leftSignature = new Paragraph("Head of Event\n" + courseName)
                    .setFont(PdfFontFactory.createFont("fonts/Roboto-Regular.ttf"))
                    .setFontSize(15)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFixedPosition(160, 100, 230);
            document.add(leftSignature);

            Paragraph rightSignature = new Paragraph("Mentor\n" + courseCreator)
                    .setFont(PdfFontFactory.createFont("fonts/Roboto-Regular.ttf"))
                    .setFontSize(15)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFixedPosition(450, 100, 230);
            document.add(rightSignature);

            document.close();
            log.info("Certificate successfully generated for user '{}'", userName);

            return byteArrayOutputStream.toByteArray();

        } catch (Exception e) {
            log.error("Error generating certificate for user '{}': {}", userName, e.getMessage());
            throw new RuntimeException("Failed to generate certificate", e);
        }
    }

    private String capitalizeFully(String str) {
        String[] words = str.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return result.toString().trim();
    }

    public byte[] loadExistingCertificate(String userName) {
        try {
            String formattedUserName = userName.replace(" ", "_");
            String path = "/path/to/certificates/" + formattedUserName + "_certificate.pdf";
            File file = new File(path);
            if (!file.exists()) {
                log.warn("No existing certificate found at {}", path);
                return null;
            }
            return Files.readAllBytes(file.toPath());
        } catch (Exception e) {
            log.error("Failed to load existing certificate for user '{}': {}", userName, e.getMessage());
            return null;
        }
    }

}
