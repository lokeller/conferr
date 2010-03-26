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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * A fault space is a class that specifies how a parameter of a fault injector
 * can be set. The parameter can assume a value from one of the specified sets.
 * Depending on set from wich the value of parameter is choosen another parameter
 * has to be set, how this parameter can be set is specified by another fault space.
 */

public class FaultSpace {
    
    private String name;   
    
    private HashMap<AbstractValueSet, FaultSpace> subspaces = new HashMap<AbstractValueSet, FaultSpace>();
    private Vector<AbstractValueSet> valueSets = new Vector<AbstractValueSet>();
    
    public FaultSpace(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public void addSubspace( AbstractValueSet value, FaultSpace param) {
        subspaces.put(value, param);
        valueSets.add(value);
    }

    public HashMap<AbstractValueSet, FaultSpace> getSubspaces() {
        return subspaces;
    }
    
    
    public Fault getFault(Vector<String> faultDesc) {
        
        HashMap<FaultSpace, Value> values = new  HashMap<FaultSpace, Value>();
        
        faultDesc.remove(0);
        
        if (string2Values(values, faultDesc)) return new Fault(values);
        else return null;
        
    }
    
    public Fault getNextFault(Fault fault) {
        
        Value v = fault.getValues().get(this);
        
        if (v == null ) return null;
        
        Map.Entry<AbstractValueSet, FaultSpace> entry = getAbstractValueSet(v);
        
        if (entry == null) return null;
        
        if ( entry.getValue() != null) {

            Fault nextInSubspace = entry.getValue().getNextFault(fault);

            if ( nextInSubspace != null) {
                nextInSubspace.getValues().put(this, v);
                return nextInSubspace;
            }
            
        } 
            
        Value nextValue = entry.getKey().nextOf(v);         
        FaultSpace nextSpace = entry.getValue();
        if (nextValue == null) {
            int id = valueSets.indexOf(entry.getKey()) + 1;
            while ( id < valueSets.size() ) {
                nextValue = valueSets.get(id).getFirstValue();
                nextSpace = subspaces.get(valueSets.get(id));
                if (nextValue != null) break;
            }
        } 

        if (nextValue == null) return null;

        Fault nextFault;
        
        if (nextSpace != null) {
            nextFault = nextSpace.getFirstFault();
        } else {
            nextFault = new Fault(new HashMap<FaultSpace, Value>());
        }
                 
        nextFault.getValues().put(this, nextValue);            
        return nextFault;

    }
    
    private Map.Entry<AbstractValueSet, FaultSpace> getAbstractValueSet( Value v ) {
        for (Map.Entry<AbstractValueSet, FaultSpace> entry : subspaces.entrySet()) { 
            
            if ( entry.getKey().contains(v) ) return entry;
            
        }
        
        return null;
    }
    
    public Vector<String> getFaultDescription(Fault fault) {
        
        Map.Entry<AbstractValueSet, FaultSpace> entry = getAbstractValueSet(fault.getValues().get(this));
        
        FaultSpace space = entry.getValue();
        AbstractValueSet set = entry.getKey();
        
        Vector<String> faultDescription;
        
        if (space == null) {
            faultDescription = new Vector<String>();
            faultDescription.add(fault.getValues().get(this).getIdentifier());            
        } else {
            faultDescription = space.getFaultDescription(fault);
            faultDescription.insertElementAt(fault.getValues().get(this).getIdentifier(), 0);            
        }
        
        if (valueSets.size() > 1 ) {
            faultDescription.insertElementAt("subtype" + valueSets.indexOf(set), 0);
        }
        
        return faultDescription;
        
    }
    
    private boolean string2Values (HashMap<FaultSpace, Value> values, Vector<String> stringValues) {
                
        for (Map.Entry<AbstractValueSet, FaultSpace> entry : subspaces.entrySet()) {
            Vector<String> newStringValues = new Vector<String>(stringValues);
            if (valueSets.size() > 1) newStringValues.remove(0);
            String identifier = newStringValues.remove(0);            
            Value o = entry.getKey().getValue(identifier);
            if (o != null) {
                values.put(this, o);
                if (entry.getValue() == null) {
                    return true;
                } else {
                    return entry.getValue().string2Values(values, newStringValues);                
                }
            } 
        }
        
        return false;
    }   
    
    public int numberOfFaults() {
        
        int sum = 0;
        for (Map.Entry<AbstractValueSet, FaultSpace> entry : subspaces.entrySet()) {
            if (entry.getValue() == null) 
                sum += entry.getKey().size();
            else 
                sum += entry.getKey().size() * entry.getValue().numberOfFaults();
        }
        
        return sum;
        
    }
    
    public Enumeration<Fault> faults() {
        return new FaultEnumeration();
    }
    
    private Fault getFirstFault() {
        
        if ( valueSets.size() == 0 ) 
            return null;
        
        int i = 0;
        while (i < valueSets.size()) {
            
            Value v = valueSets.get(i).getFirstValue();
                        
            if (v != null) {
                
                FaultSpace set = subspaces.get(valueSets.get(i));
                
                if (set == null) {
                    HashMap<FaultSpace, Value> map = new HashMap<FaultSpace, Value>();
                    map.put(this, v);
                    return new Fault(map);
                } else {
                    Fault f = subspaces.get(valueSets.get(i)).getFirstFault();
                    if ( f != null) {
                        f.getValues().put(this, v);
                        return f;
                    }
                }
            }
        }
        
        return null;
        

    }
    
    private class FaultEnumeration implements Enumeration<Fault> {

        private Fault currentFault;
        private boolean started = false;

        public FaultEnumeration() {                        
        }        
        
        public boolean hasMoreElements() {
            if ( !started) return getFirstFault() != null;
            return getNextFault(currentFault) != null;
        }

        public Fault nextElement() {
            
            
            if (started && currentFault == null) return null;
            if (! started ) {
                currentFault = getFirstFault();
                started = true;
            } else 
                currentFault = getNextFault(currentFault);
            return currentFault;
        }
        
    }
    
    public Vector<String> toMultipleStrings() {
        
        Vector<String> strings = new Vector<String>();
        
        int count = 0;
        for ( AbstractValueSet set : valueSets) {
                        
            if ( subspaces.get(set) != null) {
                
                for (String str : subspaces.get(set).toMultipleStrings()) {
                    strings.add((valueSets.size() > 1 ? "subtype" + count + " " : "") + name + " : " + set + " " + str);
                }
                
            } else {                
                strings.add((valueSets.size() > 1 ? "subtype" + count + " " : "") + name + " : " + set + " ; ");
            }
            
            count++;
        } 
        
        return strings;
    }

    @Override
    public String toString() {

        String ret = "";
        int count = 0;
        for ( String s : toMultipleStrings()) {
            ret += "subtype0" + " " + s + "\n";
            count ++;
        }
        
        return ret;
    }
    
    
    
    
}
