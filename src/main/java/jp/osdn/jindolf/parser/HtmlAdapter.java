/*
 * html handler adapter
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.osdn.jindolf.parser;

import jp.osdn.jindolf.parser.content.DecodedContent;
import jp.sourceforge.jindolf.corelib.EventFamily;
import jp.sourceforge.jindolf.corelib.GameRole;
import jp.sourceforge.jindolf.corelib.PeriodType;
import jp.sourceforge.jindolf.corelib.SysEventType;
import jp.sourceforge.jindolf.corelib.TalkType;
import jp.sourceforge.jindolf.corelib.Team;
import jp.sourceforge.jindolf.corelib.VillageState;

/**
 * インタフェース{@link HtmlHandler}の抽象アダプタクラス。
 * このクラスのメソッド自身は何もしない。
 */
public abstract class HtmlAdapter implements HtmlHandler{

    /**
     * コンストラクタ。
     */
    protected HtmlAdapter(){
        super();
        return;
    }


    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void startParse(DecodedContent content)
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param titleRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void pageTitle(DecodedContent content, SeqRange titleRange)
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param loginRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void loginName(DecodedContent content, SeqRange loginRange)
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param type {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void pageType(PageType type)
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param villageRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void villageName(DecodedContent content, SeqRange villageRange)
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param month {@inheritDoc}
     * @param day {@inheritDoc}
     * @param hour {@inheritDoc}
     * @param minute {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void commitTime(int month, int day, int hour, int minute)
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param anchorRange {@inheritDoc}
     * @param periodType {@inheritDoc}
     * @param day {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void periodLink(DecodedContent content,
                            SeqRange anchorRange,
                            PeriodType periodType, int day)
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param anchorRange {@inheritDoc}
     * @param villageRange {@inheritDoc}
     * @param hour {@inheritDoc}
     * @param minute {@inheritDoc}
     * @param state {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void villageRecord(DecodedContent content,
                                SeqRange anchorRange,
                                SeqRange villageRange,
                                int hour, int minute,
                                VillageState state )
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void endParse() throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void startTalk() throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void endTalk() throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param talkNo {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void talkNo(int talkNo) throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param idRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void talkId(DecodedContent content, SeqRange idRange)
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param avatarRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void talkAvatar(DecodedContent content, SeqRange avatarRange)
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param hour {@inheritDoc}
     * @param minute {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void talkTime(int hour, int minute) throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param urlRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void talkIconUrl(DecodedContent content, SeqRange urlRange)
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param type {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void talkType(TalkType type) throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param textRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void talkText(DecodedContent content, SeqRange textRange)
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void talkBreak() throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param eventFamily {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void startSysEvent(EventFamily eventFamily)
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param type {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventType(SysEventType type) throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void endSysEvent() throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param entryNo {@inheritDoc}
     * @param avatarRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventOnStage(DecodedContent content,
                                  int entryNo,
                                  SeqRange avatarRange )
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param role {@inheritDoc}
     * @param num {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventOpenRole(GameRole role, int num)
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param avatarRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventSurvivor(DecodedContent content,
                                   SeqRange avatarRange)
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param voteByRange {@inheritDoc}
     * @param voteToRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventCounting(DecodedContent content,
                                   SeqRange voteByRange,
                                   SeqRange voteToRange )
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param voteByRange {@inheritDoc}
     * @param voteToRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventCounting2(DecodedContent content,
                                    SeqRange voteByRange,
                                    SeqRange voteToRange )
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param avatarRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventSuddenDeath(DecodedContent content,
                                       SeqRange avatarRange )
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param avatarRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventMurdered(DecodedContent content,
                                   SeqRange avatarRange )
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param avatarRange {@inheritDoc}
     * @param anchorRange {@inheritDoc}
     * @param loginRange {@inheritDoc}
     * @param isLiving {@inheritDoc}
     * @param role {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventPlayerList(DecodedContent content,
                                     SeqRange avatarRange,
                                     SeqRange anchorRange,
                                     SeqRange loginRange,
                                     boolean isLiving,
                                     GameRole role)
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param avatarRange {@inheritDoc}
     * @param votes {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventExecution(DecodedContent content,
                                    SeqRange avatarRange,
                                    int votes )
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param avatarRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventVanish(DecodedContent content,
                                 SeqRange avatarRange )
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param avatarRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventCheckout(DecodedContent content,
                                   SeqRange avatarRange )
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param judgeByRange {@inheritDoc}
     * @param judgeToRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventJudge(DecodedContent content,
                                SeqRange judgeByRange,
                                SeqRange judgeToRange )
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param guardByRange {@inheritDoc}
     * @param guardToRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventGuard(DecodedContent content,
                                SeqRange guardByRange,
                                SeqRange guardToRange )
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param hour {@inheritDoc}
     * @param minute {@inheritDoc}
     * @param minLimit {@inheritDoc}
     * @param maxLimit {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventAskEntry(int hour, int minute,
                                   int minLimit, int maxLimit)
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param hour {@inheritDoc}
     * @param minute {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventAskCommit(int hour, int minute)
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param avatarRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventNoComment(DecodedContent content,
                                    SeqRange avatarRange )
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param winner {@inheritDoc}
     * @param hour {@inheritDoc}
     * @param minute {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventStayEpilogue(Team winner, int hour, int minute)
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param contentRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventContent(DecodedContent content,
                                  SeqRange contentRange )
            throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventContentBreak() throws HtmlParseException{
        return;
    }

    /**
     * {@inheritDoc}
     * @param content {@inheritDoc}
     * @param anchorRange {@inheritDoc}
     * @param contentRange {@inheritDoc}
     * @throws HtmlParseException {@inheritDoc}
     */
    @Override
    public void sysEventContentAnchor(DecodedContent content,
                                         SeqRange anchorRange,
                                         SeqRange contentRange )
            throws HtmlParseException{
        return;
    }

}
