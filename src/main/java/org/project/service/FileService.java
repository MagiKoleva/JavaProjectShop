package org.project.service;

import org.project.data.Receipt;
import org.project.data.Store;

import java.io.IOException;

public interface FileService {

    void issueReceipt(Receipt receipt, Store store) throws IOException;
    void serializeReceipt(Receipt receipt) throws IOException;
    Receipt deserializeReceipt(String fileName) throws IOException, ClassNotFoundException;
}
