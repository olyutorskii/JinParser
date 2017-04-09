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
 *
 * <p>文字実体参照は{@code &gt; &lt; &quot; &amp;}が対象。
 *
 * <p>U+005C(バックスラッシュ)をU+00A5(円通貨)に直す処理も行われる。
 * ※ 人狼BBSはShift_JIS(⊃JISX0201)で運営されているので、
 * バックスラッシュは登場しないはず。
 * ※ が、バックスラッシュを生成するShift_JISデコーダは存在する。
 *
 * <p>指示によりサロゲートペア上位下位の並びを
 * 単一疑問符?に直す処理も可能。
 * {@link java.lang.Character#MIN_SUPPLEMENTARY_CODE_POINT}
 * {@link java.lang.Character#MAX_CODE_POINT}
 *
 * <p>マルチスレッドには非対応。
 */
public class EntityConverter{

    private static final char   DQ_CH = '"';
    private static final String DQ_STR = Character.toString(DQ_CH);
    private static final String YEN_STR = "\u00a5";

    private static final char   BS_CH = '\u005c\u005c';
    private static final String BS_STR = Character.toString(BS_CH);
    private static final String BS_PATTERN = BS_STR + BS_STR;

    private static final String UCS4_PATTERN = "[\\x{10000}-\\x{10ffff}]";

    private static final RegexRep[] VALUES_CACHE = RegexRep.values();


    private final Matcher matcher = RegexRep.buildMatcher();
    private final boolean replaceSmp;


    /**
     * コンストラクタ。
     * SMP面文字の代替処理は行われない。
     */
    public EntityConverter(){
        this(false);
        return;
    }

    /**
     * コンストラクタ。
     * @param replaceSmp SMP面文字を代替処理するならtrue
     */
    public EntityConverter(boolean replaceSmp){
        super();
        this.replaceSmp = replaceSmp;
        return;
    }


    /**
     * 実体参照の変換を行う。
     * @param content 変換元文書
     * @return 切り出された変換済み文書
     */
    public DecodedContent convert(DecodedContent content){
        int startPos = 0;
        int endPos   = content.length();
        return append(null, content, startPos, endPos);
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
        int startPos = range.getStartPos();
        int endPos   = range.getEndPos();
        return append(null, content, startPos, endPos);
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
        int startPos = 0;
        int endPos   = content.length();
        return append(target, content, startPos, endPos);
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
        int startPos = range.getStartPos();
        int endPos   = range.getEndPos();
        return append(target, content, startPos, endPos);
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
        if(    startPos > endPos
            || startPos < 0
            || content.length() < endPos){
            throw new IndexOutOfBoundsException();
        }

        DecodedContent result;
        if(target == null){
            int length = endPos - startPos;
            result = new DecodedContent(length);
        }else{
            result = target;
        }

        this.matcher.reset(content.getRawContent());
        this.matcher.region(startPos, endPos);

        int copiedPos = startPos;
        while(this.matcher.find()){
            int group = -1;
            int matchStart = -1;
            String altTxt = "";
            for(RegexRep rr : VALUES_CACHE){
                group = rr.getGroupNo();
                matchStart = this.matcher.start(group);
                if(matchStart >= 0){
                    if(rr == RegexRep.UCS4 &&  ! this.replaceSmp){
                        altTxt = this.matcher.group(group);
                    }else{
                        altTxt = rr.getAltTxt();
                    }
                    break;
                }
            }
            assert group >= 1;
            int matchEnd = this.matcher.end(group);

            result.append(content, copiedPos, matchStart);
            result.append(altTxt);

            copiedPos = matchEnd;
        }
        result.append(content, copiedPos, endPos);

        this.matcher.reset("");

        return result;
    }


    /**
     * 文字列置換リスト。
     */
    private static enum RegexRep{

        GT   ("&gt;",       ">"),
        LT   ("&lt;",       "<"),
        AMP  ("&amp;",      "&"),
        QUAT ("&quot;",     DQ_STR),
        BS   (BS_PATTERN,   YEN_STR),
        UCS4 (UCS4_PATTERN, "?"),
        ;


        private final String regex;
        private final String altTxt;


        /**
         * コンストラクタ。
         * @param regex 置換元パターン正規表現
         * @param altTxt 置換文字列。
         */
        RegexRep(String regex, String altTxt){
            this.regex = regex;
            this.altTxt = altTxt;
            return;
        }


        /**
         * 全正規表現をOR連結したパターンを生成する。
         * @return パターン
         */
        private static Pattern buildPattern(){
            StringBuilder orRegex = new StringBuilder();

            for(RegexRep rr : values()){
                if(orRegex.length() > 0) orRegex.append('|');
                orRegex.append('(');
                orRegex.append(rr.regex);
                orRegex.append(')');
            }

            Pattern result = Pattern.compile(orRegex.toString());
            return result;
        }

        /**
         * マッチャを生成する。
         * @return マッチャ
         */
        private static Matcher buildMatcher(){
            Pattern pattern = buildPattern();
            Matcher result = pattern.matcher("");
            return result;
        }


        /**
         * 置換文字列を返す。
         * @return 置換文字列
         */
        private String getAltTxt(){
            return this.altTxt;
        }

        /**
         * パターン内において占めるグループ番号を返す。
         * @return グループ番号
         */
        private int getGroupNo(){
            int group = ordinal() + 1;
            return group;
        }

    }

}
