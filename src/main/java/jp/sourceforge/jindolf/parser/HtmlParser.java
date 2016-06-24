/*
 * XHTML parser
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

import java.util.regex.Pattern;
import jp.sourceforge.jindolf.corelib.PeriodType;
import jp.sourceforge.jindolf.corelib.VillageState;

/**
 * 人狼BBS各種XHTML文字列のパースを行いハンドラに通知する。
 */
public class HtmlParser extends AbstractParser{

    private static final String SP = "\u0020";


    private BasicHandler basicHandler;
    private final TalkParser     talkParser     = new TalkParser(this);
    private final SysEventParser sysEventParser = new SysEventParser(this);

    private final SeqRange rangepool_1 = new SeqRange();
    private final SeqRange rangepool_2 = new SeqRange();

    /**
     * コンストラクタ。
     */
    public HtmlParser(){
        super();
        return;
    }

    /**
     * {@link BasicHandler}ハンドラを登録する。
     * @param basicHandler ハンドラ
     */
    public void setBasicHandler(BasicHandler basicHandler){
        this.basicHandler = basicHandler;
        return;
    }

    /**
     * {@link TalkHandler}ハンドラを登録する。
     * @param talkHandler ハンドラ
     */
    public void setTalkHandler(TalkHandler talkHandler){
        this.talkParser.setTalkHandler(talkHandler);
        return;
    }

    /**
     * {@link SysEventHandler}ハンドラを登録する。
     * @param handler ハンドラ
     */
    public void setSysEventHandler(SysEventHandler handler){
        this.sysEventParser.setSysEventHandler(handler);
        return;
    }

    private static final Pattern XMLDECL_PATTERN =
            compile("<\\?xml\u0020");
    private static final Pattern O_HTML_PATTERN =
            compile("<html\u0020");
    private static final Pattern TITLE_PATTERN =
            compile("<title>([^<]*)</title>");
    private static final Pattern O_BODY_PATTERN =
            compile("<body>");
    private static final Pattern O_DIVMAIN_PATTERN =
            compile("<div\u0020class=\"main\">");

    /**
     * XHTML先頭部分のパース。
     * @throws HtmlParseException パースエラー
     */
    private void parseHead() throws HtmlParseException{
        setContextErrorMessage("lost head part");

        SeqRange titleRange = this.rangepool_1;

        lookingAtAffirm(XMLDECL_PATTERN);
        shrinkRegion();

        findAffirm(O_HTML_PATTERN);
        shrinkRegion();

        findAffirm(TITLE_PATTERN);
        titleRange.setLastMatchedGroupRange(getMatcher(), 1);
        shrinkRegion();

        this.basicHandler.pageTitle(getContent(), titleRange);

        findAffirm(O_BODY_PATTERN);
        shrinkRegion();

        findAffirm(O_DIVMAIN_PATTERN);
        shrinkRegion();

        return;
    }

    private static final Pattern LOGINFORM_PATTERN =
            compile(
                  "("
                    +"<form"
                    +SP + "action=\"index\\.rb\""
                    +SP + "method=\"post\""
                    +SP + "class=\"login_form\""
                    +">"
                + ")|("
                    +"<div"
                    +SP + "class=\"login_form\""
                    +">"
                + ")"
            );
    private static final Pattern C_EDIV_PATTERN =
            compile(
                  SP_I
                + "<a\u0020href=\"[^\"]*\">[^<]*</a>"
                + SP_I
                + "</div>"
            );
    private static final Pattern USERID_PATTERN =
            compile(
                  "name=\"user_id\""
                + SP
                + "value=\"([^\"]*)\""
            );
    private static final Pattern C_FORM_PATTERN =
            compile("</form>");

    /**
     * ログインフォームのパース。
     * ログイン名までの認識を確認したのはF国のみ。
     * @throws HtmlParseException パースエラー
     */
    private void parseLoginForm() throws HtmlParseException{
        setContextErrorMessage("lost login form");

        SeqRange accountRange = this.rangepool_1;

        boolean isLand_E_Form;
        findAffirm(LOGINFORM_PATTERN);
        if(isGroupMatched(1)){
            isLand_E_Form = false;
        }else{                         // E国ログインフォーム検出
            isLand_E_Form = true;
        }
        shrinkRegion();

        if(isLand_E_Form){
            lookingAtAffirm(C_EDIV_PATTERN);
            shrinkRegion();
            return;
        }else{
            findAffirm(USERID_PATTERN);
            accountRange.setLastMatchedGroupRange(getMatcher(), 1);
            shrinkRegion();

            if(accountRange.length() > 0){
                this.basicHandler
                    .loginName(getContent(), accountRange);
            }

            findAffirm(C_FORM_PATTERN);
            shrinkRegion();
        }

        return;
    }

