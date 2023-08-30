package com.book.flaschenbook.dto;

import com.book.flaschenbook.model.SurveyContentModel;
import lombok.Data;

import java.util.List;

@Data
public class SelectedContentsDTO {
    private int userId;
    private List<SurveyContentModel> selectedContents;
}
