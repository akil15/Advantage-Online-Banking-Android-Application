package com.hp.advantage.utils;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class CountingOutputStream extends FilterOutputStream {

    private final IProgressListener listener;
    private long transferred;

    public CountingOutputStream(final OutputStream out, final IProgressListener listener) {
        super(out);
        this.listener = listener;
        this.transferred = 0;
    }

    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
        this.transferred += len;
        if (this.listener != null) {
            this.listener.transferred(this.transferred);
        }
    }

    public void write(int b) throws IOException {
        out.write(b);
        this.transferred++;
        if (this.listener != null) {
            this.listener.transferred(this.transferred);
        }
    }
}
