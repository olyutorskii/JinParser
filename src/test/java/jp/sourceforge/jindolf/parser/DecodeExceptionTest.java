/*
 * Copyright(c) 2009 olyutorskii
 * $Id: DecodeExceptionTest.java 894 2009-11-04 07:26:59Z olyutorskii $
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
public class DecodeExceptionTest {

    public DecodeExceptionTest() {
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
     * Test of getBytePos method, of class DecodeException.
     */
    @Test
    public void testGetBytePos(){
        System.out.println("getBytePos");

        DecodeException ex;

        ex = new DecodeException();
        assertTrue(0 > ex.getBytePos());

        ex = new DecodeException("abc");
        assertTrue(0 > ex.getBytePos());

        ex = new DecodeException(10, 11);
        assertEquals(10, ex.getBytePos());

        ex = new DecodeException("abc", 10, 11);
        assertEquals(10, ex.getBytePos());

        return;
    }

    /**
     * Test of getCharPos method, of class DecodeException.
     */
    @Test
    public void testGetCharPos(){
        System.out.println("getCharPos");

        DecodeException ex;

        ex = new DecodeException();
        assertTrue(0 > ex.getCharPos());

        ex = new DecodeException("abc");
        assertTrue(0 > ex.getCharPos());

        ex = new DecodeException(10, 11);
        assertEquals(11, ex.getCharPos());

        ex = new DecodeException("abc", 10, 11);
        assertEquals(11, ex.getCharPos());

        return;
    }

    /**
     * Test of getMessage method, of class DecodeException.
     */
    @Test
    public void testGetMessage(){
        System.out.println("getMessage");

        DecodeException ex;

        ex = new DecodeException();
        assertEquals("bytePos=-1 charPos=-1", ex.getMessage());

        ex = new DecodeException("abc");
        assertEquals("abc bytePos=-1 charPos=-1", ex.getMessage());

        ex = new DecodeException(10, 11);
        assertEquals("bytePos=10 charPos=11", ex.getMessage());

        ex = new DecodeException("abc", 10, 11);
        assertEquals("abc bytePos=10 charPos=11", ex.getMessage());

        return;
    }

}
