/*
 * entity converter
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 人狼BBSで用いられる4種類のXHTML文字実体参照の
 * 解決を伴う{@link DecodedContent}の切り出しを行う。
 * 文字実体参照は{@code &gt; &lt; &quot; &amp;}が対象。
 * U+005C(バックスラッシュ)をU+00A5(円通貨)に直す処理も行われる。
 * ※ 人狼BBSはShift_JIS(⊃JISX0201)で運営されているので、
 * バックスラッシュは登場しないはず。
 * ※ が、バックスラッシュを生成するShift_JISデコーダは存在する。
 * マルチスレッドには非対応。
 */
public class EntityConverter{

    private static final String[][] XCHG_TABLE = {
        {"&gt;",   ">"},
        {"&lt;",   "<"},
        {"&quot;", "\""},
        {"&amp;",  "&"},
        {"\u005c\u005c", "\u00a5"},
    };

    private static final Pattern XCHG_PATTERN;

    static{
        StringBuilder regex = new StringBuilder();
        for(String[] xchg : XCHG_TABLE){
            String xchgFrom = xchg[0];
            if(regex.length() > 0) regex.append('|');
            regex.append('(')
                 .append(Pattern.quote(xchgFrom))
                 .append(')');
            assert xchgFrom.indexOf(DecodedContent.ALTCHAR) < 0;
        }
        XCHG_PATTERN = Pattern.compile(regex.toString());
    }

    private final Matcher matcher = XCHG_PATTERN.matcher("");

    /**
     * コンストラクタ。
     */
    public EntityConverter(){
        super();
        return;
    }

    /**
     * 実体参照の変換を行う。
     * @param content 変換元文書
     * @return 切り出された変換済み文書
     */
    public DecodedContent convert(DecodedContent content){
        return append(null, content, 0, content.length());
    }

    /**
     * 実体参照の変換を行う。
     * @param content 変換元文書
     * @param range 範囲指定
     * @return 切り出された変換済み文書
     * @throws IndexOutOfBoundsException 位置指定に不正があった
     */
    public DecodedContent convert(DecodedContent content, SeqRange range)
            throws IndexOutOfBoundsException{
        return append(null, content, range.getStartPos(), range.getEndPos());
    }

    /**
     * 実体参照の変換を行う。
     * @param content 変換元文書
     * @param startPos 開始位置
     * @param endPos 終了位置
     * @return 切り出された変換済み文書
     * @throws IndexOutOfBoundsException 位置指定に不正があった
     */
    public DecodedContent convert(DecodedContent content,
                                   int startPos, int endPos)
            throws IndexOutOfBoundsException{
        return append(null, content, startPos, endPos);
    }

    /**
     * 実体参照の変換を行い既存のDecodedContentに追加を行う。
     * @param target 追加先文書。nullなら新たな文書が用意される。
     * @param content 変換元文書
     * @return targetもしくは新規に用意された文書
     * @throws IndexOutOfBoundsException 位置指定に不正があった
     */
    public DecodedContent  append(DecodedContent target,
                                   DecodedContent content)
            throws IndexOutOfBoundsException{
        return append(target, content, 0, content.length());
    }

    /**
     * 実体参照の変換を行い既存のDecodedContentに追加を行う。
     * @param target 追加先文書。nullなら新たな文書が用意される。
     * @param content 変換元文書
     * @param range 範囲指定
     * @return targetもしくは新規に用意された文書
     * @throws IndexOutOfBoundsException 位置指定に不正があった
     */
    public DecodedContent  append(DecodedContent target,
                                   DecodedContent content,
                                   SeqRange range )
            throws IndexOutOfBoundsException{
        return append(target, content,
                      range.getStartPos(), range.getEndPos());
    }

    /**
     * 実体参照の変換を行い既存のDecodedContentに追加を行う。
     * @param target 追加先文書。nullなら新たな文書が用意される。
     * @param content 変換元文書
     * @param startPos 開始位置
     * @param endPos 終了位置
     * @return targetもしくは新規に用意された文書
     * @throws IndexOutOfBoundsException 位置指定に不正があった
     */
    public DecodedContent append(DecodedContent target,
                                  DecodedContent content,
                                  int startPos, int endPos)
            throws IndexOutOfBoundsException{
        if(   startPos > endPos
           || startPos < 0
           || content.length() < endPos){
            throw new IndexOutOfBoundsException();
        }

        DecodedContent result;
        if(target == null){
            result = new DecodedContent(endPos - startPos);
        }else{
            result = target;
        }

        this.matcher.reset(content.getRawContent());
        this.matcher.region(startPos, endPos);

        int lastPos = startPos;
        while(this.matcher.find()){
            int group;
            int matchStart = -1;
            for(group = 1; group <= XCHG_TABLE.length; group++){
                matchStart = this.matcher.start(group);
                if(matchStart >= 0) break;
            }
            int matchEnd = this.matcher.end(group);

            result.append(content, lastPos, matchStart);

            String toStr = XCHG_TABLE[group - 1][1];
            result.append(toStr);

            lastPos = matchEnd;
        }
        result.append(content, lastPos, endPos);

        this.matcher.reset("");

        return result;
    }

}
