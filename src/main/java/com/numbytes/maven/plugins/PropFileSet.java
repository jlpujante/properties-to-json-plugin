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

import java.util.List;

/**
 * @author <a href="jose.pujante@numbytes.com">Pujante Jose Luis</a>
 * @since 1.0
 */
public class PropFileSet {

    private String propertiesSourceBasePath = "";
    private String propertiesTargetBasePath = "";
    private List<String> propertiesSourcePaths;
    private String propertiesSourcePath;
    private List<String> propertiesTargetPaths;
    private String propertiesTargetPath;
    private Boolean createPropertiesTarget = false;
    private Boolean unicodeEscaped = true;
    private Boolean forceWriteOutput = true;
    private Boolean onlyIncremental = false;
    private List<String> langs;

    public String getPropertiesSourceBasePath() {
        return propertiesSourceBasePath;
    }

    public void setPropertiesSourceBasePath(String propertiesSourceBasePath) {
        this.propertiesSourceBasePath = propertiesSourceBasePath;
    }

    public String getPropertiesTargetBasePath() {
        return propertiesTargetBasePath;
    }

    public void setPropertiesTargetBasePath(String propertiesTargetBasePath) {
        this.propertiesTargetBasePath = propertiesTargetBasePath;
    }

    public List<String> getPropertiesSourcePaths() {
        return propertiesSourcePaths;
    }

    public void setPropertiesSourcePaths(List<String> propertiesSourcePaths) {
        this.propertiesSourcePaths = propertiesSourcePaths;
    }

    public String getPropertiesSourcePath() {
        return propertiesSourcePath;
    }

    public void setPropertiesSourcePath(String propertiesSourcePath) {
        this.propertiesSourcePath = propertiesSourcePath;
    }

    public List<String> getPropertiesTargetPaths() {
        return propertiesTargetPaths;
    }

    public void setPropertiesTargetPaths(List<String> propertiesTargetPaths) {
        this.propertiesTargetPaths = propertiesTargetPaths;
    }

    public String getPropertiesTargetPath() {
        return propertiesTargetPath;
    }

    public void setPropertiesTargetPath(String propertiesTargetPath) {
        this.propertiesTargetPath = propertiesTargetPath;
    }

    public Boolean getCreatePropertiesTarget() {
        return createPropertiesTarget;
    }

    public void setCreatePropertiesTarget(Boolean createPropertiesTarget) {
        this.createPropertiesTarget = createPropertiesTarget;
    }

    public Boolean getUnicodeEscaped() {
        return unicodeEscaped;
    }

    public void setUnicodeEscaped(Boolean unicodeEscaped) {
        this.unicodeEscaped = unicodeEscaped;
    }

    public Boolean getForceWriteOutput() {
        return forceWriteOutput;
    }

    public void setForceWriteOutput(Boolean forceWriteOutput) {
        this.forceWriteOutput = forceWriteOutput;
    }

    public Boolean getOnlyIncremental() {
        return onlyIncremental;
    }

    public void setOnlyIncremental(Boolean onlyIncremental) {
        this.onlyIncremental = onlyIncremental;
    }

    public List<String> getLangs() {
        return langs;
    }

    public void setLangs(List<String> langs) {
        this.langs = langs;
    }
}
