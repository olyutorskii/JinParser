/*
 * basic handler for XHTML
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.osdn.jindolf.parser;

import jp.osdn.jindolf.parser.content.DecodedContent;
import jp.sourceforge.jindolf.corelib.PeriodType;
import jp.sourceforge.jindolf.corelib.VillageState;

/**
 * 人狼BBSの各種XHTMLの基本的な構造をパースするためのハンドラ。
 * このハンドラの全メソッドはパーサ{@link HtmlParser}により呼ばれる。
 *
 * <p>
 * パーサはパース開始時に{@link #startParse(DecodedContent)}を呼び、
 * パース終了直前に{@link #endParse()}を呼ぶ。
 * その間に他の様々なメソッドが呼び出される。
 * </p>
 *
 * <p>
 * 一部のメソッドに渡される{@link DecodedContent}文字列オブジェクトは
 * mutableである。
 * 後々で内容が必要になるならば、ハンドラはSeqRangeで示されたこの内容の
 * 必要な箇所をコピーして保存しなければならない。
 * </p>
 *
 * <p>
 * フラグメントや属性値中の文字参照記号列の解釈はハンドラ側の責務とする。
 * </p>
 *
 * <p>
 * 各メソッドは、各々の判断で{@link HtmlParseException}をスローする
 * ことにより、パース作業を中断させることができる。
 * </p>
 */
public interface BasicHandler{

    /**
     * パース開始の通知を受け取る。
     * @param content これからパースを始めるXHTML文字列
     * @throws HtmlParseException パースエラー
     */
    public abstract void startParse(DecodedContent content) throws HtmlParseException;

    /**
     * titleタグの内容の通知を受け取る。
     * 例：「人狼BBS:F F2019 新緑の村」。
     * @param content パース対象文字列
     * @param titleRange タイトルの範囲
     * @throws HtmlParseException パースエラー
     */
    public abstract void pageTitle(DecodedContent content, SeqRange titleRange)
            throws HtmlParseException;

    /**
     * ログイン名(ID)の通知を受け取る。
     * ログインせずに得られたページがパース対象であるなら、呼ばれない。
     * F国のみで動作確認。
     * @param content パース対象文字列
     * @param loginRange ログイン名の範囲
     * @throws HtmlParseException パースエラー
     */
    public abstract void loginName(DecodedContent content, SeqRange loginRange)
            throws HtmlParseException;

    /**
     * 読み込んだページ種別を自動認識した結果を伝える。
     * ページタイトルもしくはログイン名の通知の後に呼ばれうる。
     * @param type ページ種別
     * @throws HtmlParseException パースエラー
     */
    public abstract void pageType(PageType type)
            throws HtmlParseException;

    /**
     * 村の名前の通知を受け取る。
     * 国名と番号と愛称に分解するのはハンドラ側の責務。
     * 例：「F2019 新緑の村」。
     * @param content パース対象文字列
     * @param villageRange 村名の範囲
     * @throws HtmlParseException パースエラー
     */
    public abstract void villageName(DecodedContent content, SeqRange villageRange)
            throws HtmlParseException;

    /**
     * 次回更新時刻の通知を受け取る。
     * 既に終了した村がパース対象の場合、あまり月日に意味はないかも。
     * @param month 更新月
     * @param day 更新日
     * @param hour 更新時
     * @param minute 更新分
     * @throws HtmlParseException パースエラー
     */
    public abstract void commitTime(int month, int day, int hour, int minute)
            throws HtmlParseException;

    /**
     * 他の日へのリンクの通知を受け取る。
     * 複数回呼ばれる場合がある。
     * @param content パース対象文字列
     * @param anchorRange aタグhref属性値の範囲
     * @param periodType 日のタイプ。「終了」ならnull。
     * @param day 日にち。「プロローグ」、「エピローグ」、「終了」では-1。
     * @throws HtmlParseException パースエラー
     */
    public abstract void periodLink(DecodedContent content,
                     SeqRange anchorRange,
                     PeriodType periodType, int day)
            throws HtmlParseException;

    /**
     * 村一覧リスト内の個別の村情報の通知を受け取る。
     * @param content パース対象文字列
     * @param anchorRange URLの範囲
     * @param villageRange 村名の範囲
     * @param hour 更新時。不明なら負の数。
     * @param minute 更新分。不明なら負の数。
     * @param state 村の状態
     * @throws HtmlParseException パースエラー
     */
    public abstract void villageRecord(DecodedContent content,
                         SeqRange anchorRange,
                         SeqRange villageRange,
                         int hour, int minute,
                         VillageState state )
            throws HtmlParseException;

    /**
     * パースの終了の通知を受け取る。
     * @throws HtmlParseException パースエラー
     */
    public abstract void endParse() throws HtmlParseException;

}
