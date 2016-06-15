/*
 * System event parser
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

import java.util.regex.Pattern;
import jp.sourceforge.jindolf.corelib.EventFamily;
import jp.sourceforge.jindolf.corelib.GameRole;
import jp.sourceforge.jindolf.corelib.SysEventType;
import jp.sourceforge.jindolf.corelib.Team;

/**
 * 人狼BBSシステムが出力する各種イベント表記のパースを行うパーサ。
 * パース進行に従い{@link SysEventHandler}の各種メソッドが呼び出される。
 */
public class SysEventParser extends AbstractParser{

    private static final String AVATAR_REGEX =
            "[^<、" + SPCHAR + "]+\u0020[^<、。" + SPCHAR + "]+";

    private static final Pattern C_DIV_PATTERN =
            compile(SP_I+ "</div>" +SP_I);
    private static final Pattern AVATAR_PATTERN =
            compile(AVATAR_REGEX);


    private SysEventHandler sysEventHandler;

    private int pushedRegionStart = -1;
    private int pushedRegionEnd   = -1;

    private final SeqRange rangepool_1 = new SeqRange();
    private final SeqRange rangepool_2 = new SeqRange();
    private final SeqRange rangepool_3 = new SeqRange();

    /**
     * コンストラクタ。
     * @param parent 親パーサ
     */
    public SysEventParser(ChainedParser parent){
        super(parent);
        return;
    }

    /**
     * {@link SysEventHandler}ハンドラを登録する。
     * @param sysEventHandler ハンドラ
     */
    public void setSysEventHandler(SysEventHandler sysEventHandler){
        this.sysEventHandler = sysEventHandler;
        return;
    }

    /**
     * Announceメッセージをパースする。
     * @throws HtmlParseException パースエラー
     */
    public void parseAnnounce() throws HtmlParseException{
        setContextErrorMessage("Unknown Announce message");

        this.sysEventHandler.startSysEvent(EventFamily.ANNOUNCE);

        int regionStart = regionStart();
        int regionEnd   = regionEnd();

        boolean result =
                   probeSimpleAnnounce()
                || probeOpenRole()
                || probeSurvivor()
                || probeMurdered()
                || probeOnStage()
                || probeSuddenDeath()
                || probeCounting()
                || probePlayerList()
                || probeExecution()
                || probeVanish()
                || probeCheckout()
                ;
        if( ! result ){
            throw buildParseException();
        }

        getMatcher().region(regionStart, regionEnd);
        parseContent();

        lookingAtAffirm(C_DIV_PATTERN);
        shrinkRegion();

        this.sysEventHandler.endSysEvent();

        return;
    }

    private static final Pattern STARTENTRY_PATTERN =
             compile(
             "昼間は人間のふりをして、夜に正体を現すという人狼。<br />"
            +"その人狼が、"
            +"この村に紛れ込んでいるという噂が広がった。<br /><br />"
            +"村人達は半信半疑ながらも、"
            +"村はずれの宿に集められることになった。"
            +"<br />");
    private static final Pattern STARTMIRROR_PATTERN =
             compile(
             "さあ、自らの姿を鏡に映してみよう。<br />"
            +"そこに映るのはただの村人か、"
            +"それとも血に飢えた人狼か。<br /><br />"
            +"例え人狼でも、多人数で立ち向かえば怖くはない。<br />"
            +"問題は、だれが人狼なのかという事だ。<br />"
            +"占い師の能力を持つ人間ならば、それを見破れるだろう。"
            +"(?:<br />)?");
    private static final Pattern STARTASSAULT_PATTERN =
             compile(
             "ついに犠牲者が出た。人狼はこの村人達のなかにいる。<br />"
            +"しかし、それを見分ける手段はない。<br /><br />"
            +"村人達は、疑わしい者を排除するため、"
            +"投票を行う事にした。<br />"
            +"無実の犠牲者が出るのもやむをえない。"
            +"村が全滅するよりは……。<br /><br />"
            +"最後まで残るのは村人か、それとも人狼か。"
            +"(?:<br />)?");
    private static final Pattern NOMURDER_PATTERN =
             compile(
             "今日は犠牲者がいないようだ。人狼は襲撃に失敗したのだろうか。");
    private static final Pattern WINVILLAGE_PATTERN =
             compile(
             "全ての人狼を退治した……。人狼に怯える日々は去ったのだ！"
            +"(?:<br />)?");
    private static final Pattern WINWOLF_PATTERN =
             compile(
             "もう人狼に抵抗できるほど村人は残っていない……。<br />"
            +"人狼は残った村人を全て食らい、"
            +"別の獲物を求めてこの村を去っていった。"
            +"(?:<br />)?");
    private static final Pattern WINHAMSTER_PATTERN =
             compile(
              "全ては終わったかのように見えた。<br />"
             +"だが、奴が生き残っていた……。");
    private static final Pattern PANIC_PATTERN =
             compile("……。");
    private static final Pattern SHORTMEMBER_PATTERN =
             compile(
             "まだ村人達は揃っていないようだ。"
            +"(?:<br />)?");

