package com.fa.msci.manager;

import com.fa.msci.conf.MsciConfig;
import com.fa.msci.util.NativeProcessUtil;

import java.io.*;

public class Deployer {

    MsciConfig conf;

    public Deployer(MsciConfig conf) {
        this.conf = conf;
    }

    public void deploySingleProject(String projectName) throws InterruptedException, IOException {

        System.out.println("Deploying : " + projectName);

        ProcessBuilder pbFindCmd = new ProcessBuilder("find", ".", "-name", projectName + "*.jar");
        pbFindCmd.directory(new File(conf.getRootSrcFolderPath()));
        Process processFindCmd = pbFindCmd.start();
        String output = NativeProcessUtil.getProcessOutput(processFindCmd.getInputStream());
        processFindCmd.waitFor();
        String jarToBeCopied = null;
        String[] lines = output.split(System.getProperty("line.separator"));
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains("-sources") || lines[i].contains("-shared"))
                continue;
            jarToBeCopied = lines[i];
        }
        jarToBeCopied = conf.getRootSrcFolderPath() + jarToBeCopied.replace("./", "");
        System.out.println("Found : " + jarToBeCopied);

        if (jarToBeCopied == null || jarToBeCopied.equals(""))
            throw new RuntimeException("Jar file : " + projectName + " not found!");

        File jarFile = new File(jarToBeCopied);

        String fileNameToBeCopied = jarFile.getParent() + File.separator + projectName + ".jar";

        File renamedJarFile = new File(fileNameToBeCopied);
        jarFile.renameTo(renamedJarFile);

        System.out.println("Copying : " + renamedJarFile.getAbsolutePath() + " to " + conf.getSshUserName() + "@" + conf.getSshIp() + ":" + conf.getSshTargetPath());
        ProcessBuilder pbScpCmd = new ProcessBuilder("scp", renamedJarFile.getAbsolutePath(),
                conf.getSshUserName() + "@" + conf.getSshIp() + ":" + conf.getSshTargetPath());

        Process processScpCmd = pbScpCmd.start();
        System.out.println(NativeProcessUtil.getProcessOutput(processScpCmd.getInputStream()));
        processScpCmd.waitFor();
        System.out.println("Deployed : " + renamedJarFile.getAbsolutePath());

    }

    public void deployAllProjects() throws FileNotFoundException, IOException {
        String deployableProjectListFilePath = conf.getWorkspacePath() + File.separator + "conf" + File.separator
                + "deployables.txt";
        try (BufferedReader brTest = new BufferedReader(new FileReader(deployableProjectListFilePath))) {
            brTest.lines().forEach(projectName -> {

                try {
                    deploySingleProject(projectName);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException();
                }

            });
        }

    }

}
