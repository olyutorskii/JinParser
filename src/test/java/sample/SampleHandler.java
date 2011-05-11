/*
 * sample handler
 * 
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package sample;

import jp.sourceforge.jindolf.parser.DecodedContent;
import jp.sourceforge.jindolf.parser.EntityConverter;
import jp.sourceforge.jindolf.parser.HtmlAdapter;
import jp.sourceforge.jindolf.parser.HtmlParseException;
import jp.sourceforge.jindolf.parser.SeqRange;

/**
 * サンプルのハンドラ
 */
public class SampleHandler extends HtmlAdapter{

    private final EntityConverter converter = new EntityConverter();

    public SampleHandler(){
        super();
        return;
    }

    @Override
    public void talkText(DecodedContent content, SeqRange textRange)
            throws HtmlParseException{
        System.out.println(this.converter.convert(content, textRange));
        return;
    }

}
