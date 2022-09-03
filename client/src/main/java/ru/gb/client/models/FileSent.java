package ru.gb.client.models;


import java.io.Serial;
import java.io.Serializable;

public class FileSent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1l;

    private String fileName;

    private long fileSize;

    private byte[] fileData;

    public FileSent() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }
}