    private static final Object[][] SIMPLE_REGEX_TO_TYPE = {
        { STARTENTRY_PATTERN,   SysEventType.STARTENTRY   },
        { STARTMIRROR_PATTERN,  SysEventType.STARTMIRROR  },
        { STARTASSAULT_PATTERN, SysEventType.STARTASSAULT },
        { NOMURDER_PATTERN,     SysEventType.NOMURDER     },
        { WINVILLAGE_PATTERN,   SysEventType.WINVILLAGE   },
        { WINWOLF_PATTERN,      SysEventType.WINWOLF      },
        { WINHAMSTER_PATTERN,   SysEventType.WINHAMSTER   },
        { PANIC_PATTERN,        SysEventType.PANIC        },
        { SHORTMEMBER_PATTERN,  SysEventType.SHORTMEMBER  },
    };

    /**
     * 文字列が固定されたシンプルなAnnounceメッセージのパースを試みる。
     * @return マッチしたらtrue
     * @throws HtmlParseException パースエラー
     */
    private boolean probeSimpleAnnounce() throws HtmlParseException{
        pushRegion();

        sweepSpace();

        SysEventType matchedType = null;

        for(Object[] pair : SIMPLE_REGEX_TO_TYPE){
            Pattern pattern = (Pattern) pair[0];

            if(lookingAtProbe(pattern)){
                shrinkRegion();
                matchedType = (SysEventType) pair[1];
                break;
            }
        }

        if(matchedType == null){
            popRegion();
            return false;
        }

        this.sysEventHandler.sysEventType(matchedType);

        sweepSpace();

        return true;
    }

    private static final Pattern OPENROLE_HEAD_PATTERN =
            compile("どうやらこの中には、");
    private static final Pattern OPENROLE_NUM_PATTERN =
            compile("が([0-9]+)名(?:、)?");
    private static final Pattern OPENROLE_TAIL_PATTERN =
            compile("いるようだ。");

    /**
     * OPENROLEメッセージのパースを試みる。
     * @return マッチしたらtrue
     * @throws HtmlParseException パースエラー
     */
    private boolean probeOpenRole() throws HtmlParseException{
        pushRegion();

        sweepSpace();

        if( ! lookingAtProbe(OPENROLE_HEAD_PATTERN) ){
            popRegion();
            return false;
        }
        shrinkRegion();

        this.sysEventHandler.sysEventType(SysEventType.OPENROLE);

        for(;;){
            GameRole role = lookingAtRole();
            if(role == null){
                if( lookingAtProbe(OPENROLE_TAIL_PATTERN) ){
                    shrinkRegion();
                    break;
                }
                popRegion();
                return false;
            }
            shrinkRegion();

            if( ! lookingAtProbe(OPENROLE_NUM_PATTERN) ){
                popRegion();
                return false;
            }
            int num = parseGroupedInt(1);
            shrinkRegion();

            this.sysEventHandler.sysEventOpenRole(role, num);
        }

        sweepSpace();

        return true;
    }

    private static final Pattern SURVIVOR_HEAD_PATTERN =
            compile("現在の生存者は、");
    private static final Pattern SURVIVOR_PATTERN =
            Pattern.compile(
            "(" + AVATAR_REGEX + ")"
            +"(?:"
                +"(?:"
                    +"、"
                +")|(?:"
                    +"\u0020の\u0020([0-9]+)\u0020名。"
                +")"
            +")");

    /**
     * SURVIVORメッセージのパースを試みる。
     * @return マッチしたらtrue
     * @throws HtmlParseException パースエラー
     */
    private boolean probeSurvivor() throws HtmlParseException{
        SeqRange avatarRange = this.rangepool_1;

        pushRegion();

        sweepSpace();

        if( ! lookingAtProbe(SURVIVOR_HEAD_PATTERN) ){
            popRegion();
            return false;
        }
        shrinkRegion();

        this.sysEventHandler.sysEventType(SysEventType.SURVIVOR);

        int avatarNum = 0;
        for(;;){
            if( ! lookingAtProbe(SURVIVOR_PATTERN) ){
                popRegion();
                return false;
            }
            avatarRange.setLastMatchedGroupRange(getMatcher(), 1);
            this.sysEventHandler
                .sysEventSurvivor(getContent(), avatarRange);
            avatarNum++;
            if(isGroupMatched(2)){
                int num = parseGroupedInt(2);
                shrinkRegion();
                if(num != avatarNum){
                    throw new HtmlParseException(regionStart());
                }
                break;
            }
            shrinkRegion();
        }

        sweepSpace();

        return true;
    }

