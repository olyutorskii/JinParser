/*
 * handler for parse talk-part
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

import jp.sourceforge.jindolf.corelib.TalkType;

/**
 * 人狼BBSの発言部XHTML断片をパースするためのハンドラ。
 *
 * <p>
 * このハンドラの全メソッドはパーサ{@link HtmlParser}から呼び出される。
 * </p>
 *
 * <p>
 * パーサが発言箇所を発見すると、まず最初に
 * {@link #startTalk()}が呼び出される。
 * 発言内容に従い、このハンドラの様々なメソッドが0回以上呼び出される。
 * 最後に{@link #endTalk()}が呼び出される。
 * その後パーサは次の発言を探し始める。
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
public interface TalkHandler{

    /**
     * 発言部パース開始の通知を受け取る。
     * @throws HtmlParseException パースエラー
     */
    void startTalk()
            throws HtmlParseException;

    /**
     * 発言部パース終了の通知を受け取る。
     * @throws HtmlParseException パースエラー
     */
    void endTalk()
            throws HtmlParseException;

    /**
     * 白発言番号を受け取る。※G国only。
     * 負の値が渡ってきた場合は白発言でないので無視してよい。
     * @param talkNo 白発言番号
     * @throws HtmlParseException パースエラー
     */
    void talkNo(int talkNo)
            throws HtmlParseException;

    /**
     * 発言部ID(Aタグのname属性)の通知を受け取る。
     * @param content パース対象文字列
     * @param idRange IDの範囲
     * @throws HtmlParseException パースエラー
     */
    void talkId(DecodedContent content, SeqRange idRange)
            throws HtmlParseException;

    /**
     * 発言したAvatar名の通知を受け取る。
     * @param content パース対象文字列
     * @param avatarRange Avatar名の範囲
     * @throws HtmlParseException パースエラー
     */
    void talkAvatar(DecodedContent content, SeqRange avatarRange)
            throws HtmlParseException;

    /**
     * 発言時刻の通知を受け取る。
     * @param hour 時間(24時間制)
     * @param minute 分
     * @throws HtmlParseException パースエラー
     */
    void talkTime(int hour, int minute)
            throws HtmlParseException;

    /**
     * 発言者の顔アイコンURLの通知を受け取る。
     * @param content パース対象文字列
     * @param urlRange URLの範囲。
     * @throws HtmlParseException パースエラー
     */
    void talkIconUrl(DecodedContent content, SeqRange urlRange)
            throws HtmlParseException;

    /**
     * 発言種別の通知を受け取る。
     * @param type 発言種別
     * @throws HtmlParseException パースエラー
     */
    void talkType(TalkType type)
            throws HtmlParseException;

    /**
     * 発言テキスト内容の通知を受け取る。
     * 1発言のパース中に複数回呼ばれる事もありうる。
     * @param content パース対象文字列
     * @param textRange テキストの範囲
     * @throws HtmlParseException パースエラー
     */
    void talkText(DecodedContent content, SeqRange textRange)
            throws HtmlParseException;

    /**
     * 発言テキスト内のBRタグ出現の通知を受け取る。
     * 1発言のパース中に複数回呼ばれる事もありうる。
     * @throws HtmlParseException パースエラー
     */
    void talkBreak()
            throws HtmlParseException;

}
