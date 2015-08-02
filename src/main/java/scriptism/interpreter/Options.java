package scriptism.interpreter;

import java.io.File;

public class Options {
    private int exitStatus;
    private boolean shouldExit;
    private File script;
    private boolean writeClassfile;
    private String[] args = new String[0];
    private String scriptName = "ScriptClass";

    public boolean getShouldExit() {
        return shouldExit;
    }

    public void shouldExit(int exitStatus) {
        this.shouldExit = true;
        this.exitStatus = exitStatus;
    }

    public int getExitStatus() {
        return exitStatus;
    }

    public void setExitStatus(int exitStatus) {
        this.exitStatus = exitStatus;
    }

    public Options withExitStatus(int exitStatus) {
        setExitStatus(exitStatus);
        return this;
    }

    public File getScript() {
        return script;
    }

    public void setScript(File script) {
        this.script = script;
    }

    public Options withScript(File file) {
        setScript(file);
        return this;
    }

    public boolean getWriteClassfile() {
        return writeClassfile;
    }

    public void setWriteClassfile(boolean writeClassfile) {
        this.writeClassfile = writeClassfile;
    }

    public Options withWriteClassfile(boolean writeClassfile) {
        setWriteClassfile(writeClassfile);
        return this;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public Options withArgs(String[] args) {
        setArgs(args);
        return this;
    }

    public String getScriptName() {
        if (script != null && script.getName().length() > 0) {
            String name = script.getName();
            char[] n = new char[name.length()];
            if (Character.isJavaIdentifierStart(name.charAt(0))) {
                n[0] = name.charAt(0);
            } else {
                n[0] = '_';
            }
            for (int i = 0; i < name.length(); i++) {
                if (Character.isJavaIdentifierPart(name.charAt(i))) {
                    n[i] = name.charAt(i);
                } else {
                    n[i] = '_';
                }
            }
            return new String(n);
        } else {
            return scriptName;
        }
    }
}
