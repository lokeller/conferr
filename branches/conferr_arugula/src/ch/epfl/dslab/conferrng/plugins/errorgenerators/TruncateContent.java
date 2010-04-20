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
public class TruncateContent extends StringModification {

    @Override
    public Object clone() {
        return new TruncateContent();
    }

    @Override
    protected String getModification(String text) {
        return text.substring(0, (int) (RandomNumberGenerator.staticGetNextDouble() * text.length()));
    }
}
