/*
 * License : The MIT License
 * Copyright(c) 2018 olyutorskii
 */

package jp.osdn.jindolf.parser.content;

import io.bitbucket.olyutorskii.jiocema.DecodeBreakException;

/**
 * Test listener for {@link CharDecodeListener} with Raw-bytes
 */
class TestListenerRW extends TestListener{

    @Override
    public void rawBytes(byte[] byteArray, int offset, int length)
            throws DecodeBreakException {
        append("[RW]");
        dumpHex(byteArray, offset, length);
        return;
    }

}
