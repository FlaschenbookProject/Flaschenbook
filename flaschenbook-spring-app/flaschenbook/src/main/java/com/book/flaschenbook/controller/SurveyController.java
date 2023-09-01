package com.book.flaschenbook.controller;

import com.book.flaschenbook.dto.ContentPairDTO;
import com.book.flaschenbook.dto.SelectedContentsDTO;
import com.book.flaschenbook.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/survey")
public class SurveyController {
    private final SurveyService surveyService;

    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @GetMapping("/content-pairs")
    public ResponseEntity<List<ContentPairDTO>> getSurveyContentPairs() {
        List<ContentPairDTO> surveyContentPairs = surveyService.getRandomSurveyContentPairs();
        return ResponseEntity.ok(surveyContentPairs);
    }

    @PostMapping("/selected-contents")
    public ResponseEntity<?> handleSelectedContents(@RequestBody SelectedContentsDTO selectedContentsDTO) {
        surveyService.handleSelectedContents(selectedContentsDTO);

        return ResponseEntity.ok().body(Map.of("message", "Success"));

    }
}
