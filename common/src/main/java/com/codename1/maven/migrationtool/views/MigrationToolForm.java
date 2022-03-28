package com.codename1.maven.migrationtool.views;

import com.codename1.components.SpanLabel;
import com.codename1.components.SplitPane;
import com.codename1.components.ToastBar;
import com.codename1.io.Preferences;
import com.codename1.maven.migrationtool.models.ProjectMigrationRequest;
import com.codename1.ui.*;
import com.codename1.ui.Button;
import com.codename1.ui.Container;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.util.Resources;

import static com.codename1.ui.ComponentSelector.$;

public class MigrationToolForm extends Form {

    private ProjectMigrationRequest model;

    private String usePluginVersion;
    private final Button browseSourceProject, selectOutputDirectory, createProject;
    private final Container mavenDetailsCnt;
    private final TextField sourceProjectPath;
    private final TextField destinationProjectPath;
    private final TextField groupId;
    private final TextField artifactId;
    private Resources theme;
    private final Label endOfConsoleMarker;
    private final TextArea consoleBuffer;
    private final CheckBox verboseMode;

    private Container appDetailsCnt;
    private final TextField mainName, packageName;

    public MigrationToolForm(ProjectMigrationRequest model, Resources theme) {
        super("Maven Migration Tool", new BorderLayout());
        this.theme = theme;
        this.model = model;
        Form hi = this;

        hi.getToolbar().hideToolbar();

        usePluginVersion = Preferences.get("cn1Version", "LATEST");
        TextField cn1Version = new TextField(usePluginVersion);
        cn1Version.addActionListener(evt->{
            Preferences.set("cn1Version", cn1Version.getText());
            usePluginVersion = cn1Version.getText();
        });

        SpanLabel welcome = new SpanLabel("This tool can help you to migrate your existing Codename One projects over to the new Maven project structure.");

        sourceProjectPath = new TextField();
        sourceProjectPath.setHint("/path/to/existing/project");
        sourceProjectPath.addActionListener(e->model.setSourceProjectPath(sourceProjectPath.getText()));

        destinationProjectPath = new TextField(Preferences.get("destinationProjectPath", ""));
        destinationProjectPath.addActionListener(evt->{
            Preferences.set("destinationProjectPath", destinationProjectPath.getText());
        });
        destinationProjectPath.addActionListener(e->model.setDestinationProjectPath(destinationProjectPath.getText()));

        destinationProjectPath.setHint("/path/to/outputproject");

        groupId = new TextField(Preferences.get("groupId", ""));
        groupId.addActionListener(evt->{
            Preferences.set("groupId", groupId.getText());
            model.setGroupId(groupId.getText());
        });
        groupId.setHint("com.example.mylib");

        artifactId = new TextField();
        artifactId.setHint("my-library");
        artifactId.addActionListener(e->{
            model.setArtifactId(artifactId.getText());
        });

        mavenDetailsCnt = BoxLayout.encloseY(
                new Label("Maven Details", "H2"),
                new Label("Group ID", "FieldLabel"),
                groupId,
                new Label("Artifact ID", "FieldLabel"),
                artifactId);

        mainName = new TextField("");
        mainName.addActionListener(e->model.setMainName(mainName.getText()));

        packageName = new TextField("");
        packageName.addActionListener(e->model.setPackageName(packageName.getText()));

        appDetailsCnt = BoxLayout.encloseY(
                new Label("App Details", "H2"),
                new Label("Package Name", "FieldLabel"),
                packageName,
                new Label("Main Class Name", "FieldLabel"),
                mainName
        );



        browseSourceProject = new Button("Browse...");

        selectOutputDirectory = new Button("Browse...");

        verboseMode = new CheckBox("Verbose Output");
        verboseMode.addActionListener(evt->model.setVerboseMode(verboseMode.isSelected()));


        createProject = new Button("Create Project", "FeaturedButton");

        Image cn1Logo = theme.getImage("codenameone-logo.png");

        Container wrapper = BoxLayout.encloseY(

                BorderLayout.centerAbsoluteEastWest(new Label("Maven Migration Tool", "Title"), null, new Label(cn1Logo)),
                welcome,
                new Label("Select Codename One Plugin Version (leave blank for LATEST)", "FieldLabel"),
                cn1Version,
                new Label("Source Project Path", "FieldLabel"),
                BorderLayout.centerEastWest(sourceProjectPath, browseSourceProject, null),
                new Label("Destination Directory", "FieldLabel"),
                BorderLayout.centerEastWest(destinationProjectPath, selectOutputDirectory, null),
                mavenDetailsCnt,
                appDetailsCnt,
                FlowLayout.encloseIn(verboseMode),
                createProject
        );

        wrapper.setScrollableY(true);
        endOfConsoleMarker = new Label();
        consoleBuffer = new TextArea();
        consoleBuffer.setMaxSize(9999999);
        consoleBuffer.setEditable(false);
        consoleBuffer.setUIID("ConsoleBuffer");
        consoleBuffer.setGrowByContent(true);


        Container consoleWrapper = new Container(BoxLayout.y());
        consoleWrapper.setScrollableY(true);
        consoleWrapper.add(consoleBuffer);
        consoleWrapper.add(endOfConsoleMarker);
        $(consoleWrapper).selectAllStyles().setBgColor(0xffffff).setBgTransparency(0xff);

        Button copyConsoleToClipboard = new Button(FontImage.MATERIAL_CONTENT_COPY);
        copyConsoleToClipboard.addActionListener(evt->{
            Display.getInstance().copyToClipboard(consoleBuffer.getText());
            ToastBar.showInfoMessage("Console contents copied to clipboard");
        });

        SplitPane splitPane = new SplitPane(SplitPane.VERTICAL_SPLIT, wrapper, BorderLayout.center(consoleWrapper).add(BorderLayout.SOUTH, copyConsoleToClipboard), "10%", "70%", "100%");

        hi.add(BorderLayout.CENTER, splitPane);
    }

