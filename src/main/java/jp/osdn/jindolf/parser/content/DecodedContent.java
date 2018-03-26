/*
 * decoded source
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.osdn.jindolf.parser.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

/**
 * デコードエラー情報を含む再利用可能な文字列。
 *
 * <p>デコードエラーを起こした箇所は代替文字{@link #ALTCHAR}で置き換えられる。
 *
 * <p>マルチスレッドには非対応。
 */
public class DecodedContent
        implements CharSequence,
                   Appendable {

    /**
     * 代替文字。
     *
     * <p>{@literal HTMLで使うなら < や > や & や " や ' は避けた方が無難}
     */
    public static final char ALTCHAR = '?';

    private static final String NULLTEXT = "null";

    private static final List<DecodeErrorInfo> EMPTY_LIST;

    static{
        List<DecodeErrorInfo> emptyList;
        emptyList = Collections.emptyList();
        emptyList = Collections.unmodifiableList(emptyList);
        EMPTY_LIST = emptyList;
    }


    private final StringBuilder rawContent = new StringBuilder();

    private List<DecodeErrorInfo> decodeError;


    /**
     * コンストラクタ。
     *
     * <p>長さ0の文字列が反映され、デコードエラー総数は0件となる。
     */
    public DecodedContent(){
        super();
        initImpl();
        return;
    }

    /**
     * コンストラクタ。
     *
     * <p>引数の文字列が反映され、デコードエラー総数は0件となる。
     *
     * <p>nullが渡されると文字列"null"として解釈される。
     *
     * @param seq 初期化文字列
     */
    public DecodedContent(CharSequence seq){
        super();
        initImpl();
        this.rawContent.append(seq);
        return;
    }

    /**
     * コンストラクタ。
     *
     * <p>長さ0の文字列が反映され、デコードエラー総数は0件となる。
     *
     * <p>文字数の初期容量を引数で指定する。
     * 文字列長が初期容量を超えるまでの間、
     * 文字列格納の再割り当てが起こらないことが期待される。
     *
     * @param capacity 文字数の初期容量
     * @throws NegativeArraySizeException 容量が負の値
     */
    public DecodedContent(int capacity) throws NegativeArraySizeException{
        super();
        if(capacity < 0) throw new NegativeArraySizeException();
        initImpl();
        this.rawContent.ensureCapacity(capacity);
        return;
    }


    /**
     * ギャップ情報が加味されたデコードエラー情報を、
     * 範囲指定込みで指定エラーリストに追加コピーする。
     *
     * <p>追加先エラーリストがnullだった場合、
     * 必要に応じてエラーリストが生成され
     * 戻り値となる場合がありうる。
     *
     * @param srcErrList 追加元エラーリスト
     * @param startCharPos 範囲開始位置
     * @param endCharPos 範囲終了位置
     * @param dstErrList 追加先エラーリスト。nullでもよい。
     *     追加元と異なるリストでなければならない。
     * @param gap ギャップ量
     * @return 引数targetErrorもしくは新規生成されたリストを返す。
     *     なにもコピーされなければnullを返す。
     * @throws IllegalArgumentException 追加元リストと追加先リストが
     *     同一インスタンス
     */
    protected static List<DecodeErrorInfo>
            appendGappedErrorInfo(List<DecodeErrorInfo> srcErrList,
                                  int startCharPos, int endCharPos,
                                  List<DecodeErrorInfo> dstErrList,
                                  int gap){
        if(srcErrList == dstErrList) throw new IllegalArgumentException();

        if(startCharPos >= endCharPos) return dstErrList;

        int errSize = srcErrList.size();

        int startErrorIdx;
        int endErrorIdx;

        startErrorIdx =
                DecodeErrorInfo.searchErrorIndex(srcErrList, startCharPos);
        if(startErrorIdx >= errSize){
            return null;
        }

        int lastCharPos = endCharPos - 1;
        endErrorIdx =
                DecodeErrorInfo.searchErrorIndex(srcErrList, lastCharPos);
        if(endErrorIdx >= errSize){
            endErrorIdx = errSize - 1;
        }else{
            DecodeErrorInfo lastErrorInfo = srcErrList.get(endErrorIdx);
            boolean isLastErrorInfoOnLastPos =
                    lastErrorInfo.getCharPosition() == lastCharPos;
            if( ! isLastErrorInfoOnLastPos){
                endErrorIdx--;
            }
        }

        boolean hasLoop =
                (0 <= startErrorIdx) && (startErrorIdx <= endErrorIdx);
        if( ! hasLoop){
            return null;
        }

        List<DecodeErrorInfo> result;
        if(dstErrList == null) result = createErrorList();
        else                   result = dstErrList;

        copyGappedErrorInfo(srcErrList,
                            startErrorIdx, endErrorIdx,
                            result, gap);
        return result;
    }

    /**
     * エラーリストの一部の範囲を、gapを加味して別リストに追加コピーする。
     *
     * @param srcErrList コピー元リスト
     * @param startErrorIdx コピー元の範囲開始インデックス
     * @param endErrorIdx コピー元の範囲終了インデックス
     * @param dstErrList コピー先リスト
     * @param gap 代替文字出現位置ギャップ量
     */
    private static void
            copyGappedErrorInfo(List<DecodeErrorInfo> srcErrList,
                                int startErrorIdx, int endErrorIdx,
                                List<DecodeErrorInfo> dstErrList,
                                int gap){
        for(int index = startErrorIdx; index <= endErrorIdx; index++){
            DecodeErrorInfo srcErrInfo = srcErrList.get(index);
            DecodeErrorInfo gappedInfo = srcErrInfo.createGappedClone(gap);
            dstErrList.add(gappedInfo);
        }
        return;
    }

    /**
     * エラー格納用リストを生成する。
     *
     * @return リスト
     */
    private static List<DecodeErrorInfo> createErrorList(){
        List<DecodeErrorInfo> result = new ArrayList<>();
        return result;
    }

    static{
        assert createErrorList() instanceof RandomAccess;
    }


    /**
     * 初期化。
     *
     * <p>長さ0の文字列＆デコードエラー無しの状態になる。
     * コンストラクタで新インスタンスを作るより低コスト。
     */
    public void init(){
        initImpl();
        return;
    }

    /**
     * 初期化下請け。
     *
     * <p>長さ0の文字列＆デコードエラー無しの状態になる。
     */
    private void initImpl(){
        this.rawContent.setLength(0);

        if(this.decodeError != null){
            this.decodeError.clear();
        }

        return;
    }

    /**
     * 事前にキャパシティを確保する。
     *
     * <p>指定されたキャパシティの範囲内で再割り当てが起きないことを保証する。
     *
     * @param minimumCapacity キャラクタ単位のキャパシティ長。
     */
    public void ensureCapacity(int minimumCapacity){
        this.rawContent.ensureCapacity(minimumCapacity);
        return;
    }

    /**
     * デコードエラーを含むか否か判定する。
     *
     * @return デコードエラーを含むならtrue
     */
    public boolean hasDecodeError(){
        if(this.decodeError == null) return false;
        if(this.decodeError.isEmpty()) return false;
        return true;
    }

    /**
     * デコードエラーの一覧を取得する。
     *
     * @return デコードエラーの一覧
     */
    public List<DecodeErrorInfo> getDecodeErrorList(){
        if( ! hasDecodeError() ){
            return EMPTY_LIST;
        }
        return Collections.unmodifiableList(this.decodeError);
    }

    /**
     * 生の文字列を得る。
     *
     * <p>高速なCharSequenceアクセス用途。
     *
     * @return 生の文字列。
     */
    public CharSequence getRawContent(){
        return this.rawContent;
    }

    /**
     * 指定された位置の文字を変更する。
     *
     * <p>デコードエラーにより追加された代替文字の変更も可能。
     *
     * @param index 文字位置
     * @param ch 文字
     * @throws IndexOutOfBoundsException 不正な位置指定
     */
    public void setCharAt(int index, char ch)
            throws IndexOutOfBoundsException{
        this.rawContent.setCharAt(index, ch);
        return;
    }

    /**
     * {@inheritDoc}
     *
     * @param index {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public char charAt(int index){
        return this.rawContent.charAt(index);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int length(){
        return this.rawContent.length();
    }

    /**
     * {@inheritDoc}
     *
     * @param start {@inheritDoc}
     * @param end {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public CharSequence subSequence(int start, int end){
        return this.rawContent.subSequence(start, end);
    }

    /**
     * 範囲指定されたサブコンテントを切り出す。
     *
     * <p>サブコンテントにはデコードエラー情報が引き継がれる。
     *
     * @param start 開始位置
     * @param end 終了位置
     * @return サブコンテント
     * @throws IndexOutOfBoundsException start または end が負の値の場合、
     *     end が length() より大きい場合、
     *     あるいは start が end より大きい場合
     */
    public DecodedContent subContent(int start, int end)
            throws IndexOutOfBoundsException{
        int length = end - start;
        if(length < 0) throw new IndexOutOfBoundsException();
        DecodedContent result = new DecodedContent(length);
        result.append(this, start, end);
        return result;
    }

    /**
     * 文字を末尾へ追加する。
     *
     * @param letter 追加する文字
     * @return thisオブジェクト
     */
    @Override
    public DecodedContent append(char letter){
        this.rawContent.append(letter);
        return this;
    }

    /**
     * 文字列を末尾へ追加する。
     *
     * <p>nullが渡されると文字列"null"として解釈される。
     *
     * @param seq 追加する文字列
     * @return thisオブジェクト
     */
    @Override
    public DecodedContent append(CharSequence seq){
        DecodedContent result;

        if(seq == null){
            result = append(NULLTEXT);
        }else if(seq instanceof DecodedContent){
            DecodedContent content = (DecodedContent) seq;
            int seqLen = seq.length();
            result = append(content, 0, seqLen);
        }else{
            this.rawContent.append(seq);
            result = this;
        }

        return result;
    }

    /**
     * 文字列を末尾へ追加する。
     *
     * <p>nullが渡されると文字列"null"として解釈される。
     *
     * @param seq 追加する文字列
     * @param startPos 開始位置
     * @param endPos 終了位置
     * @return thisオブジェクト
     * @throws IndexOutOfBoundsException 範囲指定が変。
     */
    @Override
    public DecodedContent append(CharSequence seq,
                                 int startPos, int endPos)
            throws IndexOutOfBoundsException{
        DecodedContent result;

        if(seq == null){
            result = append(NULLTEXT, startPos, endPos);
        }else if(seq instanceof DecodedContent){
            result = append((DecodedContent) seq, startPos, endPos);
        }else if(   startPos < 0
                 || startPos > endPos
                 || endPos > seq.length()){
            throw new IndexOutOfBoundsException();
        }else if(startPos == endPos){
            result = this;
        }else{
            this.rawContent.append(seq, startPos, endPos);
            result = this;
        }

        return result;
    }

    /**
     * 文字列を末尾へ追加する。
     *
     * @param str  追加する文字配列
     * @param offset 追加される最初の char のインデックス
     * @param len 追加される char の数
     * @return thisオブジェクト
     * @throws IndexOutOfBoundsException 範囲指定が不正。
     * @see StringBuffer#append(char[], int, int)
     */
    public DecodedContent append(char[] str, int offset, int len)
            throws IndexOutOfBoundsException{
        this.rawContent.append(str, offset, len);
        return this;
    }

    /**
     * 文字列を末尾へ追加する。
     *
     * <p>追加元のエラー情報は追加先へ引き継がれる。
     *
     * <p>nullが渡されると文字列"null"として解釈される。
     *
     * @param source 追加する文字列
     * @param startPos 開始位置
     * @param endPos 終了位置
     * @return thisオブジェクト
     * @throws IndexOutOfBoundsException 範囲指定が変。
     * @see Appendable#append(CharSequence, int, int)
     */
    public DecodedContent append(DecodedContent source,
                                 int startPos, int endPos)
            throws IndexOutOfBoundsException{
        if(source == null){
            return append(NULLTEXT, startPos, endPos);
        }

        if(   startPos < 0
           || startPos > endPos
           || endPos > source.length()){
            throw new IndexOutOfBoundsException();
        }else if(startPos == endPos){
            return this;
        }

        int oldLength = this.rawContent.length();

        this.rawContent.append(source.rawContent, startPos, endPos);

        List<DecodeErrorInfo> srcErrList;
        if(source.hasDecodeError()){
            srcErrList = source.decodeError;
        }else{
            return this;
        }

        List<DecodeErrorInfo> dstErrList;
        if(source == this) dstErrList = null;
        else               dstErrList = this.decodeError;

        int gap = startPos - oldLength;

        dstErrList = appendGappedErrorInfo(srcErrList,
                                           startPos, endPos,
                                           dstErrList,
                                           gap);

        if(dstErrList == null)             return this;
        if(dstErrList == this.decodeError) return this;

        if(this.decodeError == null){
            this.decodeError = dstErrList;
        }else{
            this.decodeError.addAll(dstErrList);
        }

        return this;
    }

    /**
     * 代替文字とともにデコードエラーを末尾へ追加する。
     *
     * <p>※呼び出し側は、追加されるデコードエラーの位置情報が
     * 既存のデコードエラーよりも大きいことを保証しなければならない。
     *
     * @param errorInfo デコードエラー
     */
    private void addDecodeError(DecodeErrorInfo errorInfo){
        if(this.decodeError == null){
            this.decodeError = createErrorList();
        }
        this.decodeError.add(errorInfo);
        this.rawContent.append(ALTCHAR);
        return;
    }

    /**
     * 代替文字とともにデコードエラーを末尾へ追加する。
     *
     * @param b1st エラー1バイト目の値
     */
    public void addDecodeError(byte b1st){
        DecodeErrorInfo errInfo =
                new DecodeErrorInfo(this.rawContent.length(), b1st);
        addDecodeError(errInfo);
        return;
    }

    /**
     * 代替文字とともに2バイトからなるデコードエラーを末尾へ追加する。
     *
     * <p>主にシフトJISのUnmapエラーを想定。
     *
     * @param b1st エラー1バイト目の値
     * @param b2nd エラー2バイト目の値
     */
    public void addDecodeError(byte b1st, byte b2nd){
        DecodeErrorInfo errInfo =
                new DecodeErrorInfo(this.rawContent.length(), b1st, b2nd);
        addDecodeError(errInfo);
        return;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public String toString(){
        return this.rawContent.toString();
    }

}