    private static final Pattern MURDERED_HEAD_PATTERN =
            compile("次の日の朝、");
    private static final Pattern MURDERED_SW_PATTERN =
            compile(
                "("
                    +"\u0020と\u0020"
                +")|("
                    +"\u0020が無残な姿で発見された。"
                    +"(?:<br />)?"  // E国対策
                +")"
            );

    /**
     * MURDEREDメッセージのパースを試みる。
     * @return マッチしたらtrue
     * @throws HtmlParseException パースエラー
     */
    private boolean probeMurdered() throws HtmlParseException{
        SeqRange avatarRange  = this.rangepool_1;
        SeqRange avatarRange2 = this.rangepool_2;
        avatarRange .setInvalid();
        avatarRange2.setInvalid();

        pushRegion();

        sweepSpace();

        if( ! lookingAtProbe(MURDERED_HEAD_PATTERN)){
            popRegion();
            return false;
        }
        shrinkRegion();

        this.sysEventHandler.sysEventType(SysEventType.MURDERED);

        for(;;){
            if( ! lookingAtProbe(AVATAR_PATTERN)){
                popRegion();
                return false;
            }
            if( ! avatarRange.isValid() ){
                avatarRange.setLastMatchedRange(getMatcher());
            }else if( ! avatarRange2.isValid() ){
                avatarRange2.setLastMatchedRange(getMatcher());
            }else{
                assert false;
                throw buildParseException();
            }
            shrinkRegion();

            if( ! lookingAtProbe(MURDERED_SW_PATTERN)){
                popRegion();
                return false;
            }
            if(isGroupMatched(1)){
                shrinkRegion();
                continue;
            }else if(isGroupMatched(2)){
                shrinkRegion();
                break;
            }else{
                assert false;
                throw buildParseException();
            }
        }

        this.sysEventHandler
            .sysEventMurdered(getContent(), avatarRange);
        if(avatarRange2.isValid()){
            this.sysEventHandler
                .sysEventMurdered(getContent(), avatarRange2);
        }

        sweepSpace();

        return true;
    }

    private static final Pattern ONSTAGE_NO_PATTERN =
            compile("([0-9]+)人目、");
    private static final Pattern ONSTAGE_DOT_PATTERN =
            compile(
             "("
            +"(?:" + AVATAR_REGEX + ")"
            +"|)"    // F1556プロローグ対策
            +"。");

    /**
     * ONSTAGEメッセージのパースを試みる。
     * @return マッチしたらtrue
     * @throws HtmlParseException パースエラー
     */
    private boolean probeOnStage() throws HtmlParseException{
        SeqRange avatarRange = this.rangepool_1;

        pushRegion();

        sweepSpace();

        if( ! lookingAtProbe(ONSTAGE_NO_PATTERN) ){
            popRegion();
            return false;
        }
        int entryNo = parseGroupedInt(1);
        shrinkRegion();

        this.sysEventHandler.sysEventType(SysEventType.ONSTAGE);

        if( ! lookingAtProbe(ONSTAGE_DOT_PATTERN) ){
            popRegion();
            return false;
        }
        avatarRange.setLastMatchedGroupRange(getMatcher(), 1);
        shrinkRegion();

        this.sysEventHandler
            .sysEventOnStage(getContent(), entryNo, avatarRange);

        sweepSpace();

        return true;
    }

    private static final Pattern SUDDENDEATH_PATTERN =
            compile(
                 "("
                +"(?:" + AVATAR_REGEX + ")"
                +"|)"                            // F681 2d 対策
                +"\u0020?は、突然死した。"
            );

    /**
     * SUDDENDEATHメッセージのパースを試みる。
     * @return マッチしたらtrue
     * @throws HtmlParseException パースエラー
     */
    private boolean probeSuddenDeath() throws HtmlParseException{
        SeqRange avatarRange = this.rangepool_1;

        pushRegion();

        sweepSpace();

        if( ! lookingAtProbe(SUDDENDEATH_PATTERN)){
            popRegion();
            return false;
        }
        avatarRange.setLastMatchedGroupRange(getMatcher(), 1);
        shrinkRegion();

        this.sysEventHandler.sysEventType(SysEventType.SUDDENDEATH);
        this.sysEventHandler
            .sysEventSuddenDeath(getContent(), avatarRange);

        sweepSpace();

        return true;
    }

