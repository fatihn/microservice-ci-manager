package com.fa.msci.client;

import com.fa.msci.conf.MsciConfig;
import com.fa.msci.manager.*;
import org.apache.commons.cli.*;

public class MSCICommandLineInterface {
    public static void printUsage(Options o) {

        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null);

        formatter.printHelp("Main", o);
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        MsciConfig conf = new MsciConfig();

        Options options = new Options();
        options.addOption("s", true, "required : path to svn source code folder");
        options.addOption("w", true, "required : path to msci application workspace");

        options.addOption("b", true, "build single project");
        options.addOption("d", true, "deploy single project");

        options.addOption("ba", false,
                "build all - build projects listed in {application_workspace_path}/conf/projecs.txt");
        options.addOption("ca", false,
                "clean all - clean projects listed in {application_workspace_path}/conf/projecs.txt");
        options.addOption("da", false,
                "deploy all - deploy projects listed in {application_workspace_path}/conf/deployables.txt");
        options.addOption("dip", true, "deploy - ssh ip address");
        options.addOption("dus", true, "deploy - ssh username");
        options.addOption("dpt", true, "deploy - ssh folder path");

        options.addOption("cr", false, "create dependency repository");
        options.addOption("lrd", false, "list reverse dependencies");

        options.addOption("sc", false, "svn checker");
        options.addOption("rp", false, "produce release plan");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("w")) {
            conf.setWorkspacePath(cmd.getOptionValue("w"));
        } else
            printUsage(options);

        if (cmd.hasOption("s")) {
            conf.setRootSrcFolderPath(cmd.getOptionValue("s"));
        } else
            printUsage(options);

        if (cmd.hasOption("dip")) {
            conf.setSshIp(cmd.getOptionValue("dip"));
        }
        if (cmd.hasOption("dus")) {
            conf.setSshUserName(cmd.getOptionValue("dus"));
        }
        if (cmd.hasOption("dpt")) {
            conf.setSshTargetPath(cmd.getOptionValue("dpt"));
        }

        DependencyManager analyzer = new DependencyManager(conf);

        if (cmd.hasOption("cr")) {
            analyzer.createRepository();
        }

        if (cmd.hasOption("lrd")) {
            analyzer.analyzeDependencies();
            analyzer.listReverseDependencies();
        }

        if (cmd.hasOption("rp")) {
            analyzer.analyzeDependencies();
            ReleaseManager rm = new ReleaseManager(conf);
            rm.produceReleasePlan();
        }

        if (cmd.hasOption("sc")) {
            analyzer.analyzeDependencies();
            SvnChangeFinder scf = new SvnChangeFinder(conf);
            scf.findChangedProjects().stream().forEach(p -> {
                System.out.println(p);
            });
        }

        if (cmd.hasOption("ba")) {

            ProjectBuilder projectBuilder = new ProjectBuilder(conf);
            projectBuilder.buildAllProjects();
        }
        if (cmd.hasOption("ca")) {

            ProjectBuilder projectBuilder = new ProjectBuilder(conf);
            projectBuilder.cleanAllProjects();
        }

        if (cmd.hasOption("b")) {

            ProjectBuilder projectBuilder = new ProjectBuilder(conf);
            projectBuilder.buildSingleProject(cmd.getOptionValue("b"));
        }

        if (cmd.hasOption("da")) {
            if (!cmd.hasOption("dip") || !cmd.hasOption("dus") || !cmd.hasOption("dpt"))
                printUsage(options);

            Deployer deployer = new Deployer(conf);
            deployer.deployAllProjects();
        }
        //
        if (cmd.hasOption("d")) {
            if (!cmd.hasOption("dip") || !cmd.hasOption("dus") || !cmd.hasOption("dpt"))
                printUsage(options);

            Deployer deployer = new Deployer(conf);
            deployer.deploySingleProject(cmd.getOptionValue("d"));
        }

    }
}
