package ru.gb.nfs.server.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class FileSent implements Serializable {

    private static final long serialVersionUID = 1l;

    private String fileName;

    private long fileSize;

    private byte[] fileData;
}