    private static final Pattern COUNTING_PATTERN =
            compile(
            "(?:"
                +"<br />"
                +"(" + AVATAR_REGEX + ")"
                +"\u0020は村人達の手により処刑された。"
            +")|(?:"
                +"(" + AVATAR_REGEX + ")"
                +"\u0020は\u0020"
                +"(" + AVATAR_REGEX + ")"
                +"\u0020に投票した。"
                +"(?:<br />)?"
            +")"
            );

    /**
     * COUNTINGメッセージのパースを試みる。
     * @return マッチしたらtrue
     * @throws HtmlParseException パースエラー
     */
    private boolean probeCounting() throws HtmlParseException{
        SeqRange voteByRange = this.rangepool_1;
        SeqRange voteToRange = this.rangepool_2;

        pushRegion();

        sweepSpace();

        boolean hasVote = false;
        for(;;){
            if( ! lookingAtProbe(COUNTING_PATTERN) ){
                break; // 処刑なし
            }
            if(isGroupMatched(1)){
                voteByRange.setInvalid();
                voteToRange.setLastMatchedGroupRange(getMatcher(), 1);
                shrinkRegion();
                this.sysEventHandler
                    .sysEventCounting(getContent(),
                                      voteByRange,
                                      voteToRange );
                break;
            }else if(isGroupMatched(2)){
                if( ! hasVote ){
                    hasVote = true;
                    this.sysEventHandler.sysEventType(SysEventType.COUNTING);
                }
                voteByRange.setLastMatchedGroupRange(getMatcher(), 2);
                voteToRange.setLastMatchedGroupRange(getMatcher(), 3);
                shrinkRegion();
                this.sysEventHandler
                    .sysEventCounting(getContent(),
                                      voteByRange,
                                      voteToRange );
            }else{
                assert false;
                throw buildParseException();
            }
        }

        if( ! hasVote ){
            popRegion();
            return false;
        }

        sweepSpace();

        return true;
    }

    private static final Pattern COUNTING2_PATTERN =
            compile(
                 "(" + AVATAR_REGEX + ")"
                +"\u0020は\u0020"
                +"(" + AVATAR_REGEX + ")"
                +"\u0020に投票した。"
                +"(?:<br />)?"
            );

    /**
     * COUNTING2メッセージのパースを試みる。
     * @return マッチしたらtrue
     * @throws HtmlParseException パースエラー
     */
    private boolean probeCounting2() throws HtmlParseException{
        SeqRange voteByRange = this.rangepool_1;
        SeqRange voteToRange = this.rangepool_2;

        pushRegion();

        sweepSpace();

        boolean hasVote = false;
        for(;;){
            if( ! lookingAtProbe(COUNTING2_PATTERN) ){
                break;
            }
            if( ! hasVote ){
                hasVote = true;
                this.sysEventHandler.sysEventType(SysEventType.COUNTING2);
            }
            voteByRange.setLastMatchedGroupRange(getMatcher(), 1);
            voteToRange.setLastMatchedGroupRange(getMatcher(), 2);
            shrinkRegion();
            this.sysEventHandler
                .sysEventCounting2(getContent(),
                                   voteByRange,
                                   voteToRange );
        }

        if( ! hasVote ){
            popRegion();
            return false;
        }

        sweepSpace();

        return true;
    }

    private static final Pattern PLAYERID_PATTERN =
            compile(
                "\u0020\uff08" // 全角開き括弧
                +"(?:<a\u0020href=\"([^\"]*)\">)?"
                +"([^<]*)"
                +"(?:</a>)?"
                +"\uff09、"     // 全角閉じ括弧
            );
    private static final Pattern LIVEORDIE_PATTERN =
            compile(
                "(生存。)|(死亡。)"
            );
    private static final Pattern PLAYER_DELIM_PATTERN =
            compile(
                 "だった。"
                +"(?:<br />)?"
            );

