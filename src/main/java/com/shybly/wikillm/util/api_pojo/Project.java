package com.shybly.wikillm.util.api_pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {

    private Integer id;

    @JsonProperty("forked_from_project")
    private Project forkedFromProject;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Project getForkedFromProject() {
        return forkedFromProject;
    }
}

