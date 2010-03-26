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

/**
 * Set that contains all integers in the specified range
 */

public class ElementOfInterval extends AbstractValueSet {

    private int min ;
    private int max ;

    public ElementOfInterval(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public Value getValue(String identifier) {
        int i = Integer.parseInt(identifier);
        if ( i <= max && i >= min) {
            return new Value(identifier, i);
        }
        return null;
    }

    @Override
    public boolean contains(Value value) {       
        return ((Integer) value.getObject()) <= max && ((Integer) value.getObject()) >= min;
    }

    @Override
    public Value getFirstValue() {
        return new Value(min + "", min);
    }

    @Override
    public Value nextOf(Value value) {
        int next = (Integer) value.getObject() + 1;
        Value nextValue = new Value(next + "", next);
        if (contains(nextValue)) {            
            return nextValue;
        } else {
            return null;
        }
    }

    @Override
    public long size() {
        return max - min + 1;
    }

    @Override
    public String toString() {
        return "[ " + min + " , " + max +" ]";
    }

    @Override
    public Value get(long id) {
        return new Value((min + id) + "", min + id);
    }
    
    
    
}
