package com.book.flaschenbook.entity;


import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class SurveyContentId implements Serializable {
    private String id;
    private String type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SurveyContentId that = (SurveyContentId) o;
        return Objects.equals(id, that.id) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }
}