    /**
     * PLAYERLISTメッセージのパースを試みる。
     * @return マッチしたらtrue
     * @throws HtmlParseException パースエラー
     */
    private boolean probePlayerList() throws HtmlParseException{
        SeqRange avatarRange  = this.rangepool_1;
        SeqRange anchorRange  = this.rangepool_2;
        SeqRange accountRange = this.rangepool_3;

        pushRegion();

        sweepSpace();

        boolean hasPlayerList = false;

        for(;;){
            if( ! lookingAtProbe(AVATAR_PATTERN)){
                break;
            }
            avatarRange.setLastMatchedRange(getMatcher());
            shrinkRegion();

            if( ! lookingAtProbe(PLAYERID_PATTERN)){
                popRegion();
                return false;
            }
            if(isGroupMatched(1)){
                anchorRange.setLastMatchedGroupRange(getMatcher(), 1);
            }else{
                anchorRange.setInvalid();
            }
            accountRange.setLastMatchedGroupRange(getMatcher(), 2);
            shrinkRegion();

            boolean isLiving = false;
            if( ! lookingAtProbe(LIVEORDIE_PATTERN)){
                popRegion();
                return false;
            }
            if(isGroupMatched(1)){
                isLiving = true;
            }else if(isGroupMatched(2)){
                isLiving = false;
            }
            shrinkRegion();

            GameRole role = lookingAtRole();
            if(role == null){
                popRegion();
                return false;
            }
            shrinkRegion();

            if( ! lookingAtProbe(PLAYER_DELIM_PATTERN)){
                popRegion();
                return false;
            }
            shrinkRegion();

            if( ! hasPlayerList ){
                hasPlayerList = true;
                this.sysEventHandler.sysEventType(SysEventType.PLAYERLIST);
            }

            this.sysEventHandler
                .sysEventPlayerList(getContent(),
                                    avatarRange,
                                    anchorRange,
                                    accountRange,
                                    isLiving,
                                    role );
        }

        if( ! hasPlayerList ){
            popRegion();
            return false;
        }

        sweepSpace();

        return true;
    }

    private static final Pattern EXECUTION_PATTERN =
            compile(
                "(?:"
                + "(" + AVATAR_REGEX + ")、([0-9]+)票。(?:<br />)?"
                +")|(?:"
                +"<br />(" + AVATAR_REGEX + ")\u0020は"
                +"村人達の手により処刑された。"
                +")"
            );

    /**
     * EXECUTIONメッセージのパースを試みる。
     * @return マッチしたらtrue
     * @throws HtmlParseException パースエラー
     */
    private boolean probeExecution() throws HtmlParseException{
        SeqRange avatarRange  = this.rangepool_1;

        pushRegion();

        sweepSpace();

        boolean hasExecution = false;

        for(;;){
            if( ! lookingAtProbe(EXECUTION_PATTERN)){
                break;
            }

            if( ! hasExecution ){
                hasExecution = true;
                this.sysEventHandler.sysEventType(SysEventType.EXECUTION);
            }

            if(isGroupMatched(1)){
                avatarRange.setLastMatchedGroupRange(getMatcher(), 1);
                int votes = parseGroupedInt(2);
                shrinkRegion();
                this.sysEventHandler
                    .sysEventExecution(getContent(),
                                       avatarRange,
                                       votes );
            }else if(isGroupMatched(3)){
                avatarRange.setLastMatchedGroupRange(getMatcher(), 3);
                shrinkRegion();
                this.sysEventHandler
                    .sysEventExecution(getContent(),
                                       avatarRange,
                                       -1 );
            }
        }

        if( ! hasExecution ){
            popRegion();
            return false;
        }

        sweepSpace();

        return true;
    }

    private static final Pattern VANISH_PATTERN =
            compile(
                 "(?:<br />)*"
                +"(" + AVATAR_REGEX + ")"
                +"\u0020は、失踪した。"
                +"(?:<br />)*"
            );

    /**
     * VANISHメッセージのパースを試みる。
     * @return マッチしたらtrue
     * @throws HtmlParseException パースエラー
     */
    private boolean probeVanish() throws HtmlParseException{
        SeqRange avatarRange  = this.rangepool_1;

        pushRegion();

        sweepSpace();

        boolean hasVanish = false;

        for(;;){
            if( ! lookingAtProbe(VANISH_PATTERN)){
                break;
            }

            if( ! hasVanish ){
                hasVanish = true;
                this.sysEventHandler.sysEventType(SysEventType.VANISH);
            }
            avatarRange.setLastMatchedGroupRange(getMatcher(), 1);

            shrinkRegion();

            this.sysEventHandler
                .sysEventVanish(getContent(), avatarRange);
        }

        if( ! hasVanish ){
            popRegion();
            return false;
        }

        sweepSpace();

        return true;
    }

    private static final Pattern CHECKOUT_PATTERN =
            compile(
                 "(?:<br />)*"
                +"(" + AVATAR_REGEX + ")"
                +"\u0020は、宿を去った。"
                +"(?:<br />)*"
            );

