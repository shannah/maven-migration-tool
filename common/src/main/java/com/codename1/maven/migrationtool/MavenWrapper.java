package com.codename1.maven.migrationtool;

import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class MavenWrapper {
    private final File projectDir;
    private Writer output;
    private static String dummyProjectPath = System.getProperty("user.home") + File.separator + ".codenameone" + File.separator + "migrationTool" + File.separator + "dummyMavenProject";



    public MavenWrapper(File projectDir) {
        this.projectDir = projectDir;
    }

    public MavenWrapper output(Writer writer) {
        this.output = writer;
        return this;
    }

    private static File dummyProjectDir() {
        return new File(dummyProjectPath);
    }

    private static void extractDummyProject() throws IOException {
        File dummyProjectDir = new File(dummyProjectPath);
        if (!dummyProjectDir.exists()) {
            File canonicalProjectDir = dummyProjectDir.getCanonicalFile();
            if (!canonicalProjectDir.getParentFile().exists()) {
                canonicalProjectDir.getParentFile().mkdirs();
            }

            File tmpZip = File.createTempFile("migrationtoolDummyProject", ".zip");
            tmpZip.delete();
            FileUtils.copyURLToFile(MavenWrapper.class.getResource("/Dummy.zip"), tmpZip);

            File tmpZipExtracted = new File(tmpZip.getParentFile(), tmpZip.getName() + "-extracted");
            new ZipFile(tmpZip).extractAll(tmpZipExtracted.getPath());
            File tmpProjectDir = tmpZipExtracted;
            File pomFile = new File(tmpZipExtracted, "pom.xml");
            if (!pomFile.exists()) {
                for (File child : tmpZipExtracted.listFiles()) {
                    if (child.isDirectory()) {
                        File childPomFile = new File(child, "pom.xml");
                        if (childPomFile.exists()) {
                            tmpProjectDir = child;
                            break;
                        }
                    }
                }
            }


            FileUtils.copyDirectory(tmpProjectDir, dummyProjectDir);
            FileUtils.deleteDirectory(tmpZipExtracted);
            tmpZip.delete();
        }
    }

    private static String getMvnwPath() {
        if (MigrationTool.isWindows) {
            return new File(dummyProjectDir(), "mvnw.cmd").getAbsolutePath();
        } else {
            return new File(dummyProjectDir(), "mvnw").getAbsolutePath();
        }
    }

    public static File createTempDirectory(String prefix, String suffix) throws IOException {
        extractDummyProject();
        File tmpDir = new File(dummyProjectDir(), "tmp");
        if (!tmpDir.exists()) {
            tmpDir.mkdir();
        }
        File out =  File.createTempFile(prefix, suffix, tmpDir);
        out.delete();
        out.mkdir();
        return out;
    }

    public int exec(String... args) throws IOException {

        extractDummyProject();
        String mvnw = getMvnwPath();
        new File(mvnw).setExecutable(true, false);
        ProcessBuilder pb = new ProcessBuilder(args);
        pb.command().add(0, mvnw);
        pb.directory(projectDir);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        if (output != null) {
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            byte[] buffer = new byte[1024];
            String line = null;
            while ((line = input.readLine()) != null) {
                output.append(line);
                output.flush();
            }
            output.close();
        }
        try {
            return p.waitFor();
        } catch (InterruptedException ex) {
            return 500;
        }

    }
}