    private static final Pattern VILLAGEINFO_PATTERN =
            compile(
                 "([^<]+?)" +SP_I          // 最短一致数量子
                +"<strong>"
                    +"\uff08"
                    +"([0-9]+)"                       // 月
                    +"/"
                    +"([0-9]+)"                       // 日
                    +SP
                    +"(?:(?:(午前)|(午後))\u0020)?"  // AMPM
                    +"([0-9]+)"                       // 時
                    +"(?:時\u0020|\\:)"
                    +"([0-9]+)"                       // 分
                    +"分?\u0020に更新"
                    +"\uff09"
                +"</strong>"
            );

    /**
     * 村に関する各種情報をパース。
     * @throws HtmlParseException パースエラー
     */
    private void parseVillageInfo() throws HtmlParseException{
        setContextErrorMessage("lose village information");

        SeqRange villageRange = this.rangepool_1;

        sweepSpace();

        lookingAtAffirm(VILLAGEINFO_PATTERN);
        villageRange.setLastMatchedGroupRange(getMatcher(), 1);

        int month  = parseGroupedInt(2);
        int day    = parseGroupedInt(3);
        int hour   = parseGroupedInt(6);
        int minute = parseGroupedInt(7);
        if(isGroupMatched(5)){  // 午後指定
            hour = (hour + 12) % 24;
        }
        shrinkRegion();

        this.basicHandler.villageName(getContent(), villageRange);
        this.basicHandler.commitTime(month, day, hour, minute);

        return;
    }

    private static final Pattern O_PARAG_PATTERN = compile("<p>");
    private static final Pattern PERIODLINK_PATTERN =
            compile(
            "("
                + "<span\u0020class=\"time\">"
            +")|(?:"
                + "<a\u0020href=\"([^\"]*)\">"
            +")|("
                + "</p>"
            +")"
            );
    private static final Pattern PERIOD_PATTERN =
            compile(
                  "(プロローグ)"
            +"|"
                + "(エピローグ)"
            +"|"
                + "(終了)"
            +"|"
                + "([0-9]+)日目"
            );
    private static final Pattern C_SPAN_PATTERN   = compile("</span>");
    private static final Pattern C_ANCHOR_PATTERN = compile("</a>");

    /**
     * Period間リンクをパース。
     * @throws HtmlParseException パースエラー
     */
    private void parsePeriodLink() throws HtmlParseException{
        setContextErrorMessage("lost period link");

        SeqRange anchorRange = this.rangepool_1;

        findAffirm(O_PARAG_PATTERN);
        shrinkRegion();

        for(;;){
            Pattern closePattern;
            anchorRange.setInvalid();

            sweepSpace();
            lookingAtAffirm(PERIODLINK_PATTERN);
            if(isGroupMatched(1)){
                closePattern = C_SPAN_PATTERN;
            }else if(isGroupMatched(2)){
                closePattern = C_ANCHOR_PATTERN;
                anchorRange.setLastMatchedGroupRange(getMatcher(), 2);
            }else if(isGroupMatched(3)){
                shrinkRegion();
                break;
            }else{
                assert false;
                throw buildParseException();
            }
            shrinkRegion();

            int day = -1;
            PeriodType periodType = null;
            lookingAtAffirm(PERIOD_PATTERN);
            if(isGroupMatched(1)){
                periodType = PeriodType.PROLOGUE;
            }else if(isGroupMatched(2)){
                periodType = PeriodType.EPILOGUE;
            }else if(isGroupMatched(3)){
                periodType = null;
            }else if(isGroupMatched(4)){
                periodType = PeriodType.PROGRESS;
                day = parseGroupedInt(4);
            }else{
                assert false;
                throw buildParseException();
            }
            shrinkRegion();

            lookingAtAffirm(closePattern);
            shrinkRegion();

            this.basicHandler.periodLink(getContent(),
                                         anchorRange,
                                         periodType, day );
        }

        return;
    }

