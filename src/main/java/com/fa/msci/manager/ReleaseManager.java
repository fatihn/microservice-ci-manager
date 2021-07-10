package com.fa.msci.manager;

import com.fa.msci.conf.MsciConfig;
import com.fa.msci.dto.Dependency;
import com.fa.msci.dto.Project;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ReleaseManager {
    List<Project> releaseRequiredProjects;
    MsciConfig conf;

    public ReleaseManager(MsciConfig conf) {
        this.conf = conf;
    }

    public void produceReleasePlan() throws Exception {
        SvnChangeFinder svnChangeFinder = new SvnChangeFinder(conf);
        List<Project> changedProjects = svnChangeFinder.findChangedProjects();

        System.out.println("## Release Required Projects");
        changedProjects.forEach(project -> {
            System.out.println("\n#  " + project);
        });
    }

    public void revertAllPomsToSnapshot() throws Exception {
        PomXmlFinder pf = new PomXmlFinder();
        List<Path> pomXmlPaths = new ArrayList<>();
        pf.listFiles(new File(conf.getRootSrcFolderPath()).toPath(), pomXmlPaths);

        pomXmlPaths.forEach(p -> {
            try {
                revertPomToSnapshot(p.toFile().getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    public void incrementDependencyVersion(Project p, Dependency d) {
        //TODO
    }

    public void revertPomToSnapshot(String pomXmlPath) throws Exception {

        // Create a document by parsing a XML file
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document document = builder.parse(new File(pomXmlPath));

        // Get a node using XPath
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "/project/version";
        Node node = (Node) xPath.evaluate(expression, document, XPathConstants.NODE);

        // Set the node content
        if (node != null)
            node.setTextContent("1.0-SNAPSHOT");

        XPath xParentPath = XPathFactory.newInstance().newXPath();
        Node nodeParentGroup = (Node) xParentPath.evaluate("/project/parent/groupId", document, XPathConstants.NODE);
        Node nodeParentVersion = (Node) xParentPath.evaluate("/project/parent/version", document, XPathConstants.NODE);

        // Set the node content
        if (nodeParentVersion != null && nodeParentGroup != null && (nodeParentGroup.getTextContent().contains("xyz")
        )) {

            nodeParentVersion.setTextContent("1.0-SNAPSHOT");
        }

        // Write changes to a file

        NodeList dependencyList = document.getElementsByTagName("dependency");
        for (int i = 0; i < dependencyList.getLength(); i++) {
            Node nNode = dependencyList.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;
                String groupId = eElement.getElementsByTagName("groupId").item(0).getTextContent();
                String artifactId = eElement.getElementsByTagName("artifactId").item(0).getTextContent();

                if (groupId.contains("xyz")) {
                    System.out.println(groupId + ":" + artifactId);
                    if (eElement.getElementsByTagName("version").item(0) != null)
                        eElement.getElementsByTagName("version").item(0).setTextContent("1.0-SNAPSHOT");

                }

            }

        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();

        transformer.transform(new DOMSource(document), new StreamResult(new File(pomXmlPath)));
        System.out.println("Done");

    }

}