    /**
     * CHECKOUTメッセージのパースを試みる。
     * @return マッチしたらtrue
     * @throws HtmlParseException パースエラー
     */
    private boolean probeCheckout() throws HtmlParseException{
        SeqRange avatarRange  = this.rangepool_1;

        pushRegion();

        sweepSpace();

        boolean hasCheckout = false;

        for(;;){
            if( ! lookingAtProbe(CHECKOUT_PATTERN)){
                break;
            }

            if( ! hasCheckout ){
                hasCheckout = true;
                this.sysEventHandler.sysEventType(SysEventType.CHECKOUT);
            }
            avatarRange.setLastMatchedGroupRange(getMatcher(), 1);

            shrinkRegion();

            this.sysEventHandler
                .sysEventCheckout(getContent(), avatarRange);
        }

        if( ! hasCheckout ){
            popRegion();
            return false;
        }

        sweepSpace();

        return true;
    }

    /**
     * Orderメッセージをパースする。
     * @throws HtmlParseException パースエラー
     */
    public void parseOrder() throws HtmlParseException{
        setContextErrorMessage("Unknown Order message");

        this.sysEventHandler.startSysEvent(EventFamily.ORDER);

        int regionStart = regionStart();
        int regionEnd   = regionEnd();

        boolean result =
                   probeAskEntry()
                || probeAskCommit()
                || probeNoComment()
                || probeStayEpilogue()
                || probeGameOver()
                ;
        if( ! result ){
            throw buildParseException();
        }

        getMatcher().region(regionStart, regionEnd);
        parseContent();

        lookingAtAffirm(C_DIV_PATTERN);
        shrinkRegion();

        this.sysEventHandler.endSysEvent();

        return;
    }

    private static final Pattern ASKENTRY_PATTERN =
            compile(
             "演じたいキャラクターを選び、発言してください。<br />"
            +"([0-2][0-9]):([0-5][0-9])\u0020に"
            +"([0-9]+)名以上がエントリーしていれば進行します。<br />"
            +"最大([0-9]+)名まで参加可能です。<br /><br />"
            +"※[\u0020]?エントリーは取り消せません。"
            +"ルールをよく理解した上でご参加下さい。<br />"
            +"(?:※始めての方は、村人希望での参加となります。<br />)?"
            +"(?:※希望能力についての発言は控えてください。<br />)?"
            );

    /**
     * ASKENTRYメッセージのパースを試みる。
     * @return マッチしたらtrue
     * @throws HtmlParseException パースエラー
     */
    private boolean probeAskEntry() throws HtmlParseException{
        pushRegion();

        sweepSpace();

        if( ! lookingAtProbe(ASKENTRY_PATTERN)){
            popRegion();
            return false;
        }

        int hour     = parseGroupedInt(1);
        int minute   = parseGroupedInt(2);
        int minLimit = parseGroupedInt(3);
        int maxLimit = parseGroupedInt(4);

        shrinkRegion();

        this.sysEventHandler.sysEventType(SysEventType.ASKENTRY);
        this.sysEventHandler
            .sysEventAskEntry(hour, minute, minLimit, maxLimit);

        sweepSpace();

        return true;
    }

    private static final Pattern ASKCOMMIT_PATTERN =
            compile(
             "(?:"
            +"([0-2][0-9]):([0-5][0-9])\u0020までに、"
            +"誰を処刑するべきかの投票先を決定して下さい。<br />"
            +"一番票を集めた人物が処刑されます。"
            +"同数だった場合はランダムで決定されます。<br /><br />"
            +")?"
            +"特殊な能力を持つ人は、"
            +"([0-2][0-9]):([0-5][0-9])\u0020までに"
            +"行動を確定して下さい。<br />"
            );

    /**
     * ASKCOMMITメッセージのパースを試みる。
     * @return マッチしたらtrue
     * @throws HtmlParseException パースエラー
     */
    private boolean probeAskCommit() throws HtmlParseException{
        pushRegion();

        sweepSpace();

        if( ! lookingAtProbe(ASKCOMMIT_PATTERN)){
            popRegion();
            return false;
        }

        boolean is1stDay;
        if(isGroupMatched(1)){
            is1stDay = false;
        }else{
            is1stDay = true;
        }

        int hh1 = parseGroupedInt(1);
        int mm1 = parseGroupedInt(2);
        int hh2 = parseGroupedInt(3);
        int mm2 = parseGroupedInt(4);

        shrinkRegion();

        if( ! is1stDay && (hh1 != hh2 || mm1 != mm2) ){
            throw new HtmlParseException(regionStart());
        }

        this.sysEventHandler.sysEventType(SysEventType.ASKCOMMIT);
        this.sysEventHandler.sysEventAskCommit(hh2, mm2);

        sweepSpace();

        return true;
    }

    private static final Pattern NOCOMMENT_HEAD_PATTERN =
            compile("本日まだ発言していない者は、");
    private static final Pattern NOCOMMENT_AVATAR_PATTERN =
            compile(
             "(?:"
                +"(" + AVATAR_REGEX + ")、"
            +")|(?:"
                +"以上\u0020([0-9]+)\u0020名。"
            +")"
            );

