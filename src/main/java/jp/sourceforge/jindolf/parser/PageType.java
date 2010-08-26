/*
 * page type
 *
 * Copyright(c) 2009 olyutorskii
 * $Id: PageType.java 894 2009-11-04 07:26:59Z olyutorskii $
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
