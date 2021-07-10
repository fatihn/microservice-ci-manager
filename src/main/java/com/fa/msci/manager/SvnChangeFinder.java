package com.fa.msci.manager;

import com.fa.msci.conf.MsciConfig;
import com.fa.msci.dto.Project;
import com.fa.msci.util.NativeProcessUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SvnChangeFinder {
    MsciConfig conf;

    public SvnChangeFinder(MsciConfig conf) {
        this.conf = conf;
    }

    public Set<String> getParentProjectPathSet() throws IOException {
        PomXmlFinder pf = new PomXmlFinder();
        List<Path> pomXmlFilePaths = new ArrayList<>();
        pf.listFiles(new File(conf.getRootSrcFolderPath()).toPath(), pomXmlFilePaths);

        Set<String> parentProjectSet = new HashSet<>();

        pomXmlFilePaths.forEach(xmlFilePath -> {
            int trunkIndex = xmlFilePath.toFile().getParentFile().getAbsolutePath().indexOf("trunk") + 5;
            String parentPath = xmlFilePath.toFile().getParentFile().getAbsolutePath().substring(0, trunkIndex);
            parentProjectSet.add(parentPath);
        });
        return parentProjectSet;
    }


    public List<Project> findChangedProjects() throws Exception {
        if (ProjectStore.getProjectList() == null)
            throw new Exception("Project list is not set!");
        if (ProjectStore.getProjectList().size() == 0)
            throw new Exception("Repository is not set. Execute : java -jar msci.jar -cr -w{...} -s{...}  ");

        List<Project> changedProjectList = new ArrayList<>();
        for (Project p : ProjectStore.getProjectList()) {
            String svnFolder = p.getPomXmlPath().toFile().getParent();

            ProcessBuilder pbSvnChecker = new ProcessBuilder("svn", "log", "--xml", "--username", "jenkins.cli",
                    "--password", "123", svnFolder, "--limit", "3");

            Process processSvnChecker = pbSvnChecker.start();

            String output = NativeProcessUtil.getProcessOutput(processSvnChecker.getInputStream());

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(output.getBytes()));

            NodeList nList = document.getElementsByTagName("logentry");
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;
                    String author = eElement.getElementsByTagName("author").item(0).getTextContent();
                    String msg = eElement.getElementsByTagName("msg").item(0).getTextContent();

                    if (i == 0 && !author.equals("jenkins")
                            && !msg.trim().equals("[maven-release-plugin] prepare for next development iteration")) {
                        changedProjectList.add(p);
                    }

                }

            }

            processSvnChecker.waitFor();
        }
        return changedProjectList;
    }

}
