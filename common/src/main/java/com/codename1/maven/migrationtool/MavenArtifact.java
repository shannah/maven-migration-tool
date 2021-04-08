package com.codename1.maven.migrationtool;

import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

public class MavenArtifact {
    private final String groupId;
    private final String artifactId;
    private String latestVersionOnCentral;

    public MavenArtifact(String groupId, String artifactId) {
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    private String getGroupIdAsPath() {
        return getGroupId().replace(".", "/");
    }

    private String getArtifactAsPath() {
        return getGroupIdAsPath() + "/" + getArtifactId();
    }

    public String findLatestVersionOnMavenCentral() throws IOException, XmlPullParserException {
        URL mavenMetadata = new URL("https://repo1.maven.org/maven2/"+getArtifactAsPath() +"/maven-metadata.xml");
        MetadataXpp3Reader reader = new MetadataXpp3Reader();
        try (Reader input = new InputStreamReader(mavenMetadata.openStream(), "UTF-8")) {
            Metadata metadata = reader.read(input, false);
            this.latestVersionOnCentral = metadata.getVersioning().getLatest();
            return this.latestVersionOnCentral;
        }
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getLatestVersionOnCentral() {
        return latestVersionOnCentral;
    }

    public void setLatestVersionOnCentral(String latestVersionOnCentral) {
        this.latestVersionOnCentral = latestVersionOnCentral;
    }
}
