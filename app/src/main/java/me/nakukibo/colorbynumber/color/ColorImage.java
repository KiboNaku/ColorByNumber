package me.nakukibo.colorbynumber.color;

public class ColorImage {

    private String fileName;
    private String imageName;

    public ColorImage(String fileName) {
        this.fileName = fileName;
        this.imageName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