    private static final Pattern O_MESSAGE_PATTERN =
            compile("<div\u0020class=\"message(?:\u0020ch[0-9]+)?\">");
    private static final Pattern O_RELOAD_PATTERN =
            compile("<div\u0020id=\"reload\">");
    private static final Pattern O_MSGKIND_PATTERN =
            compile(
             "(?:"
                +"<div\u0020class=\"(?:(announce)|(order)|(extra))\">"
            +")|(?:"
                +"(?:"
                +"(?:<a name=\"[^\"]*\">)?"
                +SP_I
                +"<span\u0020class=\"mes_no\">"
                    +"([0-9]+)\\."
                +"</span>)?"
                +SP_I
                +"(?:</a>)?"
                +SP_I
                +"<a\u0020name=\"([^\"]*)\"(?:\u0020class=\"ch_name\")?>"
            +")"
            );
    private static final Pattern C_DIV_PATTERN = compile("</div>");

    /**
     * 各種メッセージをパース。
     * @throws HtmlParseException パースエラー
     */
    private void parseMessage() throws HtmlParseException{
        setContextErrorMessage("lost message");

        boolean skipGarbage = true;

        for(;;){
            sweepSpace();

            boolean matched;
            if(skipGarbage){
                skipGarbage = false;
                matched = findProbe(O_MESSAGE_PATTERN); // 最初の1回のみ
            }else{
                matched = lookingAtProbe(O_MESSAGE_PATTERN);
            }
            if( ! matched ){
                matched = lookingAtProbe(O_RELOAD_PATTERN);
                if(matched){
                    shrinkRegion();
                    findAffirm(C_DIV_PATTERN);
                    shrinkRegion();
                    continue;
                }
                break;
            }
            shrinkRegion();

            dispatchFamily();

            lookingAtAffirm(C_DIV_PATTERN);
            shrinkRegion();
        }

        return;
    }

    /**
     * イベント種別によって処理を振り分ける。
     * @throws HtmlParseException パースエラー
     */
    private void dispatchFamily() throws HtmlParseException{
        sweepSpace();

        SeqRange nameRange = this.rangepool_1;

        lookingAtAffirm(O_MSGKIND_PATTERN);
        if(isGroupMatched(1)){
            shrinkRegion();
            this.sysEventParser.parseAnnounce();
        }else if(isGroupMatched(2)){
            shrinkRegion();
            this.sysEventParser.parseOrder();
        }else if(isGroupMatched(3)){
            shrinkRegion();
            this.sysEventParser.parseExtra();
        }else if(isGroupMatched(5)){
            nameRange.setLastMatchedGroupRange(getMatcher(), 5);
            int talkNo = -1;
            if(isGroupMatched(4)){
                talkNo = parseGroupedInt(4);
            }
            shrinkRegion();
            this.talkParser.parseTalk(talkNo, nameRange);
        }else{
            assert false;
            throw buildParseException();
        }

        return;
    }

    private static final Pattern O_LISTTABLE_PATTERN =
            compile("<table\u0020class=\"list\">"
                   +"(?:"
                   +  "<tr>"
                   +    "<th>村名</th>"
                   +    "<th>Mode</th>"
                   +    "<th>更新</th>"
                   +    "<th>状態</th>"
                   +  "</tr>"
                   +")?");
    private static final Pattern ACTIVEVILLAGE =
            compile(
             "("
                +"</table>"
            +")|(?:"
                +"<tr><td>"
                +"<a\u0020href=\"([^\"]*)\">([^<]*)</a>"
                +"(?:\u0020|</td><td>"
                +"(?:<strong>)?(?:通常|初心者優先|[^<]*)(?:</strong>)?"
                +"</td><td>)"
                +"<strong>"
                    +"(?:\uff08(?:(午前)|(午後))\u0020)?"  // AMPM
                    +"([0-9]+)"                              // 時
                    +"(?:時\u0020|\\:)"
                    +"([0-9]+)"                              // 分
                    +"(?:\u0020|分\u0020更新\uff09)"
                +"</strong>"
                +"</td><td>"
                +"(?:"
                    + "(参加者募集中(?:です。)?)"
                    +"|(開始待ち(?:です。)?)"
                    +"|(進行中(?:です。)?)"
                    +"|(勝敗が決定しました。|エピローグ)"
                    +"|(終了・ログ公開中。)"
                +")"
                +"</td></tr>"
            +")"
            );

