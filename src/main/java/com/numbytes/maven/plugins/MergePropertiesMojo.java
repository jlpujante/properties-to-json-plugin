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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.sonatype.plexus.build.incremental.BuildContext;

//import javax.json.Json;
//import javax.json.JsonObject;
//import javax.json.JsonObjectBuilder;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Generate files during build
 *
 * @author <a href="jose.pujante@numbytes.com">Pujante Jose Luis</a>
 * @since 1.0
 */
@Mojo(name = "mergeproperties", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public class MergePropertiesMojo extends AbstractMojo {
    /**
     * The base path for the source file/s
     *
     * @since 1.0
     */
    @Parameter(required = false, defaultValue = "")
    private String propertiesSourceBasePath;
    /**
     * The base path for the target file/s
     *
     * @since 1.0
     */
    @Parameter(required = false, defaultValue = "")
    private String propertiesTargetBasePath;
    /**
     * The base file to be merged
     *
     * @since 1.0
     */
    @Parameter(required = false)
    private List<String> propertiesSourcePaths;

    /**
     * The target file
     *
     * @since 1.0
     */
    @Parameter(required = false)
    private String propertiesTargetPath;

    /**
     * If the target file should be created or not
     *
     * @since 1.0
     */
    @Parameter(required = false, defaultValue = "false")
    private Boolean createPropertiesTarget;

    /**
     * If the target file should be unicode escaped or not
     *
     * @since 1.0
     */
    @Parameter(required = false, defaultValue = "true")
    private Boolean unicodeEscaped;

    /**
     * If the target file should be forced to be written
     *
     * @since 1.0
     */
    @Parameter(required = false, defaultValue = "false")
    private Boolean forceWriteOutput;

    /**
     * If the log files should be generated
     *
     * @since 1.0
     */
    @Parameter(required = false, defaultValue = "false")
    private Boolean generateLogs;

    /**
     * Collection of languages available
     *
     * @since 1.0
     */
    @Parameter(required = false)
    private List<String> langs;

    /**
     * Collection of PropFileSets
     *
     * @since 1.0
     */
    @Parameter(required = false)
    private List<PropFileSet> propFileSets;

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
     * @since 1.0
     */
    @Component
    private MavenProject project;

    @Component
    private BuildContext buildContext;

    private MyLogger mylog = new MyLogger(getLog());

    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
    private String LOGGER_FILENAME = "PropertiesToJson-MergeProperties-" + dateFormat.format(new Date()) + ".log";

    public void execute() throws MojoExecutionException {
        mylog.info("+-----------------------------------------------+");
        mylog.info("| Executing the properties-to-json-plugin       |");
        mylog.info("| Action: Properties -> Properties (merged)     |");
        mylog.info("| Author: jose.pujante@numbytes.com |");
        mylog.info("+-----------------------------------------------+");

        if (propFileSets != null && propFileSets.size() > 0) {
            for (PropFileSet propFileSet : propFileSets) {
                String propertiesSourceBasePath = propFileSet.getPropertiesSourceBasePath();
                String propertiesTargetBasePath = propFileSet.getPropertiesTargetBasePath();
                List<String> propertiesSourcePaths = propFileSet.getPropertiesSourcePaths();
                String propertiesTargetPath = propFileSet.getPropertiesTargetPath();
                Boolean createPropertiesTarget = propFileSet.getCreatePropertiesTarget();
                Boolean unicodeEscaped = propFileSet.getUnicodeEscaped();
                List<String> langs = propFileSet.getLangs();

                if (propertiesSourceBasePath != null &&
                        propertiesTargetBasePath != null &&
                        propertiesSourcePaths.size() != 0 &
                        propertiesTargetPath != null) {

                    processMergePropertiesForLanguage(propertiesSourceBasePath,
                            propertiesTargetBasePath,
                            propertiesSourcePaths,
                            propertiesTargetPath,
                            createPropertiesTarget,
                            unicodeEscaped,
                            forceWriteOutput,
                            null);

                    if (langs != null) {
                        for (Iterator<String> i = langs.iterator(); i.hasNext();) {
                            String lang = i.next();
                            processMergePropertiesForLanguage(propertiesSourceBasePath,
                                    propertiesTargetBasePath,
                                    propertiesSourcePaths,
                                    propertiesTargetPath,
                                    createPropertiesTarget,
                                    unicodeEscaped,
                                    forceWriteOutput,
                                    lang);
                        }
                    }
                } else {
                    mylog.error("Invalid PropFileSet");
                    mylog.error("propertiesSourceBasePath: " + propertiesSourceBasePath);
                    mylog.error("propertiesTargetBasePath: " + propertiesTargetBasePath);
                    mylog.error("propertiesSourcePaths: " + propertiesSourcePaths);
                    mylog.error("propertiesTargetPath: " + propertiesTargetPath);
                    mylog.error("createPropertiesTarget: " + createPropertiesTarget);
                    mylog.error("unicodeEscaped: " + unicodeEscaped);
                }
                mylog.info("");
            }
        } else if (propertiesSourceBasePath != null &&
                propertiesTargetBasePath != null &&
                propertiesSourcePaths.size() != 0 &&
                propertiesTargetPath != null) {

            processMergePropertiesForLanguage(propertiesSourceBasePath,
                    propertiesTargetBasePath,
                    propertiesSourcePaths,
                    propertiesTargetPath,
                    createPropertiesTarget,
                    unicodeEscaped,
                    forceWriteOutput,
                    null);
            if (langs != null) {
                for (Iterator<String> i = langs.iterator(); i.hasNext();) {
                    String lang = i.next();
                    processMergePropertiesForLanguage(propertiesSourceBasePath,
                            propertiesTargetBasePath,
                            propertiesSourcePaths,
                            propertiesTargetPath,
                            createPropertiesTarget,
                            unicodeEscaped,
                            forceWriteOutput,
                            lang);
                }
            }
        } else {
            mylog.info("No Files to process");
        }

        if (generateLogs) {
            mylog.writeLoggerFile(LOGGER_FILENAME);
        } else {
            mylog.info("Log files not generated.");
        }
    }

    private String generateLogHeaderComments() {
        String comments = "Autogenerated by Maven-Properties-To-Json-Plugin\n";
        comments += "Author: jose.pujante@numbytes.com\n";
        comments += "This file can be modified, but it will be updated automatically";
        return comments;
    }

    private void processMergePropertiesForLanguage(String propertiesSourceBasePath,
                                                   String propertiesTargetBasePath,
                                                   List<String> propertiesSourcePaths,
                                                   String propertiesTargetPath,
                                                   Boolean createPropertiesTarget,
                                                   Boolean unicodeEscaped,
                                                   Boolean forceWriteOutput,
                                                   String lang) throws MojoExecutionException {

        String propertiesTargetPathCurrentLang = propertiesTargetPath;
        if (lang != null) {
            propertiesTargetPathCurrentLang = PropertiesUtil.getNewPropertiesFilenameForLanguage(propertiesTargetPath, lang);
            mylog.infoFile("Generating Properties for language [" + lang + "]" + " --> [" + propertiesTargetPathCurrentLang + "]");
            mylog.infoFile("Create Target File for language [" + lang + "]" + " --> " + createPropertiesTarget);
            mylog.infoFile("Escape unicode for language [" + lang + "]" + " --> " + unicodeEscaped);
            mylog.infoFile("Forced to overwrite target file [" + lang + "]" + " --> " + forceWriteOutput);

        } else {
            mylog.infoFile("Generating Properties for default language --> [" + propertiesTargetPath + "]");
            mylog.infoFile("Create Target File for default language --> " + createPropertiesTarget);
            mylog.infoFile("Escape unicode for default language --> " + unicodeEscaped);
            mylog.infoFile("Forced to overwrite target file for default language --> " + forceWriteOutput);
        }

        File propTrgtBaseCurrentLang = new File(propertiesTargetBasePath + File.separator + propertiesTargetPathCurrentLang);
        Properties trgtPropertiesCurrentLang = new Properties();
        Properties newProperties;
        if (!createPropertiesTarget) {
            trgtPropertiesCurrentLang = PropertiesUtil.readPropertiesFile(mylog, propTrgtBaseCurrentLang.getAbsolutePath());
            // Remove all the properties stating by 'angular_'
            newProperties = PropertiesUtil.removePropertiesBySubstr(mylog, trgtPropertiesCurrentLang, "angular_");
        } else {
            newProperties = (Properties) trgtPropertiesCurrentLang.clone();
            File propTrgtCurrentLangParentFolder = new File(propTrgtBaseCurrentLang.getParentFile().getAbsolutePath());
            if (! propTrgtCurrentLangParentFolder.exists()) {
                if (propTrgtCurrentLangParentFolder.mkdirs()) {
                    mylog.infoFile("Target parent folder (" + propTrgtCurrentLangParentFolder.getAbsolutePath() + ") has been created");
                }
            }
        }

        Properties mergeResult = null;
        for (Iterator<String> i = propertiesSourcePaths.iterator(); i.hasNext();) {
            String propertiesSrcPath = i.next();
            if (lang != null) {
                propertiesSrcPath = PropertiesUtil.getNewPropertiesFilenameForLanguage(propertiesSrcPath, lang);
            }
            File propSrcBase = new File(propertiesSourceBasePath + File.separator + propertiesSrcPath);
            Properties props = PropertiesUtil.readPropertiesFile(mylog, propSrcBase.getAbsolutePath());
            mergeResult = processMergeProperties(props, newProperties);
            if (mergeResult != null) { newProperties = mergeResult; }
        }

        // Comparison between the current language file with the default language to avoid missing keys
        // Normally we are adding the keys in the default language file and forget to added into the rest ones.
        // This step will generate a merged file between the current language and the default language avoiding
        // missing keys
        if (lang != null) {
            File propTrgtBase = new File(propertiesTargetBasePath + File.separator + propertiesTargetPath);
            Properties trgtProperties = PropertiesUtil.readPropertiesFile(mylog, propTrgtBase.getAbsolutePath());
            mylog.infoFile("Merging [" + propTrgtBase.getName() + "] into [" + propTrgtBaseCurrentLang.getName());
            mergeResult = processMergeOnlyNotFoundProperties(trgtProperties, newProperties);
            if (mergeResult != null) { newProperties = mergeResult; }
        }

        if (!PropertiesUtil.equalsProperties(mylog, newProperties, trgtPropertiesCurrentLang) || forceWriteOutput) {
            if (unicodeEscaped) {
                PropertiesUtil.writePropertiesFileUnicodeEscaped(mylog, generateLogHeaderComments(), propTrgtBaseCurrentLang, newProperties);
            } else {
                PropertiesUtil.writePropertiesFileUnicode(mylog, generateLogHeaderComments(), propTrgtBaseCurrentLang, newProperties);
            }
        } else {
            mylog.info("No results has been generated for the properties file");
        }
    }

    private Properties processMergeOnlyNotFoundProperties(Properties propertiesSrc, Properties propertiesTrgt)
            throws MojoExecutionException {
        Properties propsMerged = PropertiesUtil.mergeNonExistantProperties(mylog, propertiesTrgt, propertiesSrc);
        if (PropertiesUtil.equalsProperties(null, propsMerged, propertiesTrgt)) {
            return null;
        }
        return propsMerged;
    }

    private Properties processMergeProperties(Properties propertiesSrc, Properties propertiesTrgt)
        throws MojoExecutionException {
        Properties propsMerged = PropertiesUtil.mergeProperties(mylog, propertiesTrgt, propertiesSrc);
        if (PropertiesUtil.equalsProperties(null, propsMerged, propertiesTrgt)) {
            return null;
        }
        return propsMerged;
    }
}
