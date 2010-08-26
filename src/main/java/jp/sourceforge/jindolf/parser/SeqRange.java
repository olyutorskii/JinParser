/*
 * range of string
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

import java.util.regex.MatchResult;

/**
 * 文字列の範囲を表す。
 * 範囲は開始位置と終了位置で表される。
 * 開始位置と終了位置が同じ場合、長さ0の範囲とみなされる。
 * 開始位置0は文字列の左端を表す。
 * 開始位置が負の場合、もしくは開始位置より終了位置が小さい場合、
 * このオブジェクトは無効とみなされる。
 */
public class SeqRange{

    private int startPos;
    private int endPos;

    /**
     * コンストラクタ。
     * 開始位置、終了位置ともに無効状態となる。
     */
    public SeqRange(){
        this(-1, -1);
        return;
    }

    /**
     * コンストラクタ。
     * @param startPos 開始位置
     * @param endPos 終了位置
     */
    public SeqRange(int startPos, int endPos){
        super();
        this.startPos = startPos;
        this.endPos   = endPos;
        return;
    }

    /**
     * 開始位置を設定する。
     * @param startPos 開始位置
     */
    public void setStartPos(int startPos){
        this.startPos = startPos;
        return;
    }

    /**
     * 終了位置を設定する。
     * @param endPos 終了位置
     */
    public void setEndPos(int endPos){
        this.endPos = endPos;
        return;
    }

    /**
     * 開始位置と終了位置を設定する。
     * @param startPosition 開始位置
     * @param endPosition 終了位置
     */
    public void setRange(int startPosition, int endPosition){
        this.startPos = startPosition;
        this.endPos   = endPosition;
        return;
    }

    /**
     * 最後にマッチした前方参照グループの範囲で設定する。
     * @param result 正規表現マッチ結果
     * @param groupId グループ番号
     * @throws IllegalStateException マッチしていない
     * @throws IndexOutOfBoundsException グループ番号が不正
     */
    public void setLastMatchedGroupRange(MatchResult result, int groupId)
            throws IllegalStateException,
                   IndexOutOfBoundsException {
        this.startPos = result.start(groupId);
        this.endPos   = result.end(groupId);
        return;
    }

    /**
     * 最後にマッチした範囲全体で設定する。
     * @param result 正規表現マッチ結果
     * @throws IllegalStateException マッチしていない
     */
    public void setLastMatchedRange(MatchResult result)
            throws IllegalStateException {
        this.startPos = result.start();
        this.endPos   = result.end();
        return;
    }

    /**
     * 開始位置を取得する。
     * @return 開始位置
     */
    public int getStartPos(){
        return this.startPos;
    }

    /**
     * 終了位置を取得する。
     * @return 終了位置
     */
    public int getEndPos(){
        return this.endPos;
    }

    /**
     * 範囲の長さを得る。
     * 内容が無効な場合、負の値もありえる。
     * @return 長さ
     */
    public int length(){
        int length = this.endPos - this.startPos;
        return length;
    }

    /**
     * 現在の範囲で与えられた文字列を切り出す。
     * @param seq 切り出し元文字列
     * @return 切り出された文字列
     * @throws IndexOutOfBoundsException 範囲が無効
     */
    public CharSequence sliceSequence(CharSequence seq)
            throws IndexOutOfBoundsException{
        CharSequence result = seq.subSequence(this.startPos, this.endPos);
        return result;
    }

    /**
     * 範囲指定を無効にする。
     */
    public void setInvalid(){
        this.startPos = -1;
        this.endPos   = -1;
        return;
    }

    /**
     * 範囲指定が有効か判定する。
     * @return 有効であればtrue
     */
    public boolean isValid(){
        if     (this.startPos < 0)           return false;
        else if(this.startPos > this.endPos) return false;
        return true;
    }

}
