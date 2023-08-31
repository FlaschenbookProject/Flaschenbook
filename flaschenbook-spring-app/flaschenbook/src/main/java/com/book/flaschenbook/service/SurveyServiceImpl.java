package com.book.flaschenbook.service;

import com.book.flaschenbook.dto.ContentPairDTO;
import com.book.flaschenbook.dto.SelectedContentsDTO;
import com.book.flaschenbook.entity.SurveyContentEntity;
import com.book.flaschenbook.entity.SurveyDetailsEntity;
import com.book.flaschenbook.entity.SurveysEntity;
import com.book.flaschenbook.model.SurveyContentModel;
import com.book.flaschenbook.repository.SurveyContentRepository;
import com.book.flaschenbook.repository.SurveyDetailsRepository;
import com.book.flaschenbook.repository.SurveysRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SurveyServiceImpl implements SurveyService{
    private final SurveyContentRepository surveyContentRepository;
    private final SurveysRepository surveysRepository;
    private final SurveyDetailsRepository surveyDetailsRepository;
    private final ModelMapper modelMapper;

    public SurveyServiceImpl(SurveyContentRepository surveyContentRepository, SurveysRepository surveysRepository, SurveyDetailsRepository surveyDetailsRepository, ModelMapper modelMapper) {
        this.surveyContentRepository = surveyContentRepository;
        this.surveysRepository = surveysRepository;
        this.surveyDetailsRepository = surveyDetailsRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ContentPairDTO> getRandomSurveyContentPairs() {
        List<SurveyContentEntity> sContents = getRandomContentsByType("S", 8);
        List<SurveyContentEntity> cContents = getRandomContentsByType("C", 6);
        List<SurveyContentEntity> tContents = getRandomContentsByType("T", 6);

        List<ContentPairDTO> pairs = new ArrayList<>();
        pairs.addAll(createContentPairDTOs(sContents));
        pairs.addAll(createContentPairDTOs(cContents));
        pairs.addAll(createContentPairDTOs(tContents));

        return pairs;
    }

    private List<SurveyContentEntity> getRandomContentsByType(String type, int count) {
        List<SurveyContentEntity> contentsByType = surveyContentRepository.findAllByType(type);
        Collections.shuffle(contentsByType);
        return contentsByType.subList(0, Math.min(contentsByType.size(), count));
    }

    private List<ContentPairDTO> createContentPairDTOs(List<SurveyContentEntity> contents) {
        List<ContentPairDTO> pairs = new ArrayList<>();
        for (int i = 0; i < contents.size(); i += 2) {
            ContentPairDTO pair = new ContentPairDTO();
            pair.setContent1(modelMapper.map(contents.get(i), SurveyContentModel.class));
            if (i + 1 < contents.size()) {
                pair.setContent2(modelMapper.map(contents.get(i + 1), SurveyContentModel.class));
            } else {
                pair.setContent2(null);
            }
            pairs.add(pair);
        }
        return pairs;
    }

    @Override
    public void handleSelectedContents(SelectedContentsDTO selectedContentsDTO) {
        // Create a new SurveysEntity and save it
        SurveysEntity surveysEntity = new SurveysEntity();
        surveysEntity.setUserId(selectedContentsDTO.getUserId());
        surveysEntity = surveysRepository.save(surveysEntity);

        // Create and save SurveyDetailsEntities
        for (SurveyContentModel contentModel : selectedContentsDTO.getSelectedContents()) {
            SurveyDetailsEntity detailsEntity = new SurveyDetailsEntity();
            detailsEntity.setSurvey(surveysEntity);
            detailsEntity.setContentType(contentModel.getType());
            detailsEntity.setContentId(contentModel.getId());
            surveyDetailsRepository.save(detailsEntity);
        }
    }
}
