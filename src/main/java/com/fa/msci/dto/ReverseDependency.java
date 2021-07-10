package com.fa.msci.dto;

public class ReverseDependency {
    Project usingProject;
    Dependency usedDependency;

    public Project getUsingProject() {
        return usingProject;
    }

    public void setUsingProject(Project usingProject) {
        this.usingProject = usingProject;
    }

    public Dependency getUsedDependency() {
        return usedDependency;
    }

    public void setUsedDependency(Dependency usedDependency) {
        this.usedDependency = usedDependency;
    }

    public String toString() {
        return usingProject + " -- " + usedDependency;
    }
}
