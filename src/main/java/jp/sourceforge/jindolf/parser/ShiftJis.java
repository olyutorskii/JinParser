/*
 * Shift_JIS encoding utilities
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

import java.nio.charset.Charset;

/**
 * シフトJIS符号化ユーティリティ。
 * JIS X0208:1997 準拠。（Windows-31Jではない！）
 * @see <a href="http://www.iana.org/assignments/character-sets">
 * CHARACTER SETS</a>
 * @see <a href="http://ja.wikipedia.org/wiki/Shift_JIS">
 * Wikipedia: Shift_JIS</a>
 */
public final class ShiftJis{

    /** エンコード名。 */
    public static final String ENCODE_NAME = "Shift_JIS";
    /** SHift_JIS用Charsetインスタンス。 */
    public static final Charset CHARSET = Charset.forName(ENCODE_NAME);
    /** char1文字をエンコードした時の最大バイト数。 */
    public static final int MAX_BYTES_PER_CHAR = 2;

    /**
     * 任意のバイト値がシフトJISの1バイト目でありうるか否か判定する。
     * 文字集合の判定は行わない。
     * @param bval バイト値
     * @return シフトJISの1バイト目でありうるならtrue
     */
    public static boolean isShiftJIS1stByte(byte bval){
        if(   (byte)0x81 <= bval && bval <= (byte)0x9f
           || (byte)0xe0 <= bval && bval <= (byte)0xfc){
            return true;
        }
        return false;
    }

    /**
     * 任意のバイト値がシフトJISの2バイト目でありうるか否か判定する。
     * 文字集合の判定は行わない。
     * @param bval バイト値
     * @return シフトJISの2バイト目でありうるならtrue
     */
    public static boolean isShiftJIS2ndByte(byte bval){
        if(   (byte)0x40 <= bval && bval <= (byte)0x7e
           || (byte)0x80 <= bval && bval <= (byte)0xfc){
            return true;
        }
        return false;
    }

    /**
     * 任意のバイト値ペアがシフトJISでありうるか否か判定する。
     * 文字集合の判定は行わない。
     * @param b1st 第一バイト値
     * @param b2nd 第二バイト値
     * @return シフトJISならtrue
     */
    public static boolean isShiftJIS(byte b1st, byte b2nd){
        if(   ShiftJis.isShiftJIS1stByte(b1st)
           && ShiftJis.isShiftJIS2ndByte(b2nd)){
            return true;
        }
        return false;
    }

    /**
     * 隠しコンストラクタ。
     */
    private ShiftJis(){
        super();
        return;
    }

}
