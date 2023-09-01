package com.book.flaschenbook.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "CodeDetail")
public class CodeDetailEntity {

    @Id
    private Integer code;

    private Integer commonCode;

    private String codeName;
    private String description;

    private String etc1;
    private String etc2;
    private String etc3;
    private String etc4;

}
