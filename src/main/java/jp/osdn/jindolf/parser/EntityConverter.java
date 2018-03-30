/*
 * entity converter
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.osdn.jindolf.parser;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jp.osdn.jindolf.parser.content.DecodedContent;

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

    private static final RegexCnv GT   = new RegexCnv("&gt;",       ">");
    private static final RegexCnv LT   = new RegexCnv("&lt;",       "<");
    private static final RegexCnv AMP  = new RegexCnv("&amp;",      "&");
    private static final RegexCnv QUAT = new RegexCnv("&quot;",     DQ_STR);
    private static final RegexCnv BS   = new RegexCnv(BS_PATTERN,   YEN_STR);
    private static final RegexCnv UCS4 = new RegexCnv(UCS4_PATTERN, "?");

    private static final List<RegexCnv> REGS =
            Arrays.asList(GT, LT, AMP, QUAT, BS, UCS4);

    private static final Pattern ORPAT;

    static{
        int groupNo = 1;
        StringBuilder orRegex = new StringBuilder();
        for(RegexCnv cnv : REGS){
            if(groupNo > 1) orRegex.append('|');
            orRegex.append('(');
            orRegex.append(cnv.getRegex());
            orRegex.append(')');
            cnv.setGroupNo(groupNo++);
        }
        ORPAT = Pattern.compile(orRegex.toString());
    }


    private final Matcher matcher;
    private final boolean replaceSmp;


    /**
     * コンストラクタ。
     *
     * <p>SMP面文字の代替処理は行われない。
     */
    public EntityConverter(){
        this(false);
        return;
    }

    /**
     * コンストラクタ。
     *
     * @param replaceSmp SMP面文字を代替処理するならtrue
     */
    public EntityConverter(boolean replaceSmp){
        super();
        this.matcher = buildMatcher();
        this.replaceSmp = replaceSmp;
        return;
    }


    /**
     * マッチャを生成する。
     *
     * @return マッチャ
     */
    private static Matcher buildMatcher(){
        Matcher result = ORPAT.matcher("");
        return result;
    }


    /**
     * 実体参照の変換を行う。
     *
     * @param srcContent 変換元文書
     * @return 切り出された変換済み文書
     */
    public DecodedContent convert(DecodedContent srcContent){
        int startPos = 0;
        int endPos   = srcContent.length();
        return append(null, srcContent, startPos, endPos);
    }

    /**
     * 実体参照の変換を行う。
     *
     * @param srcContent 変換元文書
     * @param range 範囲指定
     * @return 切り出された変換済み文書
     * @throws IndexOutOfBoundsException 位置指定に不正があった
     */
    public DecodedContent convert(DecodedContent srcContent, SeqRange range)
            throws IndexOutOfBoundsException{
        int startPos = range.getStartPos();
        int endPos   = range.getEndPos();
        return append(null, srcContent, startPos, endPos);
    }

    /**
     * 実体参照の変換を行う。
     *
     * @param srcContent 変換元文書
     * @param startPos 開始位置
     * @param endPos 終了位置
     * @return 切り出された変換済み文書
     * @throws IndexOutOfBoundsException 位置指定に不正があった
     */
    public DecodedContent convert(DecodedContent srcContent,
                                  int startPos, int endPos)
            throws IndexOutOfBoundsException{
        return append(null, srcContent, startPos, endPos);
    }

    /**
     * 実体参照の変換を行い既存のDecodedContentに追加を行う。
     *
     * @param dstContent 追加先文書。nullなら新たな文書が用意される。
     * @param srcContent 変換元文書
     * @return targetもしくは新規に用意された文書
     * @throws IndexOutOfBoundsException 位置指定に不正があった
     */
    public DecodedContent append(DecodedContent dstContent,
                                 DecodedContent srcContent)
            throws IndexOutOfBoundsException{
        int startPos = 0;
        int endPos   = srcContent.length();
        return append(dstContent, srcContent, startPos, endPos);
    }

    /**
     * 実体参照の変換を行い既存のDecodedContentに追加を行う。
     *
     * @param dstContent 追加先文書。nullなら新たな文書が用意される。
     * @param srcContent 変換元文書
     * @param range 範囲指定
     * @return targetもしくは新規に用意された文書
     * @throws IndexOutOfBoundsException 位置指定に不正があった
     */
    public DecodedContent append(DecodedContent dstContent,
                                 DecodedContent srcContent,
                                 SeqRange range )
            throws IndexOutOfBoundsException{
        int startPos = range.getStartPos();
        int endPos   = range.getEndPos();
        return append(dstContent, srcContent, startPos, endPos);
    }

    /**
     * 実体参照の変換を行い既存のDecodedContentに追加を行う。
     *
     * @param dstContent 追加先文書。nullなら新たな文書が用意される。
     * @param srcContent 変換元文書
     * @param startPos 開始位置
     * @param endPos 終了位置
     * @return targetもしくは新規に用意された文書
     * @throws IndexOutOfBoundsException 位置指定に不正があった
     */
    public DecodedContent append(DecodedContent dstContent,
                                 DecodedContent srcContent,
                                 int startPos, int endPos)
            throws IndexOutOfBoundsException{
        if(    startPos > endPos
            || startPos < 0
            || srcContent.length() < endPos){
            throw new IndexOutOfBoundsException();
        }

        DecodedContent result;
        if(dstContent == null){
            int length = endPos - startPos;
            result = new DecodedContent(length);
        }else{
            result = dstContent;
        }

        this.matcher.reset(srcContent.getRawContent());
        this.matcher.region(startPos, endPos);

        int copiedPos = startPos;
        while(this.matcher.find()){
            int group = -1;
            int matchStart = -1;
            String altTxt = "";
            for(RegexCnv rc : REGS){
                group = rc.getGroupNo();
                matchStart = this.matcher.start(group);
                if(matchStart >= 0){
                    if(rc == UCS4 &&  ! this.replaceSmp){
                        altTxt = this.matcher.group(group);
                    }else{
                        altTxt = rc.getAltTxt();
                    }
                    break;
                }
            }
            assert group >= 1;
            int matchEnd = this.matcher.end(group);

            result.append(srcContent, copiedPos, matchStart);
            result.append(altTxt);

            copiedPos = matchEnd;
        }
        result.append(srcContent, copiedPos, endPos);

        this.matcher.reset("");

        return result;
    }


    /**
     * 文字列置換リスト。
     */
    private static class RegexCnv{

        private final String regex;
        private final String altTxt;
        private int groupNo;


        /**
         * コンストラクタ。
         *
         * @param regex 置換元パターン正規表現
         * @param altTxt 置換文字列。
         */
        RegexCnv(String regex, String altTxt){
            this.regex = regex;
            this.altTxt = altTxt;
            return;
        }


        /**
         * 正規表現文字列を返す。
         *
         * @return 正規表現文字列
         */
        String getRegex(){
            return this.regex;
        }

        /**
         * 置換文字列を返す。
         *
         * @return 置換文字列
         */
        String getAltTxt(){
            return this.altTxt;
        }

        /**
         * パターン内において占めるグループ番号を返す。
         *
         * @return グループ番号
         */
        int getGroupNo(){
            return this.groupNo;
        }

        void setGroupNo(int groupNo){
            this.groupNo = groupNo;
            return;
        }

    }

}
