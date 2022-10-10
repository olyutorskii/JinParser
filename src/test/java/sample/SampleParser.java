/*
 * sample parser
 *
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package sample;

import io.bitbucket.olyutorskii.jiocema.DecodeBreakException;
import io.bitbucket.olyutorskii.jiocema.DecodeNotifier;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import jp.osdn.jindolf.parser.HtmlHandler;
import jp.osdn.jindolf.parser.HtmlParseException;
import jp.osdn.jindolf.parser.HtmlParser;
import jp.osdn.jindolf.parser.content.ContentBuilder;
import jp.osdn.jindolf.parser.content.DecodedContent;

/**
 * サンプルのパーサ。
 *
 * <p>F国以前(Shift_JIS)用
 */
public class SampleParser{

    private static final Charset CS = Charset.forName("Shift_JIS");


    private SampleParser(){
        super();
        return;
    }


    private static DecodedContent contentFromStream(InputStream istream)
            throws IOException, DecodeBreakException{
        CharsetDecoder cd = CS.newDecoder();
        DecodeNotifier decoder = new DecodeNotifier(cd);
        ContentBuilder builder = new ContentBuilder();

        decoder.setCharDecodeListener(builder);

        decoder.decode(istream);

        DecodedContent content = builder.getContent();

        return content;
    }

    private static void parseContent(DecodedContent content)
            throws HtmlParseException{
        HtmlParser parser = new HtmlParser();
        HtmlHandler handler = new SampleHandler();

        parser.setBasicHandler   (handler);
        parser.setTalkHandler    (handler);
        parser.setSysEventHandler(handler);

        parser.parseAutomatic(content);

        return;
    }

    private static void parseStream(InputStream istream)
            throws IOException,
                   DecodeBreakException,
                   HtmlParseException{
        DecodedContent content = contentFromStream(istream);
        parseContent(content);
        return;
    }

    private static void modeStdin()
            throws IOException,DecodeBreakException,HtmlParseException{
        System.out.println(
                 "標準入力から人狼BBSのXHTML文書の読み取りを"
                +"開始します...");

        parseStream(System.in);

        return;
    }

    private static void modeZip(String zipFileName)
            throws IOException,DecodeBreakException,HtmlParseException{
        System.out.println(
                 "ZIPアーカイブ内の*.htmlファイルから"
                +"人狼BBSのXHTML文書の読み取りを開始します...");

        try(ZipFile zipFile = new ZipFile(zipFileName)){
            Enumeration<? extends ZipEntry> list = zipFile.entries();
            while(list.hasMoreElements()){
                ZipEntry entry = list.nextElement();
                String name = entry.getName();
                if( ! name.endsWith(".html") ) continue;

                System.out.println(name + "のパースを開始...");

                try(InputStream istream = zipFile.getInputStream(entry)){
                    parseStream(istream);
                }
            }
        }

        return;
    }

    private static void modeFile(String fileName)
            throws IOException,DecodeBreakException,HtmlParseException{
        System.out.println(fileName + "のパースを開始...");

        try(InputStream istream = new FileInputStream(fileName)){
            parseStream(istream);
        }

        return;
    }

    public static void main(String[] args)
            throws IOException,
                   DecodeBreakException,
                   HtmlParseException {
        if(args.length == 0){
            modeStdin();
            return;
        }

        String fileName;
        fileName = args[0];

        if(fileName.endsWith(".zip")){
            modeZip(fileName);
        }else{
            modeFile(fileName);
        }

        System.exit(0);

        return;
    }

}
