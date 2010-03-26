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

package conferr;

import java.util.Vector;
import org.jdom.Element;

/**
 * This class represent a particular instantiation of a fault template.
 * 
 */
public class FaultScenarioSet extends DefaultPluginContainer {

    private String name = "New fault scenario set";
    private FaultInjectionPlan plan;
    private FaultScenarioSet parent;

    private Vector<FaultScenarioSet> children = new Vector<FaultScenarioSet>();

    public FaultScenarioSet(FaultInjectionPlan plan, FaultScenarioSet parent) {
        this.plan = plan;
        this.parent = parent;
    }   
    
    
    public String getId() {
        if (parent != null )
            return parent.getId() + "_" + parent.getChildren().indexOf(this);
        else return "0";
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        String old = this.name;
        this.name = name;
        pcs.firePropertyChange("name", old, this.name);
    }    

    public void setPluginClass(String action) {        
        
        super.setPluginClass(action);
        
        FaultTemplate ca = (FaultTemplate) getPluginInstance();
        
        if (ca == null) return;
        
        Vector<FaultScenarioSet> oldC = this.children;
        this.children = new Vector<FaultScenarioSet>();
        
        if (ca.getMaxChildren() != -1) {
            for (int i = 0; i < ca.getMaxChildren() ; i++) {
                if (oldC.size() > i) 
                    children.add(oldC.get(i));
                else
                    children.add(new FaultScenarioSet(plan, this));
            }
        } else {
            children.addAll(oldC);
        }
                
        pcs.firePropertyChange("children", oldC, children);
        
    }
        
    public FaultTemplate getFaultTemplateInstance() {
        
        return (FaultTemplate) getPluginInstance();

    }
    
    
    @Override
    public String toString() {
        return "Scenario: " + name;
    }

    public Vector<FaultScenarioSet> getChildren() {
        return children;
    }

    public void setChildren(Vector<FaultScenarioSet> children) {        
        
        Vector<FaultScenarioSet> old = this.children;
                
        this.children = children;
        pcs.firePropertyChange("children", old, children);
    }
    
    
    public FaultScenarioSet copy() {
        FaultScenarioSet s = new FaultScenarioSet(plan, parent);
        s.setName(getName());
        s.setParams(getParams());        
        
        Vector<FaultScenarioSet> children = new Vector<FaultScenarioSet>();
        
        for (FaultScenarioSet s2 : getChildren()) {
            children.add(s2.copy());                    
        }
        
        s.setChildren(children);
        s.setPluginClass(getPluginClass());
        
        return s;
        
    }
    
    
    public Element toElement() {
        
        Element scenarioElement = new Element("template");

        scenarioElement.setAttribute("name", getName());        
        scenarioElement.setAttribute("template-class-name", (getPluginClass() == null ? "" : getPluginClass()));

        for (Parameter p : this.getParams()) {
            Element pElement = new Element("param");
            pElement.setAttribute("name", plan.stringToXMLString(p.getName()));
            pElement.setAttribute("value", plan.stringToXMLString(p.getValue()));
            scenarioElement.addContent(pElement);
        }
        
        Element childrenEl = new Element("children");
        
        for (FaultScenarioSet s2: getChildren()) {
            childrenEl.addContent(s2.toElement());
        }
        
        scenarioElement.addContent(childrenEl);
        
        return scenarioElement;
    }
    
    public static FaultScenarioSet fromElement(FaultInjectionPlan plan, Element scenarioElement, FaultScenarioSet parent) {
        FaultScenarioSet scenario = new FaultScenarioSet(plan, parent);

        scenario.setName(scenarioElement.getAttributeValue("name"));
        
        scenario.setPluginClass(scenarioElement.getAttributeValue("template-class-name"));    

        for (Object o2 : scenarioElement.getChildren("param")) {

            Element e = (Element) o2;
            scenario.setParameterValue(e.getAttributeValue("name"), e.getAttributeValue("value"));
            

        }
        
        Vector<FaultScenarioSet> scenarios = new Vector<FaultScenarioSet>();
        if (scenarioElement.getChild("children") != null) {
            for(Object o2 : scenarioElement.getChild("children").getChildren("template")) {
                scenarios.add(fromElement(plan, (Element) o2, scenario));
            }
        }                  
        
        scenario.setChildren(scenarios);
      
        return scenario;
    }

    @Override
    public String getPluginInterface() {
        return "conferr.FaultTemplate";
    }

    @Override
    public ClassFinderBean getClassFinderBean() {
        return new ClassFinderBean(plan);
    }

    public FaultInjectionPlan getPlan() {
        return plan;
    }
    
    public FaultScenarioSet substituteVariable(String name, String value) {
        
        FaultScenarioSet set = (FaultScenarioSet) this.clone();
    
        set.substituteVariableInt(name, value);
        
        return set;
    }
    
    private void substituteVariableInt(String name, String value) {
        
        for (Parameter p : getParams()) {
            p.setValue(p.getValue().replaceAll("\\$\\{" + name + "\\}", value));
        }
        
        for (FaultScenarioSet child : getChildren()) {
            child.substituteVariableInt(name, value);
        }
        
    }
    
    @Override
    public Object clone() {
    
        FaultScenarioSet newSet = new FaultScenarioSet(plan, null);
        
        newSet.setName(name);
        
        newSet.setPluginClass(this.getPluginClass());       
        
        Vector<Parameter> params = new Vector<Parameter>();
        
        for (Parameter p : getParams()) {
            params.add(new Parameter(p.getName(), p.getValue(), p.getType()));
        }
        
        newSet.setParams(params);
        
        newSet.getChildren().setSize(getChildren().size());
        
        for (int i = 0 ; i < children.size() ; i++) {
            
            FaultScenarioSet cset = (FaultScenarioSet) children.get(i).clone();
            
            cset.parent = newSet;
                        
            newSet.children.set(i, cset);
            
        }
        
        return newSet;
        
    }
    
    
    
    
}
