package com.fa.msci.dto;

public class Dependency {
    private String groupId;

    private String artifactId;

    private String packaging;

    private String classifier;

    private String version;

    private String scope;

    private String description;

    private boolean omitted;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isOmitted() {
        return omitted;
    }

    public void setOmitted(boolean omitted) {
        this.omitted = omitted;
    }

    public Dependency(String dependencyString) {
        String[] items = dependencyString.split(":");
        this.groupId = items[0].trim();
        this.artifactId = items[1].trim();
        this.packaging = items[2].trim();
        this.version = items[3].trim();
    }

    public Dependency() {
    }

    public String toString() {
        return this.groupId + ":" + this.artifactId + ":" + this.version;
    }
}
