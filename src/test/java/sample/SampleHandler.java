/*
 * sample handler
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package sample;

import jp.osdn.jindolf.parser.EntityConverter;
import jp.osdn.jindolf.parser.HtmlAdapter;
import jp.osdn.jindolf.parser.HtmlParseException;
import jp.osdn.jindolf.parser.SeqRange;
import jp.osdn.jindolf.parser.content.DecodedContent;

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
