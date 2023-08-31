package com.book.flaschenbook.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import java.io.Serializable;
import java.util.Objects;

@Data
@Entity
@IdClass(SurveyContentId.class)
@Table(name = "SurveyContent")
public class SurveyContentEntity {
    @Id
    private String id;

    @Id
    private String type;

    private String content;
}

@Data
class SurveyContentId implements Serializable {
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