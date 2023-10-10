/*
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.osdn.jindolf.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 */
public class HtmlParseExceptionTest {

    public HtmlParseExceptionTest() {
    }

    /**
     * Test of getCharPos method, of class HtmlParseException.
     */
    @Test
    public void testGetCharPos(){
        System.out.println("getCharPos");

        HtmlParseException ex;

        ex = new HtmlParseException();
        assertTrue(0 > ex.getCharPos());

        ex = new HtmlParseException("abc");
        assertTrue(0 > ex.getCharPos());

        ex = new HtmlParseException(99);
        assertEquals(99, ex.getCharPos());

        ex = new HtmlParseException("abc", 99);
        assertEquals(99, ex.getCharPos());

        return;
    }

    /**
     * Test of getMessage method, of class HtmlParseException.
     */
    @Test
    public void testGetMessage(){
        System.out.println("getMessage");

        HtmlParseException ex;

        ex = new HtmlParseException();
        assertEquals("charPos=-1", ex.getMessage());

        ex = new HtmlParseException("abc");
        assertEquals("abc charPos=-1", ex.getMessage());

        ex = new HtmlParseException(99);
        assertEquals("charPos=99", ex.getMessage());

        ex = new HtmlParseException("abc", 99);
        assertEquals("abc charPos=99", ex.getMessage());

        return;
    }

}
