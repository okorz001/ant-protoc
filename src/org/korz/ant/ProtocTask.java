/*
 * Copyright (c) 2013, Oscar Korz <okorz001@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1)   Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *
 * 2)   Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.korz.ant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

public class ProtocTask extends Task {
    private boolean singleFile;
    private final List<File> files;

    private File protoPath;
    private File cppOut;
    private File javaOut;
    private File pythonOut;

    public ProtocTask() {
        this.files = new ArrayList<File>();
    }

    public void setSrcfile(File f) {
        this.singleFile = true;
        this.files.add(f);
    }

    public void setProtoPath(File f) {
        this.protoPath = f;
    }

    public void setCppOut(File f) {
        this.cppOut = f;
    }

    public void setJavaOut(File f) {
        this.javaOut = f;
    }

    public void setPythonOut(File f) {
        this.pythonOut = f;
    }

    public void addConfiguredFileset(FileSet fileset) {
        // sanity check
        if (this.singleFile) {
            throw new BuildException("cannot use nested filesets with " +
                    "srcfile attribute");
        }

        final DirectoryScanner scanner = fileset.getDirectoryScanner();
        for (final String s : scanner.getIncludedFiles()) {
            this.files.add(new File(scanner.getBasedir(), s));
        }
    }

    public void execute() {
        if (this.files.isEmpty()) {
            throw new BuildException("protoc task requires either " +
                    "srcfile attribute or nested filesets");
        }

        // Build argument vector.
        List<String> args = new ArrayList<String>();
        // TODO: find protoc
        args.add("protoc");

        if (this.protoPath != null) {
            args.add("--proto_path");
            args.add(this.protoPath.toString());
        }

        if (this.cppOut != null) {
            args.add("--cpp_out");
            args.add(this.cppOut.toString());
        }

        if (this.javaOut != null) {
            args.add("--java_out");
            args.add(this.javaOut.toString());
        }

        if (this.pythonOut != null) {
            args.add("--python_out");
            args.add(this.pythonOut.toString());
        }

        // TODO: Check filestamp dependencies.
        for (File f : this.files) {
            args.add(f.toString());
        }

        // Print some status.
        log("Compiling " + this.files.size() + " source files");
        if (this.cppOut != null) {
            log("Outputting C++ source to " + this.cppOut);
        }
        if (this.javaOut != null) {
            log("Outputting Java source to " + this.javaOut);
        }
        if (this.pythonOut != null) {
            log("Outputting Python source to " + this.pythonOut);
        }

        spawn(args);
    }

    private final void spawn(List<String> args) {
        String[] argv = new String[args.size()];
        args.toArray(argv);

        // The output formatting is inspired by javac task.
        log("Executing command:", Project.MSG_VERBOSE);
        for (String s : args) {
            log("'" + s + "'", Project.MSG_VERBOSE);
        }
        log("\nThe ' characters around the executable and arguments are\n" +
            "not part of the command.",
                Project.MSG_VERBOSE);

        try {
            final Process p = Runtime.getRuntime().exec(argv);
            if (p.waitFor() != 0) {
                Scanner scanner = new Scanner(p.getErrorStream());
                scanner.useDelimiter("\n");
                while (scanner.hasNext()) {
                    handleErrorOutput(scanner.next());
                }

                throw new BuildException("protoc failed");
            }
        } catch (IOException e) {
            throw new BuildException(e);
        } catch (InterruptedException e) {
            throw new BuildException(e);
        }
    }
}
