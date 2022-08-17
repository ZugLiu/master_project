package com.zugangliu.finalproject.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Community {
    private int id;
    private String cName;
    private String cAbout;
    private String cCreator;
    private int cPopulation;
    private String cHeaderImg;
    private String cRules;

    public Community(int id, String cName){
        this.id = id;
        this.cName = cName;
    }

    public Community(int id) {
        this.id = id;
    }
}
