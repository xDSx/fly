package callcount.gui_web.model;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class UploadItem {

    private String name;
    private String jar;
    private String cls;
    private String pkgs;
    private String args;
    private CommonsMultipartFile fileData;

    public CommonsMultipartFile getFileData() {
        return fileData;
    }

    public void setFileData(CommonsMultipartFile fileData) {
        this.fileData = fileData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJar() {
        return jar;
    }

    public void setJar(String jar) {
        this.jar = jar;
    }

    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }

    public String getPkgs() {
        return pkgs;
    }

    public void setPkgs(String pkgs) {
        this.pkgs = pkgs;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }
}