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

import java.io.IOException;
import java.io.StringWriter;
import org.incava.util.diff.Diff;
import org.incava.util.diff.Difference;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * This class creates a diff of two documents, it can either create a diff of
 * the XML representation or it can create a diff of the serialized document
 * 
 */
public class ConfigurationDiff {

    
    public static String getNativeDiff ( Document d1, Document d2, ConfigurationFile c) {
        
        String ret;
        StringWriter w1 = new StringWriter();
        StringWriter w2 = new StringWriter();
        
        try {
            
            c.getHandlerInstance().serializeConfiguration(d2, w2, c); 

            c.getHandlerInstance().serializeConfiguration(d1, w1, c);
            
            ret = getDiff(w1.getBuffer().toString(), w2.getBuffer().toString());            
            
        } catch (ImpossibleConfigurationException ex) {
            ret = "One of the configurations is impossible (" + ex.toString() + " )";
        } finally {
            try {
                w1.close();
                w2.close();
            } catch (IOException ex) {
                ret = "Error generating diff (" + ex + ")";
            }
        }
        
        return ret;
    }
    
    public static String getXmlDiff( Document d1, Document d2) {
        
        StringWriter w1 = new StringWriter();
        StringWriter w2 = new StringWriter();
        
        String ret;
        
        try {            

            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

            outputter.output(d1, w1);
            outputter.output(d2, w2);

            ret = getDiff(w1.getBuffer().toString(), w2.getBuffer().toString());            
            
        } catch (IOException ex) {
            ret = "Error generating diff (" + ex +")";   
        } finally {
            try {
                w1.close();
                w2.close();
            } catch (IOException ex) {
                ret = "Error generating diff (" + ex + ")";
            }
        }
        
        return ret;
    }
        
    private static String getDiff (String string1, String string2) {
            
            String[] f1 = string1.split("\n");
            String[] f2 = string2.split("\n");

            StringBuffer output = new StringBuffer();

            Diff diffO = new Diff(f1, f2);

            java.util.List difs = diffO.diff();

            for (Object o : difs) {
                Difference diff = (Difference) o;

                int delStart = diff.getDeletedStart();
                int delEnd = diff.getDeletedEnd();
                int addStart = diff.getAddedStart();
                int addEnd = diff.getAddedEnd();
                String from = toString(delStart, delEnd);
                String to = toString(addStart, addEnd);
                String type = delEnd != Difference.NONE && addEnd != Difference.NONE ? "c" : (delEnd == Difference.NONE ? "a" : "d");

                output.append(from + type + to + "\n");

                if (delEnd != Difference.NONE) {
                    for (int lnum = Math.max(delStart - 3, 0); lnum <= delStart - 1; ++lnum) {
                        output.append(" " + " " + f1[lnum] + "\n");
                    }

                    printLines(delStart, delEnd, "<", f1, output);
                    if (addEnd != Difference.NONE) {
                        output.append("---\n");
                    }
                } else {
                    for (int lnum = Math.max(addStart - 3, 0); lnum <= addStart - 1; ++lnum) {
                        output.append(" " + " " + f2[lnum] + "\n");
                    }
                }
                if (addEnd != Difference.NONE) {
                    printLines(addStart, addEnd, ">", f2, output);

                    for (int lnum = addEnd + 1; lnum <= Math.min(addEnd + 3, f2.length - 1); ++lnum) {
                        output.append(" " + " " + f2[lnum] + "\n");
                    }
                } else {
                    for (int lnum = delEnd + 1; lnum <= Math.min(delEnd + 3, f1.length - 1); ++lnum) {
                        output.append(" " + " " + f1[lnum] + "\n");
                    }
                }
            }
            
            return output.toString();
            
    }
    
    
    protected static void printLines(int start, int end, String ind, String[] lines, StringBuffer output) {
                
        for (int lnum = start; lnum <= end; ++lnum) {
            output.append(ind + " " + lines[lnum] + "\n");
        }
        
    }
    
    protected static String toString(int start, int end) {
        // adjusted, because file lines are one-indexed, not zero.

        StringBuffer buf = new StringBuffer();

        // match the line numbering from diff(1):
        buf.append(end == Difference.NONE ? start : (1 + start));
        
        if (end != Difference.NONE && start != end) {
            buf.append(",").append(1 + end);
        }
        return buf.toString();
    }
    
    
    
}
