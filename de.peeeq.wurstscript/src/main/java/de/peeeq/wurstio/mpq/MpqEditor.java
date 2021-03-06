package de.peeeq.wurstio.mpq;

import java.io.Closeable;
import java.io.File;

public interface MpqEditor extends Closeable {

    boolean canWrite();

    byte[] extractFile(String fileToExtract) throws Exception;

    void insertFile(String filenameInMpq, byte[] contents) throws Exception;

    void insertFile(String filenameInMpq, File contents) throws Exception;

    void deleteFile(String filenameInMpq) throws Exception;

    boolean hasFile(String fileName) throws Exception;

    void setKeepHeaderOffset(boolean flag);
}
