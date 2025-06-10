package org.project.service.impl;

import org.project.data.Receipt;
import org.project.service.FileService;

import java.io.*;

public class FileServiceImpl implements FileService {
    @Override
    public void serializeReceipt(String fileName, Receipt receipt) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(receipt);
        }
    }

    @Override
    public Receipt deserializeReceipt(String fileName) throws IOException, ClassNotFoundException {
        Receipt receipt = null;
        try (FileInputStream fileInputStream = new FileInputStream(fileName);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            receipt = (Receipt) objectInputStream.readObject();
        }

        return receipt;
    }
}
