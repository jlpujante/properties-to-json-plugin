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

import java.io.File;

/**
 * @author <a href="jose.pujante@numbytes.com">Pujante Jose Luis</a>
 * @since 1.0
 */
public class FileSet {

    private File propertiesSourcePath;
    private File jsonTargetPath;
    private File propertiesToMergePath;
    private String assetName;
    private String assetValue;
    private String propertiesKey;

    public File getPropertiesSourcePath() {
        return propertiesSourcePath;
    }

    public void setPropertiesSourcePath(File propertiesSourcePath) {
        this.propertiesSourcePath = propertiesSourcePath;
    }

    public File getJsonTargetPath() {
        return jsonTargetPath;
    }

    public void setJsonTargetPath(File jsonTargetPath) {
        this.jsonTargetPath = jsonTargetPath;
    }

    public File getPropertiesToMergePath() {
        return propertiesToMergePath;
    }

    public void setPropertiesToMergePath(File propertiesToMergePath) {
        this.propertiesToMergePath = propertiesToMergePath;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetValue() {
        return assetValue;
    }

    public void setAssetValue(String assetValue) {
        this.assetValue = assetValue;
    }

    public String getPropertiesKey() {
        return propertiesKey;
    }

    public void setPropertiesKey(String propertiesKey) {
        this.propertiesKey = propertiesKey;
    }
}
