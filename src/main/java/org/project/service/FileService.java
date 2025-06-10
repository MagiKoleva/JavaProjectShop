package org.project.service;

import org.project.data.Receipt;

import java.io.IOException;

public interface FileService {
    void serializeReceipt(String fileName, Receipt receipt) throws IOException;
    Receipt deserializeReceipt(String fileName) throws IOException, ClassNotFoundException;
}
