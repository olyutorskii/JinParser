/*
 * invalid Shift_JIS decoding information
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.osdn.jindolf.parser.content;

import java.util.Comparator;

/**
 * 不正な Shift_JIS デコードの情報。
 * 1バイトもしくは2バイトで構成される。
 * 1バイトの場合はおそらくエンコーディングに関するエラー。
 * 2バイトの場合はおそらく文字集合に関するエラー。
 */
public class DecodeErrorInfo{

    /** 出現位置順Comparator。 */
    public static final Comparator<DecodeErrorInfo> POS_COMPARATOR =
            new PosComparator();

    private final int charPos;
    private final boolean has2ndFlag;
    private final byte rawByte1st;
    private final byte rawByte2nd;

    /**
     * 下請けコンストラクタ。
     * @param charPos デコードエラーで置き換えられた文字列の開始位置
     * @param has2ndFlag 2バイト目が有効ならtrueを渡す。
     * @param rawByte1st デコードエラーを引き起こした最初のバイト値
     * @param rawByte2nd デコードエラーを引き起こした2番目のバイト値
     * @throws IndexOutOfBoundsException charPosが負
     */
    private DecodeErrorInfo(int charPos,
                              boolean has2ndFlag,
                              byte rawByte1st,
                              byte rawByte2nd)
            throws IndexOutOfBoundsException{
        if(charPos < 0) throw new IndexOutOfBoundsException();

        this.charPos = charPos;
        this.has2ndFlag = has2ndFlag;
        this.rawByte1st = rawByte1st;
        this.rawByte2nd = rawByte2nd;

        return;
    }

    /**
     * コンストラクタ。
     * @param charPos デコードエラーで置き換えられた文字列の開始位置
     * @param rawByte1st デコードエラーを引き起こした最初のバイト値
     * @param rawByte2nd デコードエラーを引き起こした2番目のバイト値
     * @throws IndexOutOfBoundsException charPosが負
     */
    public DecodeErrorInfo(int charPos,
                             byte rawByte1st,
                             byte rawByte2nd)
            throws IndexOutOfBoundsException{
        this(charPos, true, rawByte1st, rawByte2nd);
        return;
    }

    /**
     * コンストラクタ。
     * @param charPos デコードエラーで置き換えられた文字列の開始位置
     * @param rawByte1st デコードエラーを引き起こしたバイト値
     * @throws IndexOutOfBoundsException charPosが負
     */
    public DecodeErrorInfo(int charPos,
                             byte rawByte1st)
            throws IndexOutOfBoundsException{
        this(charPos, false, rawByte1st, (byte) 0x00);
        return;
    }

    /**
     * デコードエラーで置き換えられた文字列の開始位置を返す。
     * @return デコードエラーで置き換えられた文字列の開始位置
     */
    public int getCharPosition(){
        return this.charPos;
    }

    /**
     * 2バイト目の情報を持つか判定する。
     * @return 2バイト目の情報を持つならtrue
     */
    public boolean has2nd(){
        return this.has2ndFlag;
    }

    /**
     * 1バイト目の値を返す。
     * @return 1バイト目の値
     */
    public byte getRawByte1st(){
        return this.rawByte1st;
    }

    /**
     * 2バイト目の値を返す。
     * @return 2バイト目の値
     * @throws IllegalStateException 2バイト目の情報を把持していないとき
     */
    public byte getRawByte2nd() throws IllegalStateException{
        if( ! this.has2ndFlag ) throw new IllegalStateException();
        return this.rawByte2nd;
    }

    /**
     * 出現位置のみが違う複製オブジェクトを生成する。
     * @param gap 出現位置から引きたい値。正の値なら文字開始位置に向かう。
     * @return 複製オブジェクト
     * @throws IndexOutOfBoundsException 再計算された出現位置が負
     */
    public DecodeErrorInfo createGappedClone(int gap)
            throws IndexOutOfBoundsException{
        DecodeErrorInfo result;

        int newPos = this.charPos - gap;
        if(this.has2ndFlag){
            result = new DecodeErrorInfo(newPos,
                                         this.rawByte1st, this.rawByte2nd);
        }else{
            result = new DecodeErrorInfo(newPos, this.rawByte1st);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();

        result.append("start:").append(this.charPos).append(' ');

        String hex;
        hex = Integer.toHexString(this.rawByte1st & 0xff);
        if(hex.length() <= 1) result.append('0');
        result.append(hex);

        if(this.has2ndFlag){
            hex = Integer.toHexString(this.rawByte2nd & 0xff);
            result.append(':');
            if(hex.length() <= 1) result.append('0');
            result.append(hex);
        }

        return result.toString();
    }

    /**
     * 出現位置で順序づける比較子。
     */
    private static final class PosComparator
            implements Comparator<DecodeErrorInfo> {

        /**
         * コンストラクタ。
         */
        PosComparator(){
            super();
            return;
        }

        /**
         * {@inheritDoc}
         * @param info1 {@inheritDoc}
         * @param info2 {@inheritDoc}
         * @return {@inheritDoc}
         */
        @Override
        public int compare(DecodeErrorInfo info1, DecodeErrorInfo info2){
            int pos1;
            int pos2;

            if(info1 == null) pos1 = -1;
            else              pos1 = info1.charPos;

            if(info2 == null) pos2 = -1;
            else              pos2 = info2.charPos;

            return pos1 - pos2;
        }

    }

}
