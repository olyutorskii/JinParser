/*
 * page type
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

/**
 * 人狼BBSサーバが生成するXHTMLページの種別。
 */
public enum PageType{

    /** トップページ。 */
    TOP_PAGE,
    /** 終了した村一覧。古国には存在しない。 */
    VILLAGELIST_PAGE,
    /** 各村の各日々。 */
    PERIOD_PAGE,
    ;

}
