/*
 * License : The MIT License
 * Copyright(c) 2009 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 */
public class HtmlParseExceptionTest {

    public HtmlParseExceptionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception{
    }

    @AfterClass
    public static void tearDownClass() throws Exception{
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
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
