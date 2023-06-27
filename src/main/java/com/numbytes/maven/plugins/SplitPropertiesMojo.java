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

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Date;
import java.util.Properties;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

//import javax.json.Json;
//import javax.json.JsonObject;
//import javax.json.JsonObjectBuilder;
//import java.nio.file.Path;

/**
 * Generate files during build
 *
 * @author <a href="jose.pujante@numbytes.com">Pujante Jose Luis</a>
 * @since 1.0
 */
@Mojo(name = "splitproperties", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public class SplitPropertiesMojo extends AbstractMojo {
    /**
     * The base path for the source file/s
     *
     * @since 1.0
     */
    @Parameter(required = false)
    private String propertiesSourceBasePath;
    /**
     * The base path for the target file/s
     *
     * @since 1.0
     */
    @Parameter(required = false)
    private String propertiesTargetBasePath;
    /**
     * The base file to be merged
     *
     * @since 1.0
     */
    @Parameter(required = false)
    private String propertiesSourcePath;

    /**
     * The target file
     *
     * @since 1.0
     */
    @Parameter(required = false)
    private List<String> propertiesTargetPaths;

    /**
     * If the target file should be forced to be written
     *
     * @since 1.0
     */
    @Parameter(required = false, defaultValue = "false")
    private Boolean forceWriteOutput;

    /**
     * If the target file should be merged in mode 'incremental'
     *
     * @since 1.0
     */
    @Parameter(required = false, defaultValue = "false")
    private Boolean onlyIncremental;

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
    private String LOGGER_FILENAME = "PropertiesToJson-SplitProperties-" + dateFormat.format(new Date()) + ".log";

    public void execute() throws MojoExecutionException {
        mylog.info("+-----------------------------------------------+");
        mylog.info("| Executing the properties-to-json-plugin       |");
        mylog.info("| Action: Properties -> Properties (splitted)   |");
        mylog.info("| Author: jose.pujante@numbytes.com |");
        mylog.info("+-----------------------------------------------+");

        if (propFileSets != null && propFileSets.size() > 0) {
            for (PropFileSet propFileSet : propFileSets) {
                String propertiesSourceBasePath = propFileSet.getPropertiesSourceBasePath();
                String propertiesTargetBasePath = propFileSet.getPropertiesTargetBasePath();
                String propertiesSourcePath = propFileSet.getPropertiesSourcePath();
                List<String> propertiesTargetPaths = propFileSet.getPropertiesTargetPaths();
                Boolean forceWriteOutput = propFileSet.getForceWriteOutput();
                Boolean onlyIncremental = propFileSet.getOnlyIncremental();
                List<String> langs = propFileSet.getLangs();

                if (propertiesSourceBasePath != null &&
                        propertiesTargetBasePath != null &&
                        propertiesSourcePath != null &
                                propertiesTargetPaths.size() != 0) {

                    for (Iterator<String> ptps = propertiesTargetPaths.iterator(); ptps.hasNext();) {
                        String propertiesTargetPath = ptps.next();

                        processFindAndMergePropertiesForLanguage(propertiesSourceBasePath,
                                propertiesTargetBasePath,
                                propertiesSourcePath,
                                propertiesTargetPath,
                                forceWriteOutput,
                                onlyIncremental,
                                null);

                        for (Iterator<String> i = langs.iterator(); i.hasNext();) {
                            String lang = i.next();
                            processFindAndMergePropertiesForLanguage(propertiesSourceBasePath,
                                    propertiesTargetBasePath,
                                    propertiesSourcePath,
                                    propertiesTargetPath,
                                    forceWriteOutput,
                                    onlyIncremental,
                                    lang);
                        }
                    }
                } else {
                    mylog.error("Invalid PropFileSet");
                    mylog.error("propertiesSourceBasePath: " + propertiesSourceBasePath);
                    mylog.error("propertiesTargetBasePath: " + propertiesTargetBasePath);
                    mylog.error("propertiesSourcePath: " + propertiesSourcePath);
                    mylog.error("propertiesTargetPaths: " + propertiesTargetPaths);
                }
                mylog.info("");
            }
        } else if (propertiesSourceBasePath != null &&
                propertiesTargetBasePath != null &&
                propertiesSourcePath != null &&
                propertiesTargetPaths.size() != 0) {

            for (Iterator<String> ptps = propertiesTargetPaths.iterator(); ptps.hasNext();) {
                String propertiesTargetPath = ptps.next();

                processFindAndMergePropertiesForLanguage(propertiesSourceBasePath,
                        propertiesTargetBasePath,
                        propertiesSourcePath,
                        propertiesTargetPath,
                        forceWriteOutput,
                        onlyIncremental,
                        null);

                for (Iterator<String> i = langs.iterator(); i.hasNext();) {
                    String lang = i.next();
                    processFindAndMergePropertiesForLanguage(propertiesSourceBasePath,
                            propertiesTargetBasePath,
                            propertiesSourcePath,
                            propertiesTargetPath,
                            forceWriteOutput,
                            onlyIncremental,
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
        comments += "Author: jose.pujante@numbytes.com";
        return comments;
    }

    private void processFindAndMergePropertiesForLanguage(String propertiesSourceBasePath,
                                                          String propertiesTargetBasePath,
                                                          String propertiesSourcePath,
                                                          String propertiesTargetPath,
                                                          Boolean forceWriteOutput,
                                                          Boolean onlyIncremental,
                                                          String lang) throws MojoExecutionException {

        // propertiesTargetPathCurrentLang => File_lang_<LANG_CODE>.properties
        String propertiesTargetPathCurrentLang = propertiesTargetPath;
        if (lang != null) {
            propertiesTargetPathCurrentLang = PropertiesUtil.getNewPropertiesFilenameForLanguage(propertiesTargetPath, lang);
            File f = new File(propertiesTargetPathCurrentLang);
            mylog.infoFile("Generating Properties for language [" + lang + "]" + " --> [" + f.getName() + "]");
        } else {
            File f = new File(propertiesTargetPathCurrentLang);
            mylog.infoFile("Generating Properties for default language --> [" + f.getName() + "]");
        }
        mylog.infoFile("Forced to overwrite target file --> " + forceWriteOutput);
        mylog.infoFile("Incremental mode --> " + onlyIncremental);

        File propTrgtBaseCurrentLang = new File(propertiesTargetBasePath + File.separator + propertiesTargetPathCurrentLang);
        Properties trgtPropertiesCurrentLang = PropertiesUtil.readPropertiesFile(mylog, propTrgtBaseCurrentLang.getAbsolutePath());
        Properties newProperties = (Properties) trgtPropertiesCurrentLang.clone();

        // propertiesSourcePathCurrentLang => NbsAngularV2_<LANG_CODE>.properties
        String propertiesSourcePathCurrentLang = propertiesSourcePath;
        if (lang != null) {
            propertiesSourcePathCurrentLang = PropertiesUtil.getNewPropertiesFilenameForLanguage(propertiesSourcePath, lang);
        }
        File propSrcBaseCurrentLang = new File(propertiesSourceBasePath + File.separator + propertiesSourcePathCurrentLang);
        if (!propSrcBaseCurrentLang.exists()) {
            mylog.warn("Source properties file (" + propSrcBaseCurrentLang.getName() + ") NOT found");
            return;
        }

        // It is necessary to take into account the properties keys in the default language and the right value in the
        // current language file
        // propsSrc => NbsAngularV2_<LANG>.properties (values)
        // propsTrgt => File_lang.properties (keys)
        // propsTrgtCurrentLang (newProperties) => File_lang_<LANG>.properties (target)
        File propTrgtBase = new File(propertiesTargetBasePath + File.separator + propertiesTargetPath);
        Properties propsSrc = PropertiesUtil.readPropertiesFile(mylog, propSrcBaseCurrentLang.getAbsolutePath());
        Properties propsTrgt = PropertiesUtil.readPropertiesFile(mylog, propTrgtBase.getAbsolutePath());

        // Merging between File_lang.properties => File_lang_<LANG>.properties
        if (! onlyIncremental ) {
            Properties mergeCurrentLang = processMergeProperties(propsTrgt, newProperties);
            if (mergeCurrentLang != null) {
                newProperties = mergeCurrentLang;
            }
        }

        // Merging between NbsAngularV2.properties => File_lang_<LANG>.properties
        if (! onlyIncremental ) {
            Properties mergeResult = processFindAndMergeProperties(propsSrc, newProperties);
            if (mergeResult != null) { newProperties = mergeResult; }
        } else {
            Properties mergeResult = processFindAndMergeProperties(propsSrc, newProperties);
            if (mergeResult != null) { newProperties = mergeResult; }
        }
//        Properties mergeResult = processFindAndMergeProperties(propsSrc, newProperties);
//        if (mergeResult != null) { newProperties = mergeResult; }

        if (!PropertiesUtil.equalsProperties(mylog, newProperties, trgtPropertiesCurrentLang) || forceWriteOutput) {
            PropertiesUtil.writePropertiesFileUnicodeEscaped(mylog, generateLogHeaderComments(), propTrgtBaseCurrentLang, newProperties);
        } else {
            mylog.info("No results has been generated for the properties file");
        }
    }

    private Properties processMergeProperties(Properties propertiesSrc, Properties propertiesTrgt)
            throws MojoExecutionException {
        Properties propsMerged = PropertiesUtil.mergeProperties(mylog, propertiesTrgt, propertiesSrc);
        if (PropertiesUtil.equalsProperties(null, propsMerged, propertiesTrgt)) {
            return null;
        }
        return propsMerged;
    }

    private Properties processFindAndMergeProperties(Properties propertiesSrc, Properties propertiesTrgt)
            throws MojoExecutionException {
        Properties propsMerged = PropertiesUtil.mergeExistantProperties(mylog, propertiesTrgt, propertiesSrc);
        if (PropertiesUtil.equalsProperties(null, propsMerged, propertiesTrgt)) {
            return null;
        }
        return propsMerged;
    }
}
