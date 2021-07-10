package com.fa.msci.manager;

import com.fa.msci.dto.Project;

import java.util.ArrayList;
import java.util.List;

public class ProjectStore {
    private static List<Project> projectList = new ArrayList<>();

    public static List<Project> getProjectList() {
        return projectList;
    }
}
