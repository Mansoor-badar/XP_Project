package com.codeshield.controllers;

import com.codeshield.models.AnalyseRequest;
import com.codeshield.models.FileResult;
import com.codeshield.services.JavaComplexityAnalyser;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "http://localhost:3000") // allows the react app to talk to this backend
public class ComplexityController {

    private final JavaComplexityAnalyser analyser;

    // spring injects service here
    public ComplexityController(JavaComplexityAnalyser analyser) {
        this.analyser = analyser;
    }

    @PostMapping("/analyse")
    public FileResult analyse(@RequestBody AnalyseRequest request) {
        // the controller calls service layer
        return analyser.analyseFile(request.sourceCode());
    }
}