    /**
     * トップページの村一覧表のパース。
     * @throws HtmlParseException パースエラー
     */
    private void parseTopList() throws HtmlParseException{
        setContextErrorMessage("lost village list");

        SeqRange anchorRange  = this.rangepool_1;
        SeqRange villageRange = this.rangepool_2;

        if( ! findProbe(O_LISTTABLE_PATTERN) ) return;
        shrinkRegion();
        sweepSpace();

        for(;;){
            lookingAtAffirm(ACTIVEVILLAGE);
            if(isGroupMatched(1)) break;
            anchorRange .setLastMatchedGroupRange(getMatcher(), 2);
            villageRange.setLastMatchedGroupRange(getMatcher(), 3);
            int hour = parseGroupedInt(6);
            if(isGroupMatched(5)){
                hour = (hour + 12) % 24;
            }
            int minute = parseGroupedInt(7);

            VillageState state;
            if(isGroupMatched(8)){
                state = VillageState.PROLOGUE;
            }else if(isGroupMatched(9)){
                state = VillageState.PROLOGUE;
            }else if(isGroupMatched(10)){
                state = VillageState.PROGRESS;
            }else if(isGroupMatched(11)){
                state = VillageState.EPILOGUE;
            }else if(isGroupMatched(12)){
                state = VillageState.GAMEOVER;
            }else{
                assert false;
                throw buildParseException();
            }

            shrinkRegion();

            sweepSpace();

            this.basicHandler.villageRecord(getContent(),
                                            anchorRange,
                                            villageRange,
                                            hour, minute,
                                            state );
        }

        return;
    }

    private static final Pattern O_LISTLOG_PATTERN =
            compile(
            "<a\u0020href=\"(index[^\"]*(?:ready_0|000_ready))\">"
            +"([^<]*)"
            +"</a><br\u0020/>"
            );

    /**
     * 村一覧ページのパース。
     * @throws HtmlParseException パースエラー
     */
    private void parseLogList() throws HtmlParseException{
        setContextErrorMessage("lost village list");

        SeqRange anchorRange  = this.rangepool_1;
        SeqRange villageRange = this.rangepool_2;

        boolean is1st = true;
        for(;;){
            boolean matched;
            if(is1st){
                matched = findProbe(O_LISTLOG_PATTERN);
                is1st = false;
            }else{
                matched = lookingAtProbe(O_LISTLOG_PATTERN);
            }
            if( ! matched ) break;

            anchorRange .setLastMatchedGroupRange(getMatcher(), 1);
            villageRange.setLastMatchedGroupRange(getMatcher(), 2);

            shrinkRegion();

            this.basicHandler.villageRecord(getContent(),
                                            anchorRange,
                                            villageRange,
                                            -1, -1,
                                            VillageState.GAMEOVER );
        }

        return;
    }

    private static final Pattern C_BODY_PATTERN =
            compile("</body>");
    private static final Pattern C_HTML_PATTERN =
            compile(SP_I+ "</html>" +SP_I);

    /**
     * XHTML末尾のパース。
     * @throws HtmlParseException パースエラー
     */
    private void parseTail() throws HtmlParseException{
        setContextErrorMessage("lost last part");

        findAffirm(C_BODY_PATTERN);
        shrinkRegion();

        matchesAffirm(C_HTML_PATTERN);
        shrinkRegion();

        return;
    }

    private static final Pattern LISTTITLE_PATTERN =
            compile("終了した村の記録");

    /**
     * 人狼BBSのページ種別を自動認識しつつパースする。
     * @param content パース対象の文字列
     * @throws HtmlParseException パースエラー
     */
    public void parseAutomatic(DecodedContent content)
            throws HtmlParseException{
        setContent(content);

        this.basicHandler.startParse(getContent());

        parseHead();

        sweepSpace();

        if(lookingAtProbe(LISTTITLE_PATTERN)){
            shrinkRegion();
            this.basicHandler.pageType(PageType.VILLAGELIST_PAGE);
            parseLogList();
        }else{
            parseLoginForm();
            sweepSpace();
            if(lookingAtProbe(O_PARAG_PATTERN)){
                shrinkRegion();
                this.basicHandler.pageType(PageType.TOP_PAGE);
                parseTopList();
            }else{
                this.basicHandler.pageType(PageType.PERIOD_PAGE);
                parseVillageInfo();
                parsePeriodLink();
                parseMessage();
            }
        }

        parseTail();

        this.basicHandler.endParse();

        reset();

        return;
    }

}
