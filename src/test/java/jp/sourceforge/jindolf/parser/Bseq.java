/*
 */

package jp.sourceforge.jindolf.parser;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * テスト用byte列シーケンスを生成する。
 */
class Bseq {

    private Bseq(){
    }

    static ByteArrayInputStream byteStream(int... array) {
        byte[] ba = new byte[array.length];
        int idx = 0;
        for (int iVal : array) {
            byte bVal = (byte) (iVal & 255);
            ba[idx++] = bVal;
        }
        return new ByteArrayInputStream(ba);
    }

    static ByteArrayInputStream byteIs(CharSequence seq) {
        byte[] bs = byteArray(seq);
        ByteArrayInputStream result = new ByteArrayInputStream(bs);
        return result;
    }

    static byte[] byteArray(CharSequence seq) {
        byte[] result;
        List<Byte> byteList = new ArrayList<>();
        int length = seq.length();
        for (int pos = 0; pos < length; pos++) {
            int val = 0;
            char ch = seq.charAt(pos);
            if ('0' <= ch && ch <= '9') {
                val += ch - '0';
            } else if ('a' <= ch && ch <= 'f') {
                val += ch - 'a' + 10;
            } else if ('A' <= ch && ch <= 'F') {
                val += ch - 'A' + 10;
            } else {
                continue;
            }
            pos++;
            if (pos >= length) {
                break;
            }
            val *= 16;
            ch = seq.charAt(pos);
            if ('0' <= ch && ch <= '9') {
                val += ch - '0';
            } else if ('a' <= ch && ch <= 'f') {
                val += ch - 'a' + 10;
            } else if ('A' <= ch && ch <= 'F') {
                val += ch - 'A' + 10;
            } else {
                continue;
            }
            byteList.add((byte) val);
        }
        result = new byte[byteList.size()];
        for (int pos = 0; pos < result.length; pos++) {
            result[pos] = byteList.get(pos);
        }
        return result;
    }

}
