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

import org.apache.maven.plugin.MojoExecutionException;

import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Enumeration;

/**
 * @author <a href="jose.pujante@numbytes.com">Pujante Jose Luis</a>
 * @since 1.0
 */
public class PropertiesUtil {

    public static Properties readPropertiesFile(MyLogger logger, String path) throws MojoExecutionException {
        try {
            if (logger != null) {
                logger.info("Reading properties file (" + path + ")");
            }
            InputStream input = new FileInputStream(path);
            Properties props = new Properties();
            props.load(input);
            return props;
        } catch (IOException ex) {
            throw new MojoExecutionException("could not read properties file (" + path + ")");
        }
    }

    public static Properties removePropertiesBySubstr(MyLogger logger, Properties properties, String text)  {
        Properties result = new Properties();
        Enumeration props = properties.propertyNames();
        int keysRemoved = 0;
        while (props.hasMoreElements()) {
            String propertyKey = (String) props.nextElement();
            if (! propertyKey.toLowerCase().contains(text.toLowerCase())) {
                result.setProperty(propertyKey, properties.getProperty(propertyKey));
            } else {
                keysRemoved += 1;
            }
        }
        logger.info("Properties keys removed = " + keysRemoved);
        return result;
    }

    private static Properties mergeAllProperties(MyLogger logger, Properties propertiesBase, Properties propertiesToMerge, Boolean keysExistant, Boolean keysNonExistant) {
        Properties result = (Properties) propertiesBase.clone();
        Enumeration eProps = propertiesToMerge.propertyNames();
        boolean found;
        while (eProps.hasMoreElements()) {
            String propertyKey = (String) eProps.nextElement();
            found = false;
            Enumeration eBase = propertiesBase.propertyNames();
            while (eBase.hasMoreElements()) {
                String baseKey = (String) eBase.nextElement();
                if (baseKey.equals(propertyKey)) {
                    found = true;
                    if (propertiesBase.getProperty(baseKey).equals(propertiesToMerge.getProperty(propertyKey))) {
                        result.setProperty(baseKey, propertiesToMerge.getProperty(propertyKey));
                    } else {
                        logger.warnFile(baseKey + " = " + propertiesBase.getProperty(baseKey));
                        logger.warnFile(propertyKey + " = " + propertiesToMerge.getProperty(propertyKey));
                        if (keysExistant) {
                            result.setProperty(baseKey, propertiesToMerge.getProperty(propertyKey));
                            if (logger != null) {
                                logger.warnFile("Key [" + baseKey + "] exists in both properties file with different value. Assigning new value");
                            }
                        }
                    }
                    break;
                }
            }
            if (!found) {
                if (keysNonExistant) {
                    result.setProperty(propertyKey, propertiesToMerge.getProperty(propertyKey));
                    if (logger != null) {
                        logger.warnFile("Key [" + propertyKey + "] was not found.");
                    }
                }
            }
        }
        return result;
    }

    public static Properties mergeProperties(MyLogger logger, Properties propertiesBase, Properties propertiesToMerge) {
        return PropertiesUtil.mergeAllProperties(logger, propertiesBase, propertiesToMerge, true, true);
    }

    public static Properties mergeExistantProperties(MyLogger logger, Properties propertiesBase, Properties propertiesToMerge) {
        return PropertiesUtil.mergeAllProperties(logger, propertiesBase, propertiesToMerge, true, false);
    }

    public static Properties mergeNonExistantProperties(MyLogger logger, Properties propertiesBase, Properties propertiesToMerge) {
        return PropertiesUtil.mergeAllProperties(logger, propertiesBase, propertiesToMerge, false, true);
    }

    public static Boolean equalsProperties(MyLogger logger, Properties propertiesBase, Properties propertiesToMerge) {
        Enumeration eBase = propertiesBase.propertyNames();
        Enumeration eProps = propertiesToMerge.propertyNames();
        List<String> listBase = Collections.list(eBase);
        List<String> listProps = Collections.list(eProps);
        if (listBase.size() != listProps.size()) {
            if (logger != null) {
                logger.info("Properties sizes are different");
            }
            return false;
        }
        boolean found;

        eProps = propertiesToMerge.propertyNames();
        while (eProps.hasMoreElements()) {
            String propertyKey = (String) eProps.nextElement();
//            if (logger != null) {
//                logger.info("comparing [" + propertyKey + "]");
//            }
            found = false;
            eBase = propertiesBase.propertyNames();
            while (eBase.hasMoreElements()) {
                String baseKey = (String) eBase.nextElement();
                if (baseKey.equals(propertyKey)) {
                    if (logger != null) {
                        logger.info("   comparing [" + baseKey + "] === [" + propertyKey + "]");
                    }
                    if (propertiesBase.getProperty(baseKey).equals(propertiesToMerge.getProperty(propertyKey))) {
                        if (logger != null) {
                            logger.info("   comparing [" + propertiesBase.getProperty(baseKey) + "] === [" + propertiesToMerge.getProperty(propertyKey) + "]");
                        }
                        found = true;
                    }
                    break;
                }
            }
            if (!found) {
//                if (logger != null) {
//                    logger.info("---------- [" + propertyKey + "]");
//                }
                return false;
            }
        }
        return true;
    }

    public static void writePropertiesFileUnicodeEscaped(MyLogger logger, String comments, File fileOutputFile, Properties properties) throws MojoExecutionException {
        PropertiesUtil.writePropertiesFile(logger, comments, fileOutputFile, properties, true);
    }
    public static void writePropertiesFileUnicode(MyLogger logger, String comments, File fileOutputFile, Properties properties) throws MojoExecutionException {
        PropertiesUtil.writePropertiesFile(logger, comments, fileOutputFile, properties, false);
    }
    private static void writePropertiesFile(MyLogger logger, String comments, File fileOutputFile, Properties properties, Boolean escaped) throws MojoExecutionException {
        try {
            if (logger != null) {
                logger.info("Writing file [" + fileOutputFile.getPath() + "]");
            }
            FileOutputStream fos = new FileOutputStream(fileOutputFile);
            SortedProperties sortProperties = new SortedProperties().loadFromProperties(properties);
            if (!escaped) {
                OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.ISO_8859_1);
                sortProperties.store(osw, comments);
            } else {
                sortProperties.store(fos, comments);
            }
            fos.flush();
            fos.close();
        } catch (IOException e) {
            if (logger != null) {
                logger.error("Problem writing to the file (" + fileOutputFile.getPath() + ")");
                logger.error(e.getStackTrace().toString());
            }
            throw new MojoExecutionException("Problem writing to the file (" + fileOutputFile.getPath() + ")");
        }
    }

    public static String getNewPropertiesFilenameForLanguage(String path, String lang) {
        return path.replaceAll(".properties", "_" + lang + ".properties");
    }

}
