package com.fa.msci.dto;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Project {
    private Dependency module;
    private Path pomXmlPath;

    private List<Dependency> dependencyList = new ArrayList<>();
    private List<ReverseDependency> reverseDependencyList = new ArrayList<>();

    public Path getPomXmlPath() {
        return pomXmlPath;
    }

    public void setPomXmlPath(Path pomXmlPath) {
        this.pomXmlPath = pomXmlPath;
    }

    public List<ReverseDependency> getReverseDependencyList() {
        return reverseDependencyList;
    }

    public void setReverseDependencyList(List<ReverseDependency> reverseDependencyList) {
        this.reverseDependencyList = reverseDependencyList;
    }

    public String toString() {
        return module.toString();

    }

    public Dependency getModule() {
        return module;
    }

    public void setModule(Dependency module) {
        this.module = module;
    }

    public List<Dependency> getDependencyList() {
        return dependencyList;
    }

    public void setDependencyList(List<Dependency> dependencyList) {
        this.dependencyList = dependencyList;
    }

}
