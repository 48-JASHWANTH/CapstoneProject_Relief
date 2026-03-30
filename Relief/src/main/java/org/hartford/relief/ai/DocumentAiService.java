package org.hartford.relief.ai;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class DocumentAiService {

    private final ChatClient chatClient;

    public DocumentAiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem("You are an expert claims officer assistant for 'Relief', a disaster insurance company. " +
                        "Analyse the following document text (which may be a police report, repair estimate, or supporting evidence). " +
                        "Extract and summarize: 1. Main cause of damage. 2. Severity. 3. Estimated repair cost or quoted amount. " +
                        "Keep the response to a single concise paragraph (under 50 words). If the document is irrelevant or missing values, state that.")
                .build();
    }

    public String analyzeDocument(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "Unavailable: No document provided.";
        }
        
        // Only attempt to read PDF files
        if (!file.getOriginalFilename().toLowerCase().endsWith(".pdf") && 
            !"application/pdf".equals(file.getContentType())) {
            return "Analysis pending: Groq AI Document Scanner only supports PDF format at this time.";
        }

        try (PDDocument pdDocument = org.apache.pdfbox.Loader.loadPDF(file.getBytes())) {

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(pdDocument);
            
            if (text == null || text.trim().isEmpty()) {
                return "Unavailable: The PDF does not contain extractable text (it might be a scanned image without OCR).";
            }

            // Truncate text to avoid exceeding token limits if the PDF is exceptionally large
            if (text.length() > 10000) {
                text = text.substring(0, 10000);
            }

            String summary = chatClient.prompt()
                    .user("Analyze this document:\n" + text)
                    .call()
                    .content();

            log.info("[DocumentAiService] Summarized document successfully. Summary: {}", summary);
            return summary;

        } catch (Exception e) {
            log.error("[DocumentAiService] Failed to parse or summarize PDF.", e);
            return "Unavailable: Failed to parse or summarize PDF document due to an error.";
        }
    }
}
