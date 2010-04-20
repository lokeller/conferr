/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.epfl.dslab.conferrng.arugula;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author slv
 */
public class ArugulaRuntime {

    public static Collection execute(List<Operator> _listOfOperators, Collection input) {
        List<Operator> listOfOperators = new Vector<Operator>(_listOfOperators);
        while (!listOfOperators.isEmpty()) {
            Operator op = listOfOperators.get(0);
            System.err.println("=====EXECUTING:  " + op);
            listOfOperators.remove(0);
            input = op.apply(input);
        }
        return input;
    }
}
