package com.codename1.maven.migrationtool.models;

public class ProjectMigrationRequest {

    private ProjectType projectType;
    private String sourceProjectPath;
    private String destinationProjectPath;
    private String groupId;
    private String artifactId;
    private boolean inProgress;
    private boolean verboseMode;
    private String usePluginVersion;

    private String mainName;
    private String packageName;

    public String getSourceProjectPath() {
        return sourceProjectPath;
    }

    public void setSourceProjectPath(String sourceProjectPath) {
        this.sourceProjectPath = sourceProjectPath;
    }

    public String getDestinationProjectPath() {
        return destinationProjectPath;
    }

    public void setDestinationProjectPath(String destinationProjectPath) {
        this.destinationProjectPath = destinationProjectPath;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public boolean isVerboseMode() {
        return verboseMode;
    }

    public void setVerboseMode(boolean verboseMode) {
        this.verboseMode = verboseMode;
    }

    public String getMainName() {
        return mainName;
    }

    public void setMainName(String mainName) {
        this.mainName = mainName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    public String getUsePluginVersion() {
        return usePluginVersion;
    }

    public void setUsePluginVersion(String usePluginVersion) {
        this.usePluginVersion = usePluginVersion;
    }


    public static enum ProjectType {
        Library,
        App
    }

}
