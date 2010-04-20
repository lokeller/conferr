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
import java.util.Vector;


public class ChangeModifier extends StringModification {

    @Override
    protected String getModification(String text) {

        char [] s = text.toCharArray();

        /* stores all IDs of chars that have a predecessor or a successor
         * typed with a different modifier
         */
        Vector<Integer> points = new Vector<Integer>();

        /* stores the modifier used to create the corresponding neighbor */
        Vector<Integer> cases = new Vector<Integer>();

        /* always propose change case of the first key we know the position and that is a letter*/
        Character ch=null;
        for(char c : s){
            if(Character.isLetter(c)){
                ch=c;
                break;
            }

        }
        if(ch==null)
            return text;
        KeyboardLayout.KeyPos k = KeyboardLayout.findCharacter(ch);
        int offset = 0;
        while( k.isNotFound() && offset < s.length - 1) {
                offset++;
                k = KeyboardLayout.findCharacter(s[offset]);
        }

        if (offset == s.length) {
                return text;
        }

        if (k.modifier != KeyboardLayout.MODIFIER_ALT) {

                if (KeyboardLayout.findCharacter(s[0]).modifier == 0) {
                        points.add(0);
                        cases.add(1);
                } else if (KeyboardLayout.findCharacter(s[0]).modifier == 1) {
                        points.add(0);
                        cases.add(0);
                }
        }

        /* look for changes of case */
        for (int i = offset + 1; i < s.length; i ++) {

                int curr = KeyboardLayout.findCharacter(s[i]).modifier;

                /* if the key for the current char was not found skip (probably a space)*/
                if (curr == -1) continue;

                int prev = KeyboardLayout.findCharacter(s[i-1]).modifier;
                /* if the key for the previous was not found skip (probably a space)*/
                if (prev != -1 ) {
                        if (prev != curr) {
                                points.add(i);
                                cases.add(prev);
                        }
                }

                /* last character has no successor */
                if (i < s.length - 1) {
                        int post = KeyboardLayout.findCharacter(s[i+1]).modifier;
                        /* if the key for the next was not found skip (probably a space)*/
                        if (post != -1) {
                                if (post != curr) {
                                        points.add(i);
                                        cases.add(post);
                                }
                        }
                }

        }

        if (points.size() == 0) return text;
        /* take a point where the fault could be injected at random */
        int id = (int) (points.size()  * RandomNumberGenerator.staticGetNextDouble());

        /* take the character generated with the same key but with a different
         * modifier
         */
        KeyboardLayout.KeyPos p = KeyboardLayout.findCharacter(s[points.get(id)]);

        s[points.get(id)] = KeyboardLayout.keyboardLayout[cases.get(id)][p.line].charAt(p.col);

	return new String(s);
    }

    @Override
    public Object clone() {
        return new ChangeModifier();
    }
    
    
    
}
