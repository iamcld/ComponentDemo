package com.example.jucdemo;

import com.google.gson.annotations.SerializedName;

public class Status {
    private int errno;
    @SerializedName("raw_text")
    private String rawText;

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }
}


