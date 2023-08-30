package com.book.flaschenbook.service;

import com.book.flaschenbook.dto.ContentPairDTO;
import com.book.flaschenbook.dto.SelectedContentsDTO;
import com.book.flaschenbook.model.SurveyContentModel;

import java.util.List;

public interface SurveyService {
    List<ContentPairDTO> getRandomSurveyContentPairs();
    void handleSelectedContents(SelectedContentsDTO selectedContents);
}
