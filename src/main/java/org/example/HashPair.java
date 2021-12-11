package org.example;

public class HashPair {
    private final String hash;
    private final String deleteHash;

    public HashPair(String hash, String deleteHash) {
        this.hash = hash;
        this.deleteHash = deleteHash;
    }

    public String getHash() {
        return hash;
    }

    public String getDeleteHash() {
        return deleteHash;
    }
}
