/*
 * License : The MIT License
 * Copyright(c) 2018 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

import io.bitbucket.olyutorskii.jiocema.CharDecodeListener;
import io.bitbucket.olyutorskii.jiocema.DecodeBreakException;
import java.nio.charset.CharsetDecoder;

/**
 * Test listener for {@link CharDecodeListener}
 */
class TestListener implements CharDecodeListener{

    private final StringBuilder text = new StringBuilder();


    @Override
    public void startDecoding(CharsetDecoder decoder)
            throws DecodeBreakException {
        append("[ST]");
        return;
    }

    @Override
    public void endDecoding() throws DecodeBreakException {
        append("[EN]");
        return;
    }

    @Override
    public void charContent(char[] charArray, int offset, int length)
            throws DecodeBreakException {
        append("[CH]");
        this.text.append(charArray, offset, length);
        return;
    }

    @Override
    public void rawBytes(byte[] byteArray, int offset, int length)
            throws DecodeBreakException {
        // NOTHING
        return;
    }

    @Override
    public void malformedError(byte[] errorArray, int offset, int length)
            throws DecodeBreakException {
        append("[ME]");
        dumpHex(errorArray, offset, length);
        return;
    }

    @Override
    public void unmapError(byte[] errorArray, int offset, int length)
            throws DecodeBreakException {
        append("[UE]");
        dumpHex(errorArray, offset, length);
        return;
    }

    protected void append(CharSequence seq){
        this.text.append(seq);
        return;
    }

    protected void dumpHex(byte[] errorArray, int offset, int length){
        for(int ct = 0; ct < length; ct++){
            dumpHex(errorArray[offset + ct]);
        }
        return;
    }

    private void dumpHex(byte bVal){
        int val = bVal & 0xff;
        if(val <= 0xf) this.text.append('0');
        append(Integer.toHexString(val));
        return;
    }

    public void clear(){
        text.setLength(0);
        return;
    }

    @Override
    public String toString(){
        return text.toString();
    }

}
