package com.hp.advantage.utils;

public interface IProgressListener {
    void transferred(long num);

    void setTotalSize(long contentLength);
}