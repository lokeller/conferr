/*

Copyright (c) 2008, Dependable Systems Lab, EPFL
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation 
and/or other materials provided with the distribution.
 * Neither the name of the Dependable Systems Lab, EPFL nor the names of its
contributors may be used to endorse or promote products derived from this 
software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR 
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */
package ch.epfl.dslab.conferrng.plugins.errorgenerators;

import ch.epfl.dslab.conferrng.arugula.RandomNumberGenerator;
import ch.epfl.dslab.conferrng.arugula.StringModification;
import java.awt.datatransfer.StringSelection;

public class SubstituteLetter extends StringModification {

    public static boolean containsLetters(String text){
        return text.matches(".*[a-zA-Z]+.*");
    }
    @Override
    protected String getModification(String text) {

        System.err.println("_______________________" + text);
        char[] s = text.toCharArray();

        int id = -1;
        boolean search = containsLetters(text);
        System.err.println(search);
        while (search) {
            id = (int) Math.abs(s.length * RandomNumberGenerator.staticGetNextDouble());
            if (Character.isLetter(s[id])) {
                search = false;
            }
        }
        if (id == -1) {
             System.err.println("NOT FOUND_______________________" + new String(s));
            return text;
        }

        String poss = KeyboardLayout.getNeighbors(s[id]);

        if (poss == null) {
             System.err.println("NO Neighbours_______________________" + new String(s));
            return text;
        }

        s[id] = poss.charAt((int) (RandomNumberGenerator.staticGetNextDouble() * poss.length()));
        System.err.println("_______________________" + new String(s));
        return new String(s);
    }

    @Override
    public Object clone() {
        return new SubstituteLetter();
    }

    public static void main(String[] args){
       // System.err.println("\"Demo\"".matches());
    }
}
