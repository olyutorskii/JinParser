/*
 * invalid character decoding information
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.osdn.jindolf.parser.content;

import java.util.Comparator;
import java.util.List;

/**
 * 文字デコード異常系の発生により
 * {@link DecodedContent}に代替文字とエラーバイトが埋め込まれた事実を
 * 記録する。
 *
 * <p>エラーの原因となったバイト値に関する情報は、
 * 1バイトもしくは2バイトで構成される。
 * 2バイトの場合はおそらくシフトJISの文字集合に関するエラー。
 *
 * <p>{@link DecodedContent}内での代替文字出現位置をchar単位で保持する。
 */
public class DecodeErrorInfo{

    /** 出現位置順Comparator。 */
    public static final Comparator<DecodeErrorInfo> POS_COMPARATOR =
            new PosComparator();

    private static final int BSEARCH_THRESHOLD = 16;


    private final int charPos;
    private final boolean has2ndFlag;
    private final byte rawByte1st;
    private final byte rawByte2nd;


    /**
     * 下請けコンストラクタ。
     *
     * @param charPos デコードエラーで置き換えられた代替文字の出現位置
     * @param has2ndFlag 2バイト目が有効ならtrueを渡す。
     * @param rawByte1st デコードエラーを引き起こした1番目のバイト値
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
     *
     * @param charPos デコードエラーで置き換えられた代替文字の出現位置
     * @param rawByte1st デコードエラーを引き起こした1番目のバイト値
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
     *
     * @param charPos デコードエラーで置き換えられた代替文字の出現位置
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
     * 与えられた文字位置を含むか、またはそれ以降で最も小さな位置情報を持つ
     * デコードエラーのインデックス位置を返す。※リニアサーチ版。
     *
     * @param errList デコードエラーのリスト
     * @param startPos 文字位置
     * @return 0から始まるリスト内の位置。
     *     文字位置の一致するデコードエラーがなければ
     *     リストへの挿入ポイントが返る。
     */
    public static int lsearchErrorIndex(List<DecodeErrorInfo> errList,
                                        int startPos){
        // assert errList instanceof RandomAccess;

        int listSize = errList.size();

        int idx;
        for(idx = 0; idx < listSize; idx++){
            DecodeErrorInfo einfo = errList.get(idx);
            int errPos = einfo.getCharPosition();
            if(startPos <= errPos) break;
        }

        return idx;
    }

    /**
     * 与えられた文字位置を含むか、またはそれ以降で最も小さな位置情報を持つ
     * デコードエラーのインデックス位置を返す。※バイナリサーチ版。
     *
     * @param errList デコードエラーのリスト
     * @param startPos 文字位置
     * @return 0から始まるリスト内の位置。
     *     文字位置の一致するデコードエラーがなければ
     *     リストへの挿入ポイントが返る。
     */
    public static int bsearchErrorIndex(List<DecodeErrorInfo> errList,
                                        int startPos){
        // assert errList instanceof RandomAccess;

        int floor = 0;
        int roof  = errList.size() - 1;

        while(floor <= roof){
            int gapHalf = (roof - floor) / 2;  // 切り捨て
            int midpoint = floor + gapHalf;
            DecodeErrorInfo einfo = errList.get(midpoint);
            int errPos = einfo.getCharPosition();

            if     (errPos < startPos) floor = midpoint + 1;
            else if(errPos > startPos) roof  = midpoint - 1;
            else return midpoint;
        }

        return floor;
    }

    /**
     * 与えられた文字位置を含むか、またはそれ以降で最も小さな位置情報を持つ
     * デコードエラーのインデックス位置を返す。
     *
     * <p>要素数の増減に応じてリニアサーチとバイナリサーチを使い分ける。
     *
     * @param errList デコードエラーのリスト
     * @param startPos 文字位置
     * @return 0から始まるリスト内の位置。
     *     文字位置の一致するデコードエラーがなければ
     *     リストへの挿入ポイントが返る。
     */
    public static int searchErrorIndex(List<DecodeErrorInfo> errList,
                                       int startPos){
        int result;

        int listSize = errList.size();
        if(listSize < BSEARCH_THRESHOLD){
            // linear-search
            result = lsearchErrorIndex(errList, startPos);
        }else{
            // binary-search
            result = bsearchErrorIndex(errList, startPos);
        }

        return result;
    }


    /**
     * エラー代替文字の出現位置を返す。
     *
     * @return 代替文字の開始位置
     */
    public int getCharPosition(){
        return this.charPos;
    }

    /**
     * 2バイト目のエラー情報を持つか判定する。
     *
     * @return 2バイト目の情報を持つならtrue
     */
    public boolean has2nd(){
        return this.has2ndFlag;
    }

    /**
     * 1バイト目のエラーバイト値を返す。
     *
     * @return 1バイト目の値
     */
    public byte getRawByte1st(){
        return this.rawByte1st;
    }

    /**
     * 2バイト目のエラーバイト値を返す。
     *
     * @return 2バイト目の値
     * @throws IllegalStateException 2バイト目の情報を把持していないとき
     */
    public byte getRawByte2nd() throws IllegalStateException{
        if( ! this.has2ndFlag ) throw new IllegalStateException();
        return this.rawByte2nd;
    }

    /**
     * 出現位置のみが違う複製オブジェクトを生成する。
     *
     * @param gap 出現位置から引きたい値。正の値なら文字開始位置に向かう。
     * @return 複製オブジェクト
     * @throws IndexOutOfBoundsException 再計算された出現位置が負
     */
    public DecodeErrorInfo createGappedClone(int gap)
            throws IndexOutOfBoundsException{
        int newPos = this.charPos - gap;

        DecodeErrorInfo result;
        result = new DecodeErrorInfo(newPos,
                                     this.has2ndFlag,
                                     this.rawByte1st, this.rawByte2nd);

        return result;
    }

    /**
     * {@inheritDoc}
     *
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
            result.append(':');
            hex = Integer.toHexString(this.rawByte2nd & 0xff);
            if(hex.length() <= 1) result.append('0');
            result.append(hex);
        }

        return result.toString();
    }

    /**
     * 出現位置で順序づける比較子。
     */
    @SuppressWarnings("serial")
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
         *
         * @param info1 {@inheritDoc}
         * @param info2 {@inheritDoc}
         * @return {@inheritDoc}
         */
        @Override
        public int compare(DecodeErrorInfo info1, DecodeErrorInfo info2){
            if     (info1 == info2) return 0;
            else if(info1 == null)  return -1;
            else if(info2 == null)  return 1;

            int pos1 = info1.charPos;
            int pos2 = info2.charPos;

            int result;
            if     (pos1 < pos2) result = -1;
            else if(pos1 > pos2) result = 1;
            else                 result = 0;

            return result;
        }

    }

}
