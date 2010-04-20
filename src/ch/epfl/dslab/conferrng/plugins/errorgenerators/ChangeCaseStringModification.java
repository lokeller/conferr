/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.epfl.dslab.conferrng.plugins.errorgenerators;

import ch.epfl.dslab.conferrng.arugula.RandomNumberGenerator;
import ch.epfl.dslab.conferrng.arugula.StringModification;

/**
 *
 * @author slv
 */
public class ChangeCaseStringModification extends StringModification {

    @Override
    public String getModification(String text) {
        int id;
        if (!text.matches(".*[a-zA-Z].*")) {
            return null;
        }


        char[] s = text.toCharArray();

        do {
            id = (int) (s.length * RandomNumberGenerator.staticGetNextDouble()) % s.length;
        } while (id < 0 || !Character.isLetter(s[id]));

        if (Character.isLowerCase(s[id])) {
            s[id] = Character.toUpperCase(s[id]);
        } else {
            s[id] = Character.toLowerCase(s[id]);
        }

        return new String(s);//StringModification("Changed case of letter at position " + id, new String(s));
    }

    @Override
    public Object clone() {
        return new ChangeCaseStringModification();
    }
}
