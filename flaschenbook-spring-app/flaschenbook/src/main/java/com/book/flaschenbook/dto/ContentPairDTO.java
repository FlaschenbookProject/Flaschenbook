package com.book.flaschenbook.dto;

import com.book.flaschenbook.model.SurveyContentModel;
import lombok.Data;

@Data
public class ContentPairDTO {
    private SurveyContentModel content1;
    private SurveyContentModel content2;
}
