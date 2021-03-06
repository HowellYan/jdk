/*
 * Copyright (c) 2020 SAP SE. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

import java.io.IOException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import jdk.test.lib.util.FileUtils;

/*
 * @test
 * @bug 8256818 8257670
 * @summary Test that creating and closing SSL Sockets without bind/connect
 *          will not leave leaking socket file descriptors
 * @library /test/lib
 * @run main/manual SSLSocketLeak
 */
// Note: this test is not reliable, run it manually.
public class SSLSocketLeak {

    private static final int NUM_TEST_SOCK = 500;

    public static void main(String[] args) throws IOException {
        long fds_start = FileUtils.getProcessHandleCount();
        System.out.println("FDs at the beginning: " + fds_start);

        SocketFactory f = SSLSocketFactory.getDefault();
        for (int i = 0; i < NUM_TEST_SOCK; i++) {
            f.createSocket().close();
        }

        long fds_end = FileUtils.getProcessHandleCount();
        System.out.println("FDs in the end: " + fds_end);

        if ((fds_end - fds_start) > (NUM_TEST_SOCK / 10)) {
            throw new RuntimeException("Too many open file descriptors. Looks leaky.");
        }
    }
}
