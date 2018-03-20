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
import java.util.Collections;
import java.util.Enumeration;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import jp.sourceforge.jindolf.parser.*;

/**
 * サンプルのパーサ
 */
public class SampleParser{

    private static final CharsetDecoder CHD_UTF8;

    static{
        CHD_UTF8 = Charset.forName("UTF-8").newDecoder();
    }

    private SampleParser(){
        super();
        return;
    }

    public static SortedMap<String, ZipEntry> createEntryMap(ZipFile file){
        TreeMap<String, ZipEntry> result = new TreeMap<>();

        Enumeration<? extends ZipEntry> list = file.entries();
        while(list.hasMoreElements()){
            ZipEntry entry = list.nextElement();
            String name = entry.getName();
            result.put(name, entry);
        }

        return Collections.unmodifiableSortedMap(result);
    }

    public static DecodedContent contentFromStream(InputStream istream)
            throws IOException, DecodeBreakException{
        DecodeNotifier decoder = new DecodeNotifier(CHD_UTF8);
        ContentBuilder builder = new ContentBuilder();

        decoder.setCharDecodeListener(builder);

        decoder.decode(istream);

        DecodedContent content = builder.getContent();

        return content;
    }

    public static void parseContent(DecodedContent content)
            throws HtmlParseException{
        HtmlParser parser = new HtmlParser();
        HtmlHandler handler = new SampleHandler();

        parser.setBasicHandler   (handler);
        parser.setTalkHandler    (handler);
        parser.setSysEventHandler(handler);

        parser.parseAutomatic(content);

        return;
    }

    public static void parseStream(InputStream istream)
            throws IOException,
                   DecodeBreakException,
                   HtmlParseException{
        DecodedContent content = contentFromStream(istream);

        parseContent(content);

        return;
    }

    public static void main(String[] args)
            throws IOException,
                   DecodeBreakException,
                   HtmlParseException {
        if(args.length == 0){
            System.out.println(
                     "標準入力から人狼BBSのXHTML文書の読み取りを"
                    +"開始します...");

            parseStream(System.in);

            System.exit(0);

            return;
        }else if(args[0].endsWith(".zip")){
            System.out.println(
                     "ZIPアーカイブ内の*.htmlファイルから"
                    +"人狼BBSのXHTML文書の読み取りを開始します...");

            ZipFile zipfile = new ZipFile(args[0]);

            SortedMap<String, ZipEntry> map = createEntryMap(zipfile);

            for(ZipEntry entry : map.values()){
                String name = entry.getName();
                if( ! name.endsWith(".html") ) continue;

                System.out.println(name + "のパースを開始...");

                InputStream istream = zipfile.getInputStream(entry);
                parseStream(istream);

                istream.close();
            }

            zipfile.close();

            System.exit(0);

            return;
        }else{
            System.out.println(args[0] + "のパースを開始...");

            InputStream istream = new FileInputStream(args[0]);
            parseStream(istream);
            istream.close();

            System.exit(0);
        }

        return;
    }

}
