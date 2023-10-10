/*
 * System event handler
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.osdn.jindolf.parser;

import jp.osdn.jindolf.parser.content.DecodedContent;
import jp.sourceforge.jindolf.corelib.EventFamily;
import jp.sourceforge.jindolf.corelib.GameRole;
import jp.sourceforge.jindolf.corelib.SysEventType;
import jp.sourceforge.jindolf.corelib.Team;

/**
 * システムイベントのパース通知用のハンドラ。
 *
 * <p>
 * このハンドラの全メソッドはパーサ{@link SysEventParser}から呼び出される。
 * </p>
 *
 * <p>
 * パーサがシステムイベントを発見すると、まず最初に
 * {@link #startSysEvent(EventFamily)}がファミリ種別と共に呼び出される。
 * 次にシステムイベントのイベント種別が判明すると、
 * {@link #sysEventType(SysEventType)}が呼び出される。
 * イベント種別に従い、このハンドラの様々なメソッドが0回以上呼び出される。
 * 最後に{@link #endSysEvent()}が呼び出される。
 * その後パーサは次のシステムイベントを探し始める。
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
public interface SysEventHandler{

    /**
     * システムイベントのパース開始の通知を受け取る。
     * @param eventFamily イベントファミリ種別
     * @throws HtmlParseException パースエラー
     */
    public abstract void startSysEvent(EventFamily eventFamily)
        throws HtmlParseException;

    /**
     * システムイベント種別の通知を受け取る。
     * @param type イベント種別
     * @throws HtmlParseException パースエラー
     */
    public abstract void sysEventType(SysEventType type)
        throws HtmlParseException;

    /**
     * システムイベントのパース処理終了の通知を受け取る。
     * @throws HtmlParseException パースエラー
     */
    public abstract void endSysEvent()
        throws HtmlParseException;

    /**
     * ONSTAGEイベントの詳細の通知を受け取る。
     * @param content パース対象の文字列
     * @param entryNo エントリ番号
     * @param avatarRange Avatar名の範囲
     * @throws HtmlParseException パースエラー
     * @see jp.sourceforge.jindolf.corelib.SysEventType#ONSTAGE
     */
    public abstract void sysEventOnStage(DecodedContent content,
                           int entryNo,
                           SeqRange avatarRange )
        throws HtmlParseException;

    /**
     * OPENROLEイベントの詳細の通知を受け取る。
     * 複数回連続して呼ばれる。
     * @param role 役職
     * @param num 役職の人数
     * @throws HtmlParseException パースエラー
     * @see jp.sourceforge.jindolf.corelib.SysEventType#OPENROLE
     */
    public abstract void sysEventOpenRole(GameRole role, int num)
        throws HtmlParseException;

    /**
     * SURVIVORイベントの詳細の通知を受け取る。
     * 複数回連続して呼ばれる。
     * @param content パース対象の文字列
     * @param avatarRange Avatar名の範囲
     * @throws HtmlParseException パースエラー
     * @see jp.sourceforge.jindolf.corelib.SysEventType#SURVIVOR
     */
    public abstract void sysEventSurvivor(DecodedContent content,
                            SeqRange avatarRange)
        throws HtmlParseException;

    /**
     * COUNTINGイベントの詳細の通知を受け取る。
     * 複数回連続して呼ばれる。
     * 最後の呼び出しで投票元Avatar名の位置情報が負だった場合、
     * 投票先Avatar名は処刑が実行されたAvatarを表す。
     * @param content パース対象の文字列。
     * @param voteByRange 投票元Avatar名の範囲
     * @param voteToRange 投票先Avatar名の範囲
     * @throws HtmlParseException パースエラー
     * @see jp.sourceforge.jindolf.corelib.SysEventType#COUNTING
     */
    public abstract void sysEventCounting(DecodedContent content,
                            SeqRange voteByRange,
                            SeqRange voteToRange )
        throws HtmlParseException;

    /**
     * COUNTING2イベントの詳細の通知を受け取る。※G国のみ
     * 複数回連続して呼ばれる。
     * @param content パース対象の文字列。
     * @param voteByRange 投票元Avatar名の範囲
     * @param voteToRange 投票先Avatar名の範囲
     * @throws HtmlParseException パースエラー
     * @see jp.sourceforge.jindolf.corelib.SysEventType#COUNTING2
     */
    public abstract void sysEventCounting2(DecodedContent content,
                             SeqRange voteByRange,
                             SeqRange voteToRange )
        throws HtmlParseException;

    /**
     * SUDDENDEATHイベントの詳細の通知を受け取る。
     * @param content パース対象の文字列
     * @param avatarRange Avatar名の範囲
     * @throws HtmlParseException パースエラー
     * @see jp.sourceforge.jindolf.corelib.SysEventType#SUDDENDEATH
     */
    public abstract void sysEventSuddenDeath(DecodedContent content,
                               SeqRange avatarRange )
        throws HtmlParseException;

    /**
     * MURDEREDイベントの詳細の通知を受け取る。
     * ハム溶けの時など、連続して複数回呼ばれる事がある。
     * @param content パース対象の文字列
     * @param avatarRange Avatar名の範囲
     * @throws HtmlParseException パースエラー
     * @see jp.sourceforge.jindolf.corelib.SysEventType#MURDERED
     */
    public abstract void sysEventMurdered(DecodedContent content,
                            SeqRange avatarRange )
        throws HtmlParseException;

    /**
     * PLAYERLISTイベントの詳細の通知を受け取る。
     * 複数回連続して呼ばれる。
     * @param content パース対象の文字列
     * @param avatarRange Avatar名の範囲
     * @param anchorRange URLの範囲。無ければ無効。
     * @param loginRange IDの範囲
     * @param isLiving 生存していればtrue
     * @param role 役職
     * @throws HtmlParseException パースエラー
     * @see jp.sourceforge.jindolf.corelib.SysEventType#PLAYERLIST
     */
    public abstract void sysEventPlayerList(DecodedContent content,
                              SeqRange avatarRange,
                              SeqRange anchorRange,
                              SeqRange loginRange,
                              boolean isLiving,
                              GameRole role )
        throws HtmlParseException;

    /**
     * EXECUTIONイベントの詳細の通知を受け取る。※G国のみ
     * 複数回連続して呼ばれる。
     * @param content パース対象の文字列。
     * @param avatarRange 投票先Avatar名の範囲
     * @param votes 得票数。負の値であれば、
     *     処刑されたAvatarの通知と見なされる。
     * @throws HtmlParseException パースエラー
     * @see jp.sourceforge.jindolf.corelib.SysEventType#EXECUTION
     */
    public abstract void sysEventExecution(DecodedContent content,
                             SeqRange avatarRange,
                             int votes )
        throws HtmlParseException;

    /**
     * VANISHイベントの詳細の通知を受け取る。
     * @param content パース対象の文字列
     * @param avatarRange 失踪したAvatar名の範囲
     * @throws HtmlParseException パースエラー
     * @see jp.sourceforge.jindolf.corelib.SysEventType#VANISH
     */
    public abstract void sysEventVanish(DecodedContent content,
                          SeqRange avatarRange )
        throws HtmlParseException;

    /**
     * CHECKOUTイベントの詳細の通知を受け取る。
     * @param content パース対象の文字列
     * @param avatarRange 失踪したAvatar名の範囲
     * @throws HtmlParseException パースエラー
     * @see jp.sourceforge.jindolf.corelib.SysEventType#CHECKOUT
     */
    public abstract void sysEventCheckout(DecodedContent content,
                            SeqRange avatarRange )
        throws HtmlParseException;

    /**
     * JUDGEイベントの詳細の通知を受け取る。
     * @param content パース対象の文字列。
     * @param judgeByRange 占師Avatar名の範囲
     * @param judgeToRange 占われたAvatar名の範囲
     * @throws HtmlParseException パースエラー
     * @see jp.sourceforge.jindolf.corelib.SysEventType#JUDGE
     */
    public abstract void sysEventJudge(DecodedContent content,
                         SeqRange judgeByRange,
                         SeqRange judgeToRange )
        throws HtmlParseException;

    /**
     * GUARDイベントの詳細の通知を受け取る。
     * @param content パース対象の文字列。
     * @param guardByRange 狩人Avatar名の範囲
     * @param guardToRange 護られたAvatar名の範囲
     * @throws HtmlParseException パースエラー
     * @see jp.sourceforge.jindolf.corelib.SysEventType#GUARD
     */
    public abstract void sysEventGuard(DecodedContent content,
                         SeqRange guardByRange,
                         SeqRange guardToRange )
        throws HtmlParseException;

    /**
     * ASKENTRYイベントの詳細の通知を受け取る。
     * @param hour 時間
     * @param minute 分
     * @param minLimit 最小構成人数
     * @param maxLimit 最大定員
     * @throws HtmlParseException パースエラー
     * @see jp.sourceforge.jindolf.corelib.SysEventType#ASKENTRY
     */
    public abstract void sysEventAskEntry(int hour, int minute,
                            int minLimit, int maxLimit)
        throws HtmlParseException;

    /**
     * ASKCOMMITイベントの詳細の通知を受け取る。
     * @param hour 時間(24時間制)
     * @param minute 分
     * @throws HtmlParseException パースエラー
     * @see jp.sourceforge.jindolf.corelib.SysEventType#ASKCOMMIT
     */
    public abstract void sysEventAskCommit(int hour, int minute)
        throws HtmlParseException;

    /**
     * NOCOMMENTイベントの詳細の通知を受け取る。
     * 複数回連続して呼ばれる可能性がある。
     * @param content パース対象文字列
     * @param avatarRange Avatar名の範囲
     * @throws HtmlParseException パースエラー
     * @see jp.sourceforge.jindolf.corelib.SysEventType#NOCOMMENT
     */
    public abstract void sysEventNoComment(DecodedContent content,
                             SeqRange avatarRange )
        throws HtmlParseException;

    /**
     * STAYEPILOGUEイベントの詳細の通知を受け取る。
     * @param winner 勝利陣営
     * @param hour 時間(24時間制)
     * @param minute 分
     * @throws HtmlParseException パースエラー
     * @see jp.sourceforge.jindolf.corelib.SysEventType#STAYEPILOGUE
     */
    public abstract void sysEventStayEpilogue(Team winner, int hour, int minute)
        throws HtmlParseException;

    /**
     * イベントの内容(DIV要素)の一般文字列出現の通知を受け取る。
     * イベント種別は問わない。
     * @param content パース対象文字列
     * @param contentRange 内容テキストの範囲
     * @throws HtmlParseException パースエラー
     */
    public abstract void sysEventContent(DecodedContent content,
                           SeqRange contentRange )
        throws HtmlParseException;

    /**
     * イベントの内容(DIV要素)のBRタグ出現の通知を受け取る。
     * イベント種別は問わない。
     * @throws HtmlParseException パースエラー
     */
    public abstract void sysEventContentBreak()
        throws HtmlParseException;

    /**
     * イベントの内容(DIV要素)のAタグ出現の通知を受け取る。
     * イベント種別は問わない。
     * href属性によるURL記述も通知される。
     * @param content パース対象文字列
     * @param anchorRange URLの範囲
     * @param contentRange 内容テキストの範囲
     * @throws HtmlParseException パースエラー
     */
    public abstract void sysEventContentAnchor(DecodedContent content,
                                  SeqRange anchorRange,
                                  SeqRange contentRange )
        throws HtmlParseException;

}
