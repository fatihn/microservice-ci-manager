package com.fa.msci.manager;

import com.fa.msci.conf.MsciConfig;
import com.fa.msci.dto.Dependency;
import com.fa.msci.dto.Project;
import com.fa.msci.dto.ReverseDependency;
import com.fa.msci.util.NativeProcessUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class DependencyManager {
    MsciConfig conf;

    public DependencyManager(MsciConfig conf) {

        this.conf = conf;
    }

    public void analyzeDependencies() throws Exception {
        if (!isRepositoryLoaded())
            createRepository();

        getResolvedDependencies();
        getReverseDependencyTree();

    }

    public boolean isRepositoryLoaded() {
        File rootFolder = new File(
                conf.getWorkspacePath() + File.separator + "resolvedDependencyTree" + File.separator);
        if (rootFolder.listFiles().length > 0)
            return true;
        else
            return false;
    }

    public void getReverseDependencyTree() {

        for (Project refProject : ProjectStore.getProjectList()) {
            for (Project otherProject : ProjectStore.getProjectList()) {
                for (Dependency dependency : otherProject.getDependencyList()) {

                    if (refProject.getModule().getGroupId().trim().equalsIgnoreCase(dependency.getGroupId().trim())
                            && refProject.getModule().getArtifactId().trim()
                            .equalsIgnoreCase(dependency.getArtifactId().trim())) {
                        ReverseDependency reverseDependency = new ReverseDependency();
                        reverseDependency.setUsedDependency(dependency);
                        reverseDependency.setUsingProject(otherProject);

                        refProject.getReverseDependencyList().add(reverseDependency);
                        // System.out.println(otherProject + " --> " + dependency);
                    }
                }

            }
        }
    }

    public void getResolvedDependencies() throws IOException {
        ProjectStore.getProjectList().clear();

        File rootFolder = new File(
                conf.getWorkspacePath() + File.separator + "resolvedDependencyTree" + File.separator);
        File[] dtFileList = rootFolder.listFiles();
        List<File> fileList = Arrays.asList(dtFileList);

        fileList.forEach(dtFile -> {
            Project project = new Project();
            try {

                try (Stream<String> stream = Files.lines(dtFile.toPath())) {
                    stream.filter(p -> (p.contains("xyz"))).forEach(s -> {
                        Dependency dep = new Dependency(s);
                        project.getDependencyList().add(dep);
                    });
                }

                File deptreeFile = new File(conf.getWorkspacePath() + File.separator + "dependencyTree" + File.separator
                        + dtFile.getName() + ".dt");
                String projectMeta = null;
                try (BufferedReader brTest = new BufferedReader(new FileReader(deptreeFile))) {
                    projectMeta = brTest.readLine();
                }

                File metaFile = new File(conf.getWorkspacePath() + File.separator + "meta" + File.separator
                        + dtFile.getName() + ".meta");
                String xmlFilePath = null;
                try (BufferedReader brTest = new BufferedReader(new FileReader(metaFile))) {
                    xmlFilePath = brTest.readLine();
                }

                Path pomXml = Paths.get(xmlFilePath);
                project.setModule(new Dependency(projectMeta));
                project.setPomXmlPath(pomXml);

                ProjectStore.getProjectList().add(project);

            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        ProjectStore.getProjectList().forEach(p -> {
            // System.out.println("\n--------------------------------------------------------------------");
            // System.out.println("Project : " + p.getModule());
            // System.out.println(p.getDependencyList().size());

            p.getDependencyList().stream().sorted((s1, s2) -> s1.getGroupId().compareTo(s2.getGroupId()))
                    .sorted((s3, s4) -> s3.getArtifactId().compareTo(s4.getArtifactId())).forEach(d -> {
                // System.out.println(d);
            });

        });
    }

    public void clearRepository() {
        System.out.println(conf.getWorkspacePath());
        File tmpFolder = new File(conf.getWorkspacePath() + File.separator + "resolvedDependencyTree" + File.separator);
        System.out.println(tmpFolder.getAbsolutePath());

        for (File f : tmpFolder.listFiles()) {
            f.delete();
        }
        tmpFolder = new File(conf.getWorkspacePath() + File.separator + "dependencyTree" + File.separator);
        System.out.println(tmpFolder.getAbsolutePath());
        for (File f : tmpFolder.listFiles()) {
            f.delete();
        }
        tmpFolder = new File(conf.getWorkspacePath() + File.separator + "meta" + File.separator);
        System.out.println(tmpFolder.getAbsolutePath());
        for (File f : tmpFolder.listFiles()) {
            f.delete();
        }
    }


    public void createRepository() throws Exception {

        clearRepository();

        PomXmlFinder pf = new PomXmlFinder();
        List<Path> pomXmlPaths = new ArrayList<>();
        pf.listFiles(new File(conf.getRootSrcFolderPath()).toPath(), pomXmlPaths);

        pomXmlPaths.forEach(p -> {
            System.out.println(" - " + p);
        });

        for (Path p : pomXmlPaths) {

            String projectName = p.toFile().getParentFile().getAbsolutePath().replace(conf.getRootSrcFolderPath(), "");
            projectName = projectName.replace(File.separator, "_");

            String resolvedDependencyFileName = conf.getWorkspacePath() + File.separator + "resolvedDependencyTree"
                    + File.separator + projectName + ".rt";
            String dependencyTreeFileName = conf.getWorkspacePath() + File.separator + "dependencyTree" + File.separator
                    + projectName + ".rt.dt";
            String metaDataFileName = conf.getWorkspacePath() + File.separator + "meta" + File.separator + projectName
                    + ".rt.meta";

            System.out.print("\n###  ");
            System.out.println(p.toFile().getAbsolutePath());

            Path metaDataPath = Paths.get(metaDataFileName);
            try (BufferedWriter writer = Files.newBufferedWriter(metaDataPath)) {
                writer.write(p.toFile().getAbsolutePath());
            }
            String mvnCmd = "mvn";

            if (System.getProperty("os.name").toLowerCase().contains("win"))
                mvnCmd = "mvn.cmd";

            ProcessBuilder pbResolveDep = new ProcessBuilder(mvnCmd, // "-sC:\\Users\\fatih\\.m2\\settings.xml",
                    "dependency:resolve", "-DoutputFile=" + resolvedDependencyFileName);

            pbResolveDep.directory(p.toFile().getParentFile());
            Process processResolveDep = pbResolveDep.start();
            System.out.println(NativeProcessUtil.getProcessOutput(processResolveDep.getInputStream()));
            processResolveDep.waitFor();

            ProcessBuilder pbDepTree = new ProcessBuilder(mvnCmd, // "-sC:\\Users\\fatih\\.m2\\settings.xml",
                    "dependency:tree", "-DoutputFile=" + dependencyTreeFileName);

            pbDepTree.directory(p.toFile().getParentFile());
            Process processDepTree = pbDepTree.start();
            System.out.println(NativeProcessUtil.getProcessOutput(processDepTree.getInputStream()));
            processDepTree.waitFor();

        }
    }

    public void listDependencies() {
        ProjectStore.getProjectList().stream()
                .sorted((a, b) -> Integer.compare(a.getDependencyList().size(), b.getDependencyList().size()))
                .forEach(p -> {
                    p.getDependencyList().forEach(d -> {
                        Project dependencyProject = getProjectFromDependency(d);
                        if (dependencyProject == null)
                            System.out.println("----- > " + d);
                        else
                            System.out.println(d);
                    });
                });
    }

    public void listReverseDependencies() {
        ProjectStore.getProjectList().stream().sorted(
                (a, b) -> Integer.compare(a.getReverseDependencyList().size(), b.getReverseDependencyList().size()))
                .forEach(p -> {
                    if (p.getReverseDependencyList().size() > 0) {
                        System.out.println("\n\n# " + p);
                        p.getReverseDependencyList().forEach(d -> {
                            System.out.println(d);
                        });
                    }
                });
    }

    public Project getProjectFromDependency(Dependency d) {
        return ProjectStore.getProjectList().stream().filter(p -> p.getModule().getGroupId().equals(d.getGroupId())
                && p.getModule().getArtifactId().equals(d.getArtifactId())).findFirst().orElse(null);
    }
}
