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

package conferr.faultdesc;

import java.util.HashMap;
import java.util.Vector;

/**
 * Default implementation of an AbstractValueSet, it defines a set of values and
 * an order to enumerate them.
 */

public class ValueSet extends AbstractValueSet {
    
    private Vector<Value> set = new Vector<Value>();
    private HashMap<String, Value> values = new HashMap<String, Value>();

    public ValueSet() {
    }

    public ValueSet(Value value) {
        addValue(value);
    }    
        
    public void addValue(Value value) {
        set.add(value);
        values.put(value.getIdentifier(), value);
    }

    @Override
    public Value getValue(String identifier) {
        return values.get(identifier);
    }

    @Override
    public boolean contains(Value value) {
        return set.contains(value);
    }

    @Override
    public Value getFirstValue() {
        if (set.size() == 0 ) return null;
        return set.get(0);
    }

    @Override
    public Value nextOf(Value value) {
        int indexNext = set.indexOf(value) + 1; 
        if ( indexNext < set.size() )
            return set.get(indexNext);
        else
            return null;
    }

    @Override
    public long size() {
        return set.size();
    }
    
    @Override
    public String toString() {
        String ret = "{ ";
        for ( int i = 0; i < set.size() ; i ++ ) {
            if ( i > 0 ) ret += " , ";
            ret += set.get(i);
        }
        return ret + " }";
    }

    @Override
    public Value get(long id) {
        return set.get((int) id);
    }
    
}