    public MigrationToolForm onBrowseForSourceProject(ActionListener e) {
        browseSourceProject.addActionListener(e);
        return this;
    }

    public MigrationToolForm onBrowseForOutputProject(ActionListener e) {
        selectOutputDirectory.addActionListener(e);
        return this;
    }

    public MigrationToolForm onCreateProject(ActionListener e) {
        createProject.addActionListener(e);
        return this;
    }


    public void updateUI() {
        sourceProjectPath.setText(strval(model.getSourceProjectPath()));
        destinationProjectPath.setText(strval(model.getDestinationProjectPath()));
        if (model.isInProgress()) {
            createProject.setEnabled(false);
        } else {
            createProject.setEnabled(true);
        }
        artifactId.setText(strval(model.getArtifactId()));
        groupId.setText(strval(model.getGroupId()));
        verboseMode.setSelected(model.isVerboseMode());

        mainName.setText(strval(model.getMainName()));
        packageName.setText(strval(model.getPackageName()));

        boolean animateLayout = false;

        if (model.getProjectType() == ProjectMigrationRequest.ProjectType.App && appDetailsCnt.isHidden()) {
            appDetailsCnt.setHidden(false);
            animateLayout = true;
        }
        if (model.getProjectType() == ProjectMigrationRequest.ProjectType.Library && !appDetailsCnt.isHidden()) {
            appDetailsCnt.setHidden(true);
            animateLayout = true;
        }
        if (model.getProjectType() == ProjectMigrationRequest.ProjectType.Library && mavenDetailsCnt.isHidden()) {
            mavenDetailsCnt.setHidden(false);
            animateLayout = true;
        }
        if (model.getProjectType() == ProjectMigrationRequest.ProjectType.App && !mavenDetailsCnt.isHidden()) {
            mavenDetailsCnt.setHidden(true);
            animateLayout = true;
        }


        if (animateLayout) {
            mavenDetailsCnt.getParent().animateLayout(500);
        }


    }

    private static String strval(String str) {
        if (str == null) return "";
        return str;
    }

    public ProjectMigrationRequest getModel() {
        return model;
    }

    public TextField getSourceProjectPath() {
        return sourceProjectPath;
    }

    public TextField getDestinationProjectPath() {
        return destinationProjectPath;
    }

    public TextArea getConsoleBuffer() {
        return consoleBuffer;
    }

    public TextField getGroupId() {
        return groupId;
    }

    public TextField getArtifactId() {
        return artifactId;
    }

    public void appendToConsole(String text) {
        getConsoleBuffer().setText(consoleBuffer.getText() + text + "\n");
        getConsoleBuffer().getParent().revalidateWithAnimationSafety();
        getConsoleBuffer().scrollRectToVisible(0, 0, 1, 1, endOfConsoleMarker);
    }
}
