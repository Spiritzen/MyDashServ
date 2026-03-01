package com.afci.training.planning.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "afci.upload")
public class UploadProperties {

    /** Ex: D:/afci-uploads */
    private String root;

    /** Ex: ${root}/users */
    private String usersDir;

    /** Ex: ${root}/evidences */
    private String evidencesDir;

    public String getRoot() { return root; }
    public void setRoot(String root) { this.root = root; }
    public String getUsersDir() { return usersDir; }
    public void setUsersDir(String usersDir) { this.usersDir = usersDir; }
    public String getEvidencesDir() { return evidencesDir; }
    public void setEvidencesDir(String evidencesDir) { this.evidencesDir = evidencesDir; }
}
