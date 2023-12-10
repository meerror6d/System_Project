package com.example.basic;

public class block_chain {

    private String blockNo;
    private String prevHash;
    private String curHash;
    private String properties;

    public block_chain() {
    }

    public block_chain(String blockNo,String prevHash, String curHash,String properties) {
        this.blockNo = blockNo;
        this.prevHash = prevHash;
        this.curHash = curHash;
        this.properties = properties;
    }

    public String getBlockNo() {
        return blockNo;
    }

    public void setBlockNo(String blockNo) {
        this.blockNo = blockNo;
    }

    public String getPrevHash() {
        return prevHash;
    }

    public void setPrevHash(String prevHash) {
        this.prevHash = prevHash;
    }

    public String getCurHash() {
        return curHash;
    }

    public void setCurHash(String curHash) {
        this.curHash = curHash;
    }

    public String getProperties() { return properties;}

    public void setProperties(String properties) {
        this.properties = properties;
    }
}
