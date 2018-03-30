/*
 * entity converter
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.osdn.jindolf.parser;

import java.util.Arrays;
import java.util.Collections;
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
 * ※ 人狼BBS(F国以前)はShift_JIS(⊃JISX0201)で運営されているので、
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

    private static final RepInfo GT   = new RepInfo("&gt;",       ">");
    private static final RepInfo LT   = new RepInfo("&lt;",       "<");
    private static final RepInfo AMP  = new RepInfo("&amp;",      "&");
    private static final RepInfo QUAT = new RepInfo("&quot;",     DQ_STR);
    private static final RepInfo BS   = new RepInfo(BS_PATTERN,   YEN_STR);
    private static final RepInfo UCS4 = new RepInfo(UCS4_PATTERN, "?");


    private final MultiMatcher multiMatcher = new MultiMatcher();

    {
        this.multiMatcher.putRepInfo(GT, LT, AMP, QUAT, BS, UCS4);
    }

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
        this.replaceSmp = replaceSmp;
        return;
    }


    /**
     * XHTML文字実体参照の変換を行う。
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
     * XHTML文字実体参照の変換を行う。
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
     * XHTML文字実体参照の変換を行う。
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
     * XHTML文字実体参照の変換を行い既存のDecodedContentに追加を行う。
     *
     * @param dstContent 追加先文書。nullなら新たな文書が用意される。
     * @param srcContent 変換元文書
     * @return dstContentもしくは新規に用意された文書
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
     * XHTML文字実体参照の変換を行い既存のDecodedContentに追加を行う。
     *
     * @param dstContent 追加先文書。nullなら新たな文書が用意される。
     * @param srcContent 変換元文書
     * @param range 範囲指定
     * @return dstContentもしくは新規に用意された文書
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
     * XHTML文字実体参照の変換を行い既存のDecodedContentに追加を行う。
     *
     * @param dstContent 追加先文書。nullなら新たな文書が用意される。
     * @param srcContent 変換元文書
     * @param startPos 開始位置
     * @param endPos 終了位置
     * @return dstContentもしくは新規に用意された文書
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

        CharSequence rawContent = srcContent.getRawContent();
        this.multiMatcher.setText(rawContent, startPos, endPos);

        int copiedPos = startPos;

        for(;;){
            RepInfo repInfo = this.multiMatcher.multiFind();
            if(repInfo == null) break;
            if(repInfo == UCS4 &&  ! this.replaceSmp){
                continue;
            }

            int matchStart = this.multiMatcher.getMatchStart();
            int matchEnd   = this.multiMatcher.getMatchEnd();
            result.append(srcContent, copiedPos, matchStart);

            String altTxt = repInfo.getAltTxt();
            result.append(altTxt);

            copiedPos = matchEnd;
        }

        result.append(srcContent, copiedPos, endPos);

        return result;
    }


    /**
     * 同時に複数の正規表現をOR探索するマッチャ。
     */
    private static class MultiMatcher{

        private static final char REGEX_OR       = '|';
        private static final char REGEX_GRPOPEN  = '(';
        private static final char REGEX_GRPCLOSE = ')';


        private List<RepInfo> repInfoList;
        private Pattern orPattern;

        private Matcher matcher;

        private int matchStart = -1;
        private int matchEnd   = -1;


        /**
         * コンストラクタ。
         */
        MultiMatcher(){
            super();
            return;
        }


        /**
         * 置換情報を設定する。
         *
         * <p>先頭の置換情報の方が優先的にマッチングされる。
         *
         * @param infos 置換情報並び
         */
        void putRepInfo(RepInfo... infos){
            List<RepInfo> list;
            list = Arrays.asList(infos);
            list = Collections.unmodifiableList(list);
            this.repInfoList = list;

            StringBuilder orRegex = new StringBuilder();
            for(RepInfo repInfo : this.repInfoList){
                String regex = repInfo.getRegex();

                if(orRegex.length() != 0) orRegex.append(REGEX_OR);
                orRegex.append(REGEX_GRPOPEN);
                orRegex.append(regex);
                orRegex.append(REGEX_GRPCLOSE);
            }
            this.orPattern = Pattern.compile(orRegex.toString());

            this.matcher = this.orPattern.matcher("");
            this.matchStart = -1;
            this.matchEnd   = -1;

            return;
        }

        /**
         * 走査対象を設定する。
         *
         * @param seq 対象文字列
         * @param startPos 走査開始位置
         * @param endPos 走査終了位置
         * @throws IllegalStateException 置換情報が未設定
         */
        void setText(CharSequence seq, int startPos, int endPos){
            if(this.matcher == null) throw new IllegalStateException();

            this.matcher.reset(seq);
            this.matcher.region(startPos, endPos);

            this.matchStart = -1;
            this.matchEnd   = -1;

            return;
        }

        /**
         * マッチ開始位置を返す。
         *
         * @return 開始位置
         */
        int getMatchStart(){
            return this.matchStart;
        }

        /**
         * マッチ終了位置を返す。
         *
         * @return 終了位置
         */
        int getMatchEnd(){
            return this.matchEnd;
        }

        /**
         * 同時に複数の正規表現とのマッチングを試みるための走査を行う。
         *
         * <p>マッチングに伴いマッチ開始位置と終了位置が更新される。
         *
         * @return 最初にマッチングした正規表現。
         *     マッチングしなければnullを返す。
         * @throws IllegalStateException 置換情報が未設定
         */
        RepInfo multiFind(){
            if(this.repInfoList == null || this.matcher == null){
                throw new IllegalStateException();
            }

            if( ! this.matcher.find()) return null;

            RepInfo result = null;

            int group = 1;
            for(RepInfo rc : this.repInfoList){
                this.matchStart = this.matcher.start(group);
                this.matchEnd   = this.matcher.end(group);
                if(this.matchStart >= 0){
                    result = rc;
                    break;
                }
                group++;
            }

            return result;
        }

    }


    /**
     * 文字列置換設定。
     */
    private static class RepInfo{

        private final String regex;
        private final String altTxt;


        /**
         * コンストラクタ。
         *
         * <p>正規表現文字列に前方参照グループ記号()を含めてはならない。
         *
         * @param regex 置換元パターン正規表現
         * @param altTxt 置換文字列。
         */
        RepInfo(String regex, String altTxt){
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

    }

}
