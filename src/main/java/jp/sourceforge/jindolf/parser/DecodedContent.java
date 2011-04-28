/*
 * decoded source
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

/**
 * ShiftJISデコードエラー情報を含む再利用可能な文字列。
 * デコードエラーを起こした箇所は代替文字{@link #ALTCHAR}で置き換えられる。
 * マルチスレッドには非対応。
 * UCS-4コードポイントには未対応。
 */
public class DecodedContent
        implements CharSequence,
                   Appendable {

    /**
     * 代替文字。
     * {@literal HTMLで使うなら < や > や & や " や ' はやめて！}
     */
    public static final char ALTCHAR = '?';

    private static final List<DecodeErrorInfo> EMPTY_LIST =
            Collections.emptyList();

    private static final int BSEARCH_THRESHOLD = 16;

    static{
        assert ALTCHAR != '<';
        assert ALTCHAR != '>';
        assert ALTCHAR != '&';
        assert ALTCHAR != '"';
        assert ALTCHAR != '\'';
        assert ALTCHAR != '\\';
    }

    /**
     * 与えられた文字位置を含むか、またはそれ以降で最も小さな位置情報を持つ
     * デコードエラーのインデックス位置を返す。※リニアサーチ版。
     * @param errList デコードエラーのリスト
     * @param startPos 文字位置
     * @return 0から始まるリスト内の位置。
     * 一致する文字位置がなければ挿入ポイント。
     */
    protected static int lsearchErrorIndex(List<DecodeErrorInfo> errList,
                                             int startPos){
        // assert errList instanceof RandomAccess;

        int errSize = errList.size();

        int idx;
        for(idx = 0; idx < errSize; idx++){
            DecodeErrorInfo einfo = errList.get(idx);
            int errPos = einfo.getCharPosition();
            if(startPos <= errPos) break;
        }

        return idx;
    }

    /**
     * 与えられた文字位置を含むか、またはそれ以降で最も小さな位置情報を持つ
     * デコードエラーのインデックス位置を返す。※バイナリサーチ版。
     * @param errList デコードエラーのリスト
     * @param startPos 文字位置
     * @return 0から始まるリスト内の位置。
     * 一致する文字位置がなければ挿入ポイント。
     */
    protected static int bsearchErrorIndex(List<DecodeErrorInfo> errList,
                                             int startPos){
        // assert errList instanceof RandomAccess;

        int floor = 0;
        int roof  = errList.size() - 1;

        while(floor <= roof){
            int midpoint = (floor + roof) / 2;  // 切り捨て
            DecodeErrorInfo einfo = errList.get(midpoint);
            int cmp = einfo.getCharPosition() - startPos;

            if(cmp == 0) return midpoint;

            if     (cmp < 0) floor = midpoint + 1;
            else if(cmp > 0) roof  = midpoint - 1;
        }

        return floor;
    }

    /**
     * 与えられた文字位置を含むか、またはそれ以降で最も小さな位置情報を持つ
     * デコードエラーのインデックス位置を返す。
     * 要素数の増減に応じてリニアサーチとバイナリサーチを使い分ける。
     * @param errList デコードエラーのリスト
     * @param startPos 文字位置
     * @return 0から始まるリスト内の位置。
     * 一致する文字位置がなければ挿入ポイント。
     */
    protected static int searchErrorIndex(List<DecodeErrorInfo> errList,
                                            int startPos){
        int result;

        int errSize = errList.size();
        if(errSize < BSEARCH_THRESHOLD){
            // linear-search
            result = lsearchErrorIndex(errList, startPos);
        }else{
            // binary-search
            result = bsearchErrorIndex(errList, startPos);
        }

        return result;
    }

    /**
     * ギャップ情報が加味されたデコードエラー情報を、
     * 範囲指定込みで指定エラーリストに追加転記する。
     * 追加先エラーリストがnullだった場合、必要に応じてエラーリストが生成され
     * 戻り値となる場合がありうる。
     * @param sourceContent 元の文字列
     * @param startPos 範囲開始位置
     * @param endPos 範囲終了位置
     * @param targetError 追加先エラーリスト。nullでもよい。
     * @param gap ギャップ量
     * @return 引数targetErrorもしくは新規生成されたリストを返す。
     */
    protected static List<DecodeErrorInfo>
            appendGappedErrorInfo(DecodedContent sourceContent,
                                     int startPos, int endPos,
                                     List<DecodeErrorInfo> targetError,
                                     int gap){
        List<DecodeErrorInfo> sourceError = sourceContent.decodeError;
        List<DecodeErrorInfo> result = targetError;

        int startErrorIdx = searchErrorIndex(sourceError, startPos);
        int endErrorIdx = sourceError.size() - 1;
        assert endErrorIdx >= 0;

        for(int index = startErrorIdx; index <= endErrorIdx; index++){
            DecodeErrorInfo einfo = sourceError.get(index);
            int pos = einfo.getCharPosition();
            if(pos < startPos) continue;
            if(pos >= endPos) break;
            DecodeErrorInfo newInfo = einfo.createGappedClone(gap);
            if(result == null){
                result = createErrorList();
            }
            result.add(newInfo);
        }

        return result;
    }

    /**
     * エラー格納用リストを生成する。
     * @return リスト
     */
    private static List<DecodeErrorInfo> createErrorList(){
        List<DecodeErrorInfo> result = new ArrayList<DecodeErrorInfo>();
        return result;
    }

    static{
        assert createErrorList() instanceof RandomAccess;
    }

    private final StringBuilder rawContent = new StringBuilder();

    private List<DecodeErrorInfo> decodeError;

    /**
     * コンストラクタ。
     */
    public DecodedContent(){
        this("");
        return;
    }

    /**
     * コンストラクタ。
     * @param seq 初期化文字列
     * @throws NullPointerException 引数がnull
     */
    public DecodedContent(CharSequence seq) throws NullPointerException{
        super();
        if(seq == null) throw new NullPointerException();
        initImpl();
        this.rawContent.append(seq);
        return;
    }

    /**
     * コンストラクタ。
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
     * 初期化下請け。
     * 長さ0の文字列＆デコードエラー無しの状態になる。
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
     * 指定されたキャパシティの範囲内で再割り当てが起きないことを保証する。
     * @param minimumCapacity キャラクタ単位のキャパシティ長。
     */
    public void ensureCapacity(int minimumCapacity){
        this.rawContent.ensureCapacity(minimumCapacity);
        return;
    }

    /**
     * 初期化。
     * 長さ0の文字列＆デコードエラー無しの状態になる。
     * コンストラクタで新インスタンスを作るより低コスト。
     */
    public void init(){
        initImpl();
        return;
    }

    /**
     * デコードエラーを含むか判定する。
     * @return デコードエラーを含むならtrue
     */
    public boolean hasDecodeError(){
        if(this.decodeError == null) return false;
        if(this.decodeError.isEmpty()) return false;
        return true;
    }

    /**
     * デコードエラーの一覧を取得する。
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
     * 高速なCharSequenceアクセス用途。
     * @return 生の文字列。
     */
    public CharSequence getRawContent(){
        return this.rawContent;
    }

    /**
     * 指定された位置の文字を変更する。
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
     * @param index {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public char charAt(int index){
        return this.rawContent.charAt(index);
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public int length(){
        return this.rawContent.length();
    }

    /**
     * {@inheritDoc}
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
     * サブコンテントにはデコードエラー情報が引き継がれる。
     * @param start 開始位置
     * @param end 終了位置
     * @return サブコンテント
     * @throws IndexOutOfBoundsException start または end が負の値の場合、
     * end が length() より大きい場合、あるいは start が end より大きい場合
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
     * 文字を追加する。
     * @param letter 追加する文字
     * @return thisオブジェクト
     */
    public DecodedContent append(char letter){
        this.rawContent.append(letter);
        return this;
    }

    /**
     * 文字列を追加する。
     * @param seq 追加する文字列
     * @return thisオブジェクト
     */
    public DecodedContent append(CharSequence seq){
        if(seq == null){
            this.rawContent.append("null");
        }else if(seq instanceof DecodedContent){
            append((DecodedContent)seq, 0, seq.length());
        }else{
            this.rawContent.append(seq);
        }
        return this;
    }

    /**
     * 文字列を追加する。
     * @param seq 追加する文字列
     * @param startPos 開始位置
     * @param endPos 終了位置
     * @return thisオブジェクト
     * @throws IndexOutOfBoundsException 範囲指定が変。
     */
    public DecodedContent append(CharSequence seq,
                                  int startPos, int endPos)
            throws IndexOutOfBoundsException{
        if(seq == null){
            this.rawContent.append("null", startPos, endPos);
        }else if(seq instanceof DecodedContent){
            append((DecodedContent)seq, startPos, endPos);
        }else{
            this.rawContent.append(seq, startPos, endPos);
        }

        return this;
    }

    /**
     * 文字列を追加する。
     * @param source 追加する文字列
     * @param startPos 開始位置
     * @param endPos 終了位置
     * @return thisオブジェクト
     * @throws IndexOutOfBoundsException 範囲指定が変。
     */
    public DecodedContent append(DecodedContent source,
                                  int startPos, int endPos)
            throws IndexOutOfBoundsException{
        if(source == null){
            return append("null", startPos, endPos);
        }

        int gap = startPos - this.rawContent.length();

        this.rawContent.append(source.rawContent, startPos, endPos);

        if( ! source.hasDecodeError() ) return this;

        List<DecodeErrorInfo> targetErrorList;
        if(source != this) targetErrorList = this.decodeError;
        else               targetErrorList = null;

        targetErrorList = appendGappedErrorInfo(source,
                                                startPos, endPos,
                                                targetErrorList,
                                                gap);

        if(targetErrorList == null)             return this;
        if(targetErrorList == this.decodeError) return this;

        if(this.decodeError == null){
            this.decodeError = targetErrorList;
        }else{
            this.decodeError.addAll(targetErrorList);
        }

        return this;
    }

    /**
     * 代替文字とともにデコードエラーを追加する。
     * ※呼び出し側は、追加されるデコードエラーの位置情報が
     * 既存のデコードエラーよりも大きいことを保証しなければならない。
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
     * 代替文字とともにデコードエラーを追加する。
     * @param b1st 1バイト目の値
     */
    public void addDecodeError(byte b1st){
        DecodeErrorInfo errInfo =
                new DecodeErrorInfo(this.rawContent.length(), b1st);
        addDecodeError(errInfo);
        return;
    }

    /**
     * 代替文字とともにデコードエラーを追加する。
     * @param b1st 1バイト目の値
     * @param b2nd 2バイト目の値
     */
    public void addDecodeError(byte b1st, byte b2nd){
        DecodeErrorInfo errInfo =
                new DecodeErrorInfo(this.rawContent.length(), b1st, b2nd);
        addDecodeError(errInfo);
        return;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String toString(){
        return this.rawContent.toString();
    }

    // TODO Windows-31Jへの再デコード処理など
}
