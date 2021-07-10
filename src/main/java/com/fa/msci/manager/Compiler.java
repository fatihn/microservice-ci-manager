package com.fa.msci.manager;

import com.fa.msci.util.NativeProcessUtil;

import java.io.File;
import java.nio.file.Path;

public class Compiler {

    public static void compile(Path pomXmlFilePath) throws Exception {

        int i = pomXmlFilePath.toFile().getAbsolutePath().indexOf("trunk") + 6;
        String parentPath = pomXmlFilePath.toFile().getAbsolutePath().substring(0, i - 1);

        System.out.println("\n##############");
        System.out.println("MVN BUILDING : " + pomXmlFilePath.toFile().getAbsolutePath());

        ProcessBuilder pbSvnUpdate = new ProcessBuilder("svn", "update");
        pbSvnUpdate.directory(new File(parentPath));
        Process processSvnUpdate = pbSvnUpdate.start();
        System.out.println(NativeProcessUtil.getProcessOutput(processSvnUpdate.getInputStream()));
        processSvnUpdate.waitFor();

        String mvnCmd = "mvn";

        if (System.getProperty("os.name").toLowerCase().contains("win"))
            mvnCmd = "mvn.cmd";

        ProcessBuilder pbCleanInstall = new ProcessBuilder(mvnCmd, "clean", "install", "-Dmaven.test.skip=true");
        pbCleanInstall.directory(new File(parentPath));
        Process processCleanInstall = pbCleanInstall.start();
        String processOutput = NativeProcessUtil.getProcessOutput(processCleanInstall.getInputStream());
        processCleanInstall.waitFor();

        if (processOutput.contains("BUILD SUCCESS")) {
            System.out.println("" + "BUILD SUCCESS");
        } else {
            System.out.println(processOutput);
            System.out.println("" + "BUILD FAILURE");
            throw new Exception(pomXmlFilePath + " --> Build Failure");
        }

    }

    public static void clean(Path pomXmlFilePath) throws Exception {

        int i = pomXmlFilePath.toFile().getAbsolutePath().indexOf("trunk") + 6;
        String parentPath = pomXmlFilePath.toFile().getAbsolutePath().substring(0, i - 1);

        System.out.println("\n##############");
        System.out.println("MVN CLEAN : " + pomXmlFilePath.toFile().getAbsolutePath());

        String mvnCmd = "mvn";

        if (System.getProperty("os.name").toLowerCase().contains("win"))
            mvnCmd = "mvn.cmd";

        ProcessBuilder pbCleanInstall = new ProcessBuilder(mvnCmd, "clean", "-Dmaven.test.skip=true");
        pbCleanInstall.directory(new File(parentPath));
        Process processCleanInstall = pbCleanInstall.start();
        String processOutput = NativeProcessUtil.getProcessOutput(processCleanInstall.getInputStream());
        processCleanInstall.waitFor();

        if (processOutput.contains("BUILD SUCCESS")) {
            System.out.println("" + "CLEAN SUCCESS");
        } else {
            System.out.println(processOutput);
            System.out.println("" + "BUILD FAILURE");
            throw new Exception(pomXmlFilePath + " --> Build Failure");
        }

    }
}
