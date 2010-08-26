/*
 * License : The MIT License
 * Copyright(c) 2010 olyutorskii
 */

package jp.sourceforge.jindolf.parser;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class ContentBuilderUCS2Test {

    public ContentBuilderUCS2Test() {
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
     * Test of charToUTF8 method, of class ContentBuilderUCS2.
     */
    @Test
    public void testCharToUTF16(){
        System.out.println("charToUTF16");

        char ch;
        byte[] result;
        
        ch = '\ud844';
        result = ContentBuilderUCS2.charToUTF16(ch);

        assertEquals(2, result.length);

        return;
    }

}
