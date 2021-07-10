package com.fa.msci.conf;

public class MsciConfig {
    private String rootSrcFolderPath;
    private String workspacePath;

    private String sshUserName;
    private String sshIp;
    private String sshTargetPath;

    public String getSshUserName() {
        return sshUserName;
    }

    public void setSshUserName(String sshUserName) {
        this.sshUserName = sshUserName;
    }

    public String getSshIp() {
        return sshIp;
    }

    public void setSshIp(String sshIp) {
        this.sshIp = sshIp;
    }

    public String getSshTargetPath() {
        return sshTargetPath;
    }

    public void setSshTargetPath(String sshTargetPath) {
        this.sshTargetPath = sshTargetPath;
    }

    public String getRootSrcFolderPath() {
        return rootSrcFolderPath;
    }

    public void setRootSrcFolderPath(String rootSrcFolderPath) {
        this.rootSrcFolderPath = rootSrcFolderPath;
    }

    public String getWorkspacePath() {
        return workspacePath;
    }

    public void setWorkspacePath(String workspacePath) {
        this.workspacePath = workspacePath;
    }


}
