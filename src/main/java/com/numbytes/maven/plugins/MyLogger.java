package com.numbytes.maven.plugins;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

class MyLogger {
    private Log mylogger = null;
    private List<String> loggerFile = new ArrayList<>();

    public MyLogger(Log log) {
        this.mylogger = log;
    }

    public void writeLoggerFile(String path) throws MojoExecutionException {
        File fileOutputFile = new File(path);
        try {
            if (this.mylogger != null) {
                this.mylogger.info("Writing logger file [" + fileOutputFile.getPath() + "]");
            }
            FileWriter fw = new FileWriter(path);
            for (Iterator<String> messages = this.loggerFile.iterator(); messages.hasNext();) {
                fw.write(messages.next());
                fw.write(System.lineSeparator());
            }
            fw.close();
        } catch (IOException e) {
            if (this.mylogger != null) {
                this.mylogger.error("Problem writing to the logger file (" + fileOutputFile.getPath() + ")");
                this.mylogger.error(e.getStackTrace().toString());
            }
            throw new MojoExecutionException("Problem writing to the logger file (" + fileOutputFile.getPath() + ")");
        }
    }

    public void info(String msg) {
        if (this.mylogger != null) {
            this.mylogger.info(msg);
        }
    }

    public void infoFile(String msg) {
        if (this.mylogger != null) {
            this.mylogger.info(msg);
        }
        loggerFile.add("INFO: " + msg);
    }

    public void debug(String msg) {
        if (this.mylogger != null) {
            this.mylogger.debug(msg);
        }
    }

    public void debugFile(String msg) {
        if (this.mylogger != null) {
            this.mylogger.debug(msg);
        }
        loggerFile.add("DEBUG: " + msg);
    }

    public void warn(String msg) {
        if (this.mylogger != null) {
            this.mylogger.warn(msg);
        }
    }

    public void warnFile(String msg) {
        if (this.mylogger != null) {
            this.mylogger.warn(msg);
        }
        loggerFile.add("WARN: " + msg);
    }

    public void error(String msg) {
        if (this.mylogger != null) {
            this.mylogger.error(msg);
        }
    }

    public void errorFile(String msg) {
        if (this.mylogger != null) {
            this.mylogger.error(msg);
        }
        loggerFile.add("ERROR: " + msg);
    }
}