    /**
     * NOCOMMENTメッセージのパースを試みる。
     * @return マッチしたらtrue
     * @throws HtmlParseException パースエラー
     */
    private boolean probeNoComment() throws HtmlParseException{
        SeqRange avatarRange = this.rangepool_1;

        pushRegion();

        sweepSpace();

        if( ! lookingAtProbe(NOCOMMENT_HEAD_PATTERN)){
            popRegion();
            return false;
        }
        shrinkRegion();

        this.sysEventHandler.sysEventType(SysEventType.NOCOMMENT);

        int avatarNum = 0;
        for(;;){
            if( ! lookingAtProbe(NOCOMMENT_AVATAR_PATTERN)){
                popRegion();
                return false;
            }

            if(isGroupMatched(1)){
                avatarRange.setLastMatchedGroupRange(getMatcher(), 1);
                this.sysEventHandler
                    .sysEventNoComment(getContent(), avatarRange);
                shrinkRegion();
                avatarNum++;
            }else if(isGroupMatched(2)){
                int num = parseGroupedInt(2);
                shrinkRegion();
                if(num != avatarNum){
                    throw new HtmlParseException(regionStart());
                }
                break;
            }
        }

        sweepSpace();

        return true;
    }

    private static final Pattern STAYEPILOGUE_PATTERN =
            compile(
            "(?:(村人)|(人狼)|(ハムスター))側の勝利です！<br />"
            +"全てのログとユーザー名を公開します。"
            +"([0-2][0-9]):([0-5][0-9])\u0020まで"
            +"自由に書き込めますので、"
            +"今回の感想などをどうぞ。<br />"
            );

    /**
     * STAYEPILOGUEメッセージのパースを試みる。
     * @return マッチしたらtrue
     * @throws HtmlParseException パースエラー
     */
    private boolean probeStayEpilogue() throws HtmlParseException{
        pushRegion();

        sweepSpace();

        if( ! lookingAtProbe(STAYEPILOGUE_PATTERN)){
            popRegion();
            return false;
        }

        Team winner = null;
        if(isGroupMatched(1)){
            winner = Team.VILLAGE;
        }else if(isGroupMatched(2)){
            winner = Team.WOLF;
        }else if(isGroupMatched(3)){
            winner = Team.HAMSTER;
        }

        int hour = parseGroupedInt(4);
        int minute = parseGroupedInt(5);

        shrinkRegion();

        this.sysEventHandler.sysEventType(SysEventType.STAYEPILOGUE);
        this.sysEventHandler.sysEventStayEpilogue(winner, hour, minute);

        sweepSpace();

        return true;
    }

    private static final Pattern GAMEOVER_PATTERN =
            compile("終了しました。" + "<br />");

    /**
     * GAMEOVERメッセージのパースを試みる。
     * @return マッチしたらtrue
     * @throws HtmlParseException パースエラー
     */
    private boolean probeGameOver() throws HtmlParseException{
        pushRegion();

        sweepSpace();

        if( ! lookingAtProbe(GAMEOVER_PATTERN)){
            popRegion();
            return false;
        }

        shrinkRegion();

        this.sysEventHandler.sysEventType(SysEventType.GAMEOVER);

        sweepSpace();

        return true;
    }

    /**
     * Extraメッセージをパースする。
     * @throws HtmlParseException パースエラー
     */
    public void parseExtra() throws HtmlParseException{
        setContextErrorMessage("Unknown Extra message");

        this.sysEventHandler.startSysEvent(EventFamily.EXTRA);

        int regionStart = regionStart();
        int regionEnd   = regionEnd();

        boolean result =
                   probeJudge()
                || probeGuard()
                || probeCounting2();
        if( ! result ){
            throw buildParseException();
        }

        getMatcher().region(regionStart, regionEnd);
        parseContent();

        lookingAtAffirm(C_DIV_PATTERN);
        shrinkRegion();

        this.sysEventHandler.endSysEvent();

        return;
    }

    private static final Pattern JUDGE_DELIM_PATTERN =
            compile("\u0020は、");
    private static final Pattern JUDGE_TAIL_PATTERN =
            compile("\u0020を占った。");

