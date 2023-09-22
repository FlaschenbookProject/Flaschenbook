package com.book.flaschenbook.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
public class SurveySummaryKey implements Serializable {

    private int surveyId;
    private int userId;
    private String type;
    private String contentId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SurveySummaryKey that = (SurveySummaryKey) o;
        return surveyId == that.surveyId && userId == that.userId && Objects.equals(type, that.type) && Objects.equals(contentId, that.contentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(surveyId, userId, type, contentId);
    }
}
