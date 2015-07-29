package scriptism;

import java.io.File;

public class Options {
    private int exitStatus;
    private boolean shouldExit;
    private File script;
    private boolean writeClassfile;
    private String[] args = new String[0];

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

    public File getScript() {
        return script;
    }

    public void setScript(File script) {
        this.script = script;
    }

    public boolean getWriteClassfile() {
        return writeClassfile;
    }

    public boolean isWriteClassfile() {
        return writeClassfile;
    }

    public void setWriteClassfile(boolean writeClassfile) {
        this.writeClassfile = writeClassfile;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }
}
