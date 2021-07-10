package com.fa.msci.manager;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PomXmlFinder {
    List<String> foldersOmitted;

    public PomXmlFinder() {
        foldersOmitted = new ArrayList<String>();
        foldersOmitted.add("tags");
        foldersOmitted.add("branches");
        foldersOmitted.add("inhouse-dev");
    }

    public void listFiles(Path path, List<Path> pomXmlPaths) throws IOException {
        boolean isSrcFound = false;
        File[] files = path.toFile().listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isFile())
                    return true;
                else
                    return false;
            }
        });
        File[] dirs = path.toFile().listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory())
                    return true;
                else
                    return false;
            }
        });
        for (File file : files) {
            if (file.getName().equals("pom.xml")) {
                for (File dir : dirs) {
                    if (dir.getName().equals("src")) {
                        pomXmlPaths.add(file.toPath());
                        isSrcFound = true;
                        break;
                    }
                }

                break;
            }
        }
        if (!isSrcFound) {
            for (File file : dirs) {
                if (file.isDirectory() && foldersOmitted.contains(file.getName()))
                    continue;
                if (Files.isDirectory(file.toPath())) {
                    listFiles(file.toPath(), pomXmlPaths);
                }

            }
        }

    }
}
