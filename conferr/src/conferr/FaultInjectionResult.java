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

import java.io.FileNotFoundException;
import java.util.Map;
import javax.xml.transform.TransformerException;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;

/**
 * This class stores the results of the injection of a given fault scenario
 * ( what kind of fault was injected, the result of the serialization and the
 * results of the system execution).
 * 
 */

public class FaultInjectionResult {

    private String description;
    private ErrorGenerator scenarioSet;
    private String outputDir;
    private Map<ConfigurationFile, Document> configDocuments;
    private RunnerResult result;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RunnerResult getResult() {
        return result;
    }

    public void setResult(RunnerResult result) {
        this.result = result;
    }

    public FaultInjectionResult(String desc, RunnerResult res, ErrorGenerator scenarioSet, String outputDir, Map<ConfigurationFile, Document> configDocuments) {
        super();
        this.description = desc;
        this.result = res;
        this.scenarioSet = scenarioSet;
        this.outputDir = outputDir;
        this.configDocuments = configDocuments;
    }

    public ErrorGenerator getScenarioSet() {
        return scenarioSet;
    }

    public void setScenarioSet(ErrorGenerator scenarioSet) {
        this.scenarioSet = scenarioSet;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public Map<ConfigurationFile, Document> getConfigDocuments() {
        return configDocuments;
    }

    public void setConfigDocuments(Map<ConfigurationFile, Document> configDocuments) {
        this.configDocuments = configDocuments;
    }
    
    public Element toElement() {

        Element resultEl = new Element("result");

        Element descriptionEl = new Element("description");
        descriptionEl.setText(getDescription());
        resultEl.addContent(descriptionEl);

        Element scenario = new Element("scenario");
        scenario.setText(getScenarioSet().getName());
        resultEl.addContent(scenario);

        Element element = new Element("outputDir");
        element.setText(getOutputDir());
        resultEl.addContent(element);

        element = new Element("changes");
        try {
            for (Map.Entry<ConfigurationFile, Document> entry : getConfigDocuments().entrySet()) {
                Element fileEl = new Element("file");
                fileEl.setAttribute("name", entry.getKey().getName());
                Document doc = entry.getKey().getDocument();
                CDATA data = new CDATA(ConfigurationDiff.getNativeDiff(doc, entry.getValue(), entry.getKey()));
                fileEl.addContent(data);
                element.addContent(fileEl);
            }
        } catch (TransformerException ex) {
            element.setText("Unable to transform config file:" + ex.toString());
        } catch (FileNotFoundException ex) {
            element.setText("Unable to find original config file: " + ex.toString());
        }
        resultEl.addContent(element);

        element = new Element("ret-value");
        element.setText(getResult().getRetVal() + "");
        resultEl.addContent(element);

        element = new Element("log");
        CDATA data = new CDATA(getResult().getStartupLog() + "\n" + getResult().getShutdownLog());
        element.addContent(data);
        resultEl.addContent(element);


        for (TestResult t : getResult().getTestResults()) {

            Element test = new Element("test");

            test.setAttribute("ret-value", t.getReturnValue() + "");

            Element name = new Element("name");

            name.setText(t.getName());

            Element out = new Element("result");

            data = new CDATA(t.getResult());
            out.addContent(data);

            Element err = new Element("errors");

            data = new CDATA(t.getErrors());
            err.addContent(data);

            test.addContent(name);
            test.addContent(out);
            test.addContent(err);

            resultEl.addContent(test);
        }

        return resultEl;
    }

    
}
