package com.codeshield.controllers;

import com.codeshield.core.Language;
import com.codeshield.core.LanguageDetector;
import com.codeshield.models.AnalyseRequest;
import com.codeshield.models.FileResult;
import com.codeshield.models.ScanResult;
import com.codeshield.services.JavaComplexityAnalyser;
import com.codeshield.services.ScanService;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "http://localhost:3000") // allows the react app to talk to this backend
public class ComplexityController {

    private final ScanService scanService;

    // spring injects service here
    public ComplexityController() {
        this.scanService = new ScanService();
    }

    @PostMapping("/analyze")
    public ScanResult analyze(@RequestBody AnalyseRequest request) {
        String fileName = request.fileName();
        String sourceCode = request.sourceCode();

        if (sourceCode == null || sourceCode.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Source code cannot be empty");
        }

        Language language = LanguageDetector.detectLanguage(fileName, sourceCode);

        if (language != Language.JAVA) {
            throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "Only Java files are supported currently");
        }

        try {
            ScanResult result = scanService.scan(sourceCode);
            result.setFileName(fileName);
            return result;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error analyzing code: " + e.getMessage(), e);
        }
    }
}