    /**
     * JUDGEメッセージのパースを試みる。
     * @return マッチしたらtrue
     * @throws HtmlParseException パースエラー
     */
    private boolean probeJudge() throws HtmlParseException{
        SeqRange judgeByRange = this.rangepool_1;
        SeqRange judgeToRange = this.rangepool_2;

        pushRegion();

        sweepSpace();

        if( ! lookingAtProbe(AVATAR_PATTERN)){
            popRegion();
            return false;
        }
        judgeByRange.setLastMatchedRange(getMatcher());
        shrinkRegion();

        if( ! lookingAtProbe(JUDGE_DELIM_PATTERN)){
            popRegion();
            return false;
        }
        shrinkRegion();

        if( ! lookingAtProbe(AVATAR_PATTERN)){
            popRegion();
            return false;
        }
        judgeToRange.setLastMatchedRange(getMatcher());
        shrinkRegion();

        if( ! lookingAtProbe(JUDGE_TAIL_PATTERN)){
            popRegion();
            return false;
        }
        shrinkRegion();

        this.sysEventHandler.sysEventType(SysEventType.JUDGE);
        this.sysEventHandler
            .sysEventJudge(getContent(),
                           judgeByRange,
                           judgeToRange );
        sweepSpace();

        return true;
    }

    private static final Pattern GUARD_DELIM_PATTERN =
            compile("\u0020は、");
    private static final Pattern GUARD_TAIL_PATTERN =
            compile("\u0020を守っている。");

    /**
     * GUARDメッセージのパースを試みる。
     * @return マッチしたらtrue
     * @throws HtmlParseException パースエラー
     */
    private boolean probeGuard() throws HtmlParseException{
        SeqRange guardByRange = this.rangepool_1;
        SeqRange guardToRange = this.rangepool_2;

        pushRegion();

        sweepSpace();

        if( ! lookingAtProbe(AVATAR_PATTERN)){
            popRegion();
            return false;
        }
        guardByRange.setLastMatchedRange(getMatcher());
        shrinkRegion();

        if( ! lookingAtProbe(GUARD_DELIM_PATTERN)){
            popRegion();
            return false;
        }
        shrinkRegion();

        if( ! lookingAtProbe(AVATAR_PATTERN)){
            popRegion();
            return false;
        }
        guardToRange.setLastMatchedRange(getMatcher());
        shrinkRegion();

        if( ! lookingAtProbe(GUARD_TAIL_PATTERN)){
            popRegion();
            return false;
        }
        shrinkRegion();

        this.sysEventHandler.sysEventType(SysEventType.GUARD);
        this.sysEventHandler.sysEventGuard(getContent(),
                                           guardByRange,
                                           guardToRange );
        sweepSpace();

        return true;
    }

    private static final Pattern CONTENT_PATTERN =
            compile(
             "("
                +"[^<>\\n\\r]+"
            +")|("
                +"<br />"
            +")|(?:"
                +"<a\u0020href=\"([^\"]*)\">([^<>]*)</a>"
            +")"
            );

    /**
     * システムイベントの内容文字列をパースする。
     * @throws HtmlParseException パースエラー
     */
    private void parseContent() throws HtmlParseException{
        SeqRange anchorRange  = this.rangepool_1;
        SeqRange contentRange = this.rangepool_2;

        sweepSpace();

        for(;;){
            if( ! lookingAtProbe(CONTENT_PATTERN) ){
                break;
            }

            if(isGroupMatched(1)){
                contentRange.setLastMatchedGroupRange(getMatcher(), 1);
                this.sysEventHandler
                    .sysEventContent(getContent(), contentRange);
            }else if(isGroupMatched(2)){
                this.sysEventHandler.sysEventContentBreak();
            }else if(isGroupMatched(3)){
                anchorRange.setLastMatchedGroupRange(getMatcher(), 3);
                contentRange.setLastMatchedGroupRange(getMatcher(), 4);
                this.sysEventHandler
                    .sysEventContentAnchor(getContent(),
                                           anchorRange,
                                           contentRange );
            }

            shrinkRegion();
        }

        sweepSpace();

        return;
    }

    /**
     * 一時的に現在の検索領域を待避する。
     * 待避できるのは1回のみ。複数回スタックはできない。
     * @see #popRegion()
     */
    private void pushRegion(){
        this.pushedRegionStart = regionStart();
        this.pushedRegionEnd   = regionEnd();
        return;
    }

    /**
     * 一時的に待避した検索領域を復活させる。
     * @throws IllegalStateException まだ何も待避していない。
     * @see #pushRegion()
     */
    private void popRegion() throws IllegalStateException{
        if(this.pushedRegionStart < 0 || this.pushedRegionEnd < 0){
            throw new IllegalStateException();
        }

        if(    this.pushedRegionStart != regionStart()
            || this.pushedRegionEnd   != regionEnd()  ){
            getMatcher().region(this.pushedRegionStart, this.pushedRegionEnd);
        }

        this.pushedRegionStart = -1;
        this.pushedRegionEnd   = -1;

        return;
    }

}
