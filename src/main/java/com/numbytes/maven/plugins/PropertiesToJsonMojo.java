package com.numbytes.maven.plugins;
/*
 * The MIT License
 *
 * Copyright (c) 2004
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.*;
import java.util.List;
import javax.json.*;
import java.util.Properties;
import java.util.Enumeration;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.sonatype.plexus.build.incremental.BuildContext;
import org.codehaus.plexus.util.FileUtils;


/**
 * Generate files during build
 *
 * @author <a href="jose.pujante@numbytes.com">Pujante Jose Luis</a>
 * @since 1.0
 */
@Mojo(name = "propertiestojson", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public class PropertiesToJsonMojo extends AbstractMojo {
    /**
     * The file which has to be parsed
     *
     * @since 1.0
     */
    @Parameter(required = false)
    private File propertiesSourcePath;
    /**
     * The target file to which the file should be copied (this shouldn't be a directory but a file which does or does not exist)
     *
     * @since 1.0
     */
    @Parameter(required = false)
    private File jsonTargetPath;
    /**
     * The base file to be merged
     *
     * @since 1.0
     */
    @Parameter(required = false)
    private File propertiesToMergePath;

    /**
     * The key used to identify the asset
     *
     * @since 1.0
     */
    @Parameter(required = false)
    private File propertiesKey;

    /**
     * The asset name
     *
     * @since 1.0
     */
    @Parameter(required = false)
    private String assetName;
    /**
     * The asset value
     *
     * @since 1.0
     */
    @Parameter(required = false)
    private String assetValue;
    /**
     * The target file with the properties unified by key
     *
     * @since 1.0
     */
    @Parameter(required = false)
    private File propertiesUnifiedPath;
    /**
     * The target file with the properties unified by key
     *
     * @since 1.0
     */
    @Parameter(required = false)
    private File propertiesUnifiedJsPath;

    /**
     * Collection of FileSets to work on
     *
     * @since 1.0
     */
    @Parameter(required = false)
    private List<FileSet> fileSets;

    /**
     * Overwrite files
     *
     * @since 1.0
     */
    @Parameter(property = "copy.overWrite", defaultValue = "true")
    boolean overWrite;

    /**
     * Ignore File Not Found errors during incremental build
     *
     * @since 1.0
     */
    @Parameter(property = "copy.ignoreFileNotFoundOnIncremental", defaultValue = "true")
    boolean ignoreFileNotFoundOnIncremental;

    /**
     * The assets file with the results
     *
     * @since 1.0
     */
    @Parameter(property = "copy.fileOutput", defaultValue = "properties.json")
    private String fileOutput;

    /**
     * The assets file with the results
     *
     * @since 1.0
     */
    @Parameter(property = "copy.fileJsOutput", defaultValue = "properties.js")
    private String fileJsOutput;

    /**
     * The name of the application/portlet
     *
     * @since 1.0
     */
    @Parameter(property = "copy.appContext")
    private String appContext;

    /**
     * The assets file with the results
     *
     * @since 1.0
     */
    private JsonObjectBuilder assetsBuilder;

    /**
     * Results of unification
     *
     * @since 1.0
     */
    private JsonObjectBuilder unifyAssetsBuilder;

    /**
     * @since 1.0
     */
    @Component
    private MavenProject project;

    @Component
    private BuildContext buildContext;

    private MyLogger mylog = new MyLogger(getLog());

    public void execute() throws MojoExecutionException {
        mylog.info("+-----------------------------------------------+");
        mylog.info("| Executing the properties-to-json-plugin       |");
        mylog.info("| Action: Properties -> JSON                    |");
        mylog.info("| Author: jose.pujante@numbytes.com |");
        mylog.info("+-----------------------------------------------+");

        if (fileSets != null && fileSets.size() > 0) {
            assetsBuilder = Json.createObjectBuilder();
            unifyAssetsBuilder = Json.createObjectBuilder();
            for (FileSet fileSet : fileSets) {
                File propertiesSrcPath = fileSet.getPropertiesSourcePath();
                File jsonTrgtPath = fileSet.getJsonTargetPath();
                File propToMergePath = fileSet.getPropertiesToMergePath();
                String propKey = fileSet.getPropertiesKey();
                String assetName = fileSet.getAssetName();
                String assetValue = fileSet.getAssetValue();
                if (propertiesSrcPath != null && jsonTrgtPath != null && propToMergePath != null
                        && assetName != null && assetValue != null) {
                    JsonObject result = processPropertiesFile(propertiesSrcPath, jsonTrgtPath, propToMergePath);
                    if (result != null) {
                        if (propKey != null) {
                            mylog.info("Unifying properties using key [" + propKey + "]");
                            unifyAssetsBuilder.add(propKey, result);
                        }
                    } else {
                        mylog.warn("No results has been generated for the properties file (" + propertiesSrcPath.getPath() + ")");
                    }
                    assetsBuilder.add(assetName, assetValue);
                } else {
                    mylog.error("Invalid FileSet");
                    mylog.error("propertiesSourcePath: " + propertiesSrcPath);
                    mylog.error("jsonTargetPath: " + jsonTrgtPath);
                    mylog.error("propertiesToMergePath: " + propToMergePath);
                    mylog.error("assetName: " + assetName);
                    mylog.error("assetValue: " + assetValue);
                }
                mylog.info("");
            }
            writeUnifiedFile(unifyAssetsBuilder);
        } else if (propertiesSourcePath != null && jsonTargetPath != null && propertiesToMergePath != null
                && assetName != null && assetValue != null) {
            processPropertiesFile(propertiesSourcePath, jsonTargetPath, propertiesToMergePath);
            assetsBuilder = Json.createObjectBuilder().add(assetName, assetValue);
        } else {
            mylog.info("No Files to process");
        }
    }

    private void writeUnifiedFile(JsonObjectBuilder content) throws MojoExecutionException {
        JsonObject propertiesJson = content.build();
        String unifiedJsonContent = propertiesJson.toString();
        if (propertiesUnifiedPath != null) {
            writeFile(propertiesUnifiedPath.getAbsolutePath(), unifiedJsonContent);
        } else {
            mylog.info("Properties unified JSON file does not generated. 'propertiesUnifiedPath' attribute does not found in POM file..");
        }
        if (propertiesUnifiedJsPath != null) {
            // String jsContent = "LANGUAGES_INFO=" + unifiedJsonContent + ";";
            String jsContent = "";
            jsContent += "if (typeof(LANGUAGES_INFO) != 'undefined') {";
            jsContent += "LANGUAGES_INFO['" + appContext + "']=" + unifiedJsonContent + ";";
            jsContent += "if (window.hasOwnProperty('DEBUG') || (window.hasOwnProperty('NBS_SETTING') && NBS_SETTING.DEBUG)) {";
            jsContent += "console.log('Adding properties for context:' + '" + appContext + "');";
            jsContent += "}";
            jsContent += "} else {";
            jsContent += "LANGUAGES_INFO={};";
            jsContent += "LANGUAGES_INFO['" + appContext + "']=" + unifiedJsonContent + ";";
            jsContent += "if (window.hasOwnProperty('DEBUG') || (window.hasOwnProperty('NBS_SETTING') && NBS_SETTING.DEBUG)) {";
            jsContent += "console.log('Creating properties for context:' + '" + appContext + "');";
            jsContent += "}";
            jsContent += "}";
            writeFile(propertiesUnifiedJsPath.getAbsolutePath(), jsContent);
        } else {
            mylog.info("Properties unified JSON file does not generated. 'propertiesUnifiedJsPath' attribute does not found in POM file..");
        }
    }

    private void writeFile(String destinationPath, String content) throws MojoExecutionException {
        try {
            mylog.info("Writing file [" + destinationPath + "]");
            File fileOutputFile = new File(destinationPath);
            FileOutputStream is = new FileOutputStream(fileOutputFile);
            OutputStreamWriter osw = new OutputStreamWriter(is);
            Writer w = new BufferedWriter(osw);
            w.write(content);
            w.close();
        } catch (IOException e) {
            mylog.error("Problem writing to the file (" + destinationPath + ")");
            mylog.error(e.getStackTrace().toString());
        }
    }

    private JsonObject processPropertiesFile(File propertiesSrcPath, File jsonTrgtPath, File propToMergePath)
            throws MojoExecutionException {
        if (!propertiesSrcPath.exists()) {
            if (ignoreFileNotFoundOnIncremental && buildContext.isIncremental()) {
                mylog.warn("propertiesSrcPath " + propertiesSrcPath.getAbsolutePath() + " not found during incremental build");
            } else {
                mylog.error("propertiesSrcPath " + propertiesSrcPath.getAbsolutePath() + " does not exist");
            }
        } else if (propertiesSrcPath.isDirectory()) {
            mylog.error("propertiesSrcPath " + propertiesSrcPath.getAbsolutePath() + " is not a file");
        } else if (jsonTrgtPath == null) {
            mylog.error("jsonTrgtPath not specified");
        } else if (jsonTrgtPath.exists() && jsonTrgtPath.isFile() && !overWrite) {
            mylog.error(jsonTrgtPath.getAbsolutePath() + " already exists and overWrite not set");
        } else {
            try {
                if (buildContext.isIncremental() && jsonTrgtPath.exists() && !buildContext.hasDelta(propertiesSrcPath) && FileUtils.contentEquals(propertiesSrcPath, jsonTrgtPath)) {
                    mylog.info("No changes detected in " + propertiesSrcPath.getAbsolutePath());
                    return null;
                }

                String propertiesJsonContent;
                JsonObjectBuilder propertiesJsonBuilder = Json.createObjectBuilder();
                Properties props = PropertiesUtil.readPropertiesFile(mylog, propertiesSrcPath.getAbsolutePath());
                Properties propsBase = PropertiesUtil.readPropertiesFile(mylog, propToMergePath.getAbsolutePath());
                Properties propsMerged = PropertiesUtil.mergeProperties(mylog, propsBase, props);
                Enumeration e = propsMerged.propertyNames();
                while (e.hasMoreElements()) {
                    String key = (String) e.nextElement();
                    propertiesJsonBuilder.add(key, propsMerged.getProperty(key));
                }
                JsonObject propertiesJson = propertiesJsonBuilder.build();
                propertiesJsonContent = propertiesJson.toString();
                writeFile(jsonTrgtPath.getPath(), propertiesJsonContent);

                buildContext.refresh(jsonTrgtPath);
                return propertiesJson;
            } catch (IOException e) {
                throw new MojoExecutionException("could not copy " + propertiesSrcPath.getAbsolutePath() + " to " + jsonTrgtPath.getAbsolutePath());
            }
        }
        return null;
    }
}
