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

package ch.epfl.dslab.conferrng.engine;

import org.jdom.CDATA;
import org.jdom.Element;

/**
 * 
 * Results of the execution of a functional test on the SUT
 * 
 */

public class BenchmarkResult {

    /**
     * An external error occured ( caused by a component outside conferr)
     */
    public final static int ERROR = 1;
    /**
     * The benchmark was successfully carried out
     */
    public final static int OK = 2;
    /**
     * Conferr couldn't start the benchark
     */
    public final static int INTERNAL_ERROR = 3;

    private String name;
    private String result;
    private String errors;
    private int errorType;

    /**
     *
     * Create a benchmark result
     *
     * @param name the name of the test
     * @param result the value of the test
     * @param errors description of errors that occoured
     * @param errorType type of error occoured
     */
    public BenchmarkResult(String name, String result, String errors, int errorType) {
        super();
        this.name = name;
        this.result = result;
        this.errors = errors;
        this.errorType = errorType;
    }

    /**
     * Returns the name of the benchmark
     *
     * @return the name of the benchmark
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a description of the problem that occured while performing the
     * benchmark if an error occured. Otherwise returns null
     *
     * @return a description of the error if one occured, null otherwise
     */
    public String getErrors() {
        return errors;
    }

    /**
     * Returns the results of the benchmark
     *
     * @return the results of the benchmark
     */
    public String getResult() {
        return result;
    }


    /**
     * Returns whether and error occured during the benchmark
     *
     * @return one of the error type constants specified in the class
     */
    public int getErrorType() {
        return errorType;
    }

    /**
     * 
     * Returns an XML serialization of the benchmark result
     * 
     * @return a benchmark element containing the description of this benchmark result
     */
    public Element toXML() {
        Element el = new Element("benchmark");

        el.setAttribute("name", getName());
        el.setAttribute("error-type", getErrorType() + "");

        Element resEl = new Element("results");
        resEl.addContent(new CDATA(result));

        el.addContent(resEl);

        Element erEl = new Element("errors");
        erEl.addContent(new CDATA(getErrors()));

        el.addContent(erEl);

        return el;

    }

}
