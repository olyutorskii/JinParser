/*
 * talk-part parser
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

import java.util.regex.Pattern;
import jp.sourceforge.jindolf.corelib.TalkType;

/**
 * 人狼BBSシステムが出力する各発言箇所のパーサ。
 * パース進行に従い{@link TalkHandler}の各種メソッドが呼び出される。
 */
public class TalkParser extends AbstractParser{

    private TalkHandler talkHandler;

    private final SeqRange rangepool_1 = new SeqRange();

    /**
     * コンストラクタ。
     * @param parent 親パーサ
     */
    public TalkParser(ChainedParser parent){
        super(parent);
        return;
    }

    /**
     * {@link TalkHandler}ハンドラを登録する。
     * @param talkHandler ハンドラ
     */
    public void setTalkHandler(TalkHandler talkHandler){
        this.talkHandler = talkHandler;
        return;
    }

    /**
     * 各Avatarの個別の発言をパースする。
     * 最初のAタグは既にパース済みとする。
     * @param talkNo 白発言番号
     * @param nameRange Aタグのname属性値の範囲
     * @throws HtmlParseException パースエラー
     */
    public void parseTalk(int talkNo, SeqRange nameRange)
            throws HtmlParseException{
        this.talkHandler.startTalk();

        this.talkHandler.talkNo(talkNo);
        this.talkHandler.talkId(getContent(), nameRange);

        parseName();
        parseTime();
        parseIcon();
        parseType();
        parseText();
        parseTail();

        this.talkHandler.endTalk();

        return;
    }

    private static final Pattern AVATARNAME_PATTERN =
            compile("([^<]*)");

    /**
     * 発言者名をパースする。
     * @throws HtmlParseException パースエラー
     */
    private void parseName() throws HtmlParseException{
        setContextErrorMessage("lost dialog avatar-name");

        SeqRange avatarRange = this.rangepool_1;

        lookingAtAffirm(AVATARNAME_PATTERN);
        avatarRange.setLastMatchedGroupRange(getMatcher(), 1);
        shrinkRegion();

        this.talkHandler.talkAvatar(getContent(), avatarRange);

        return;
    }

    private static final Pattern TALKTIME_PATTERN =
            compile(
                 "</a>"
                +SP_I
                +"<span class=\"time\">"
                +"(?:(午前\u0020)|(午後\u0020))?"
                +"([0-9][0-9]?)(?:時\u0020|\\:)"
                +"([0-9][0-9]?)分?\u0020"
                +"</span>"
                +SP_I
                +"<table\u0020[^>]*>"
                +SP_I
                +"(?:<tbody>)?"
                +SP_I
                +"<tr>"
            );

    /**
     * 発言時刻をパースする。
     * @throws HtmlParseException パースエラー
     */
    private void parseTime() throws HtmlParseException{
        setContextErrorMessage("lost dialog time");

        lookingAtAffirm(TALKTIME_PATTERN);
        int hour = parseGroupedInt(3);
        int minute = parseGroupedInt(4);
        if(isGroupMatched(2)){  // 午後指定
            hour = (hour + 12) % 24;
        }
        shrinkRegion();
        sweepSpace();

        this.talkHandler.talkTime(hour, minute);

        return;
    }

    private static final Pattern IMGSRC_PATTERN =
            compile(
                  "<td\u0020[^>]*><img\u0020src=\"([^\"]*)\"></td>"
                 +SP_I
                 +"<td\u0020[^>]*><img\u0020[^>]*></td>"
            );

    /**
     * アイコンのURLをパースする。
     * @throws HtmlParseException パースエラー
     */
    private void parseIcon() throws HtmlParseException{
        setContextErrorMessage("lost icon url");

        SeqRange urlRange = this.rangepool_1;

        lookingAtAffirm(IMGSRC_PATTERN);
        urlRange.setLastMatchedGroupRange(getMatcher(), 1);
        shrinkRegion();
        sweepSpace();

        this.talkHandler.talkIconUrl(getContent(), urlRange);

        return;
    }

    private static final Pattern TALKDIC_PATTERN =
            compile(
                 "<td>" +SP_I+ "<div(?:\u0020[^>]*)?>"
                +SP_I
                +"<div class=\"mes_"
                +"(?:(say)|(think)|(whisper)|(groan))"
                +"_body1\">"
            );

    /**
     * 発言種別をパースする。
     * @throws HtmlParseException パースエラー
     */
    private void parseType() throws HtmlParseException{
        setContextErrorMessage("lost dialog type");

        lookingAtAffirm(TALKDIC_PATTERN);
        TalkType type;
        if(isGroupMatched(1)){
            type = TalkType.PUBLIC;
        }else if(isGroupMatched(2)){
            type = TalkType.PRIVATE;
        }else if(isGroupMatched(3)){
            type = TalkType.WOLFONLY;
        }else if(isGroupMatched(4)){
            type = TalkType.GRAVE;
        }else{
            assert false;
            throw buildParseException();
        }
        shrinkRegion();

        this.talkHandler.talkType(type);

        return;
    }

    private static final Pattern TEXT_PATTERN =
            compile("([^<>]+)|(<br />)|(<a href=\"[^\"]*\">)|(</a>)");

    /**
     * 発言テキストをパースする。
     * 前後のホワイトスペースは無視しない。
     * @throws HtmlParseException パースエラー
     */
    private void parseText() throws HtmlParseException{
        setContextErrorMessage("lost dialog text");

        SeqRange textRange = this.rangepool_1;

        while(lookingAtProbe(TEXT_PATTERN)){
            if(isGroupMatched(1)){
                textRange.setLastMatchedGroupRange(getMatcher(), 1);
                this.talkHandler.talkText(getContent(), textRange);
            }else if(isGroupMatched(2)){      // <br />
                this.talkHandler.talkBreak();
            }else if(isGroupMatched(3)){      // <a>
                // IGNORE
                assert true;
            }else if(isGroupMatched(4)){      // </a>
                // IGNORE
                assert true;
            }else{
                assert false;
                throw buildParseException();
            }
            shrinkRegion();
        }

        return;
    }

    private static final Pattern TAIL_PATTERN =
            compile(
                       "</div>"  // F1603 2d21:12 ペーター発言には注意
                +SP_I+ "</div>"
                +SP_I+ "</td>"
                +SP_I+ "</tr>"
                +SP_I+ "(?:</tbody>)?"
                +SP_I+ "</table>"
            );

    /**
     * 発言末尾をパースする。
     * @throws HtmlParseException パースエラー
     */
    private void parseTail() throws HtmlParseException{
        setContextErrorMessage("lost dialog termination");

        lookingAtAffirm(TAIL_PATTERN);
        shrinkRegion();
        sweepSpace();

        return;
    }

}
