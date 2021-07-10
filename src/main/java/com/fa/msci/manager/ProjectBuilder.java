package com.fa.msci.manager;

import com.fa.msci.conf.MsciConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class ProjectBuilder {

    MsciConfig conf;

    public ProjectBuilder(MsciConfig conf) {

        this.conf = conf;
    }

    public void buildAllProjects() throws IOException {
        String projectListFilePath = conf.getWorkspacePath() + File.separator + "conf" + File.separator
                + "projects.txt";

        try (BufferedReader brTest = new BufferedReader(new FileReader(projectListFilePath))) {
            brTest.lines().forEach(p -> {
                String path = conf.getRootSrcFolderPath() + File.separator + p.replace("/", File.separator);
                try {
                    if (!path.contains("#"))
                        Compiler.compile(Paths.get(path));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
        }

    }

    public void cleanAllProjects() throws IOException {
        String projectListFilePath = conf.getWorkspacePath() + File.separator + "conf" + File.separator
                + "projects.txt";

        try (BufferedReader brTest = new BufferedReader(new FileReader(projectListFilePath))) {
            brTest.lines().forEach(p -> {
                String path = conf.getRootSrcFolderPath() + File.separator + p.replace("/", File.separator);
                try {
                    if (!path.contains("#"))
                        Compiler.clean(Paths.get(path));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public void buildSingleProject(String projectName) throws Exception {
        String path = conf.getRootSrcFolderPath() + File.separator + projectName.replace("/", File.separator);
        Compiler.compile(Paths.get(path));
        ;

    }

}
