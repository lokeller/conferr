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

package conferr.handlers;

import conferr.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Vector;
import org.jdom.Document;
import org.jdom.Element;

public class ZoneFileHandler implements Handler {
    

    public static final String expandOriginString = "expand $ORIGIN";
    public static final String normalizeNamesString = "normalize names";
    
    public Vector<Parameter> getDefaultParameters() {
        
        Vector<Parameter> parameters = new Vector<Parameter>();
        
        parameters.add(new Parameter(expandOriginString, "", Parameter.BOOLEAN));
        parameters.add(new Parameter(normalizeNamesString, "", Parameter.BOOLEAN));
            
        return parameters;
        
    }
    
    
    public boolean getExpandOrigin(ConfigurationFile file) {
        try {
            return Boolean.parseBoolean(file.getParameterValue(expandOriginString));
        } catch ( IllegalArgumentException ex ) {
            return false;
        }
    }

    public boolean getNormalizeNames(ConfigurationFile file) {
        try {
            return Boolean.parseBoolean(file.getParameterValue(normalizeNamesString));
        } catch ( IllegalArgumentException ex ) {
            return false;
        }
    }
    
    
    @Override
    public Document parseConfiguration(Reader fr, ConfigurationFile file) {

        try {

            BufferedReader br = new BufferedReader(fr);

            Element root = new Element("root");


            String line;
            
            String lastOwner = "";
            String origin = "";
            
            while ((line = br.readLine()) != null) {

                line = line.replaceAll(";.*", "");

                if (line.matches("^\\s*$")) continue;                                        
                
                if (line.startsWith("$")) {
                    Element directive = new Element("directive");
                    
                    directive.setAttribute("name", line.replaceAll(" .*", "").replace("$", ""));
                    
                    directive.setText(line.replaceAll("^[^ ]* ", ""));
                      
                    if (directive.getAttributeValue("name").equals("ORIGIN")) {
                        origin = directive.getText();
                    }
                    
                    root.addContent(directive);
                    
                } else {                

                    if ( line.contains("(")) {
                        while ( !line.contains(")") ) {
                            line += br.readLine().replaceAll(";.*", "");
                        }
                        
                        line = line.replace("(", "");
                        line = line.replace(")", "");
                        
                    }
                                        
                    
                    String[] lineParts = line.split("\\s+");
                    
                    
                    
                    boolean pack = false;
                    int where = 0;
                    int packOffset = 0;
                    for (int i = 0 ; i < lineParts.length ; i ++) {
                        if (!pack && lineParts[i].startsWith("\"")) {
                            pack = true;
                            where = i - packOffset;
                            lineParts[i - packOffset] = lineParts[i].substring(1);                                
                        } else if (pack) {
                            
                            //FIXME: this method strips out spaces in strings
                            lineParts[where] += " " +lineParts[i];
                            packOffset ++;
                        } else {
                            lineParts[i - packOffset] = lineParts[i];
                        }
                        
                        if (pack && lineParts[i].endsWith("\"")) {
                                lineParts[where] = lineParts[where].substring(0,lineParts[where].length() - 1);
                                pack = false;
                        }
                        
                    } 
                    
                    
                    String [] lineParts2 = new String[lineParts.length - packOffset];
                    
                    System.arraycopy(lineParts, 0, lineParts2, 0, lineParts.length - packOffset);
                    
                    lineParts = lineParts2;
                    
                    
                    String owner;
                    
                    if ( lineParts[0].length() > 0 ) {
                        
                        if (getExpandOrigin(file) && lineParts[0].equals("@")) {
                            lineParts[0] = origin;
                        }                      
                        
                        lineParts[0] = normalizeAddr(lineParts[0], origin, file);
                     
                        owner =  lineParts[0];
                        lastOwner = lineParts[0];
                    } else {
                        owner = lastOwner;
                    }
                    
                    
                    String ttl = null;
                    int offset = 1;
                    try {
                        ttl = Integer.parseInt(lineParts[offset]) + "";
                        offset = offset + 1;
                    } catch (NumberFormatException ex) {
                        
                    }
                    
                    String classType = null;
                    //add support for ipv6
                    if (lineParts[offset].equals("IN")) {
                        classType = lineParts[offset];
                        offset += 1;
                    } else {
                        throw new RuntimeException("Class " + lineParts[offset] + " not supported");
                    }
                    
                    String recordType = lineParts[offset];
                    
                    offset++;

                    Element el = new Element("record");
                    
                    el.setAttribute("owner", owner);
                    
                    if (ttl != null) el.setAttribute("ttl", ttl);
                    if (classType != null) el.setAttribute("class", classType);                    
                    
                    if (recordType.equals("SOA")) {
                        
                        el.setAttribute("recordtype", "soa");
                        el.setAttribute("nsname", normalizeAddr(lineParts[offset], origin, file));
                        el.setAttribute("hostmaster", lineParts[offset + 1]);
                        el.setAttribute("serial", lineParts[offset + 2]);
                        el.setAttribute("refresh", lineParts[offset + 3]);                        
                        el.setAttribute("retry", lineParts[offset + 4]);                        
                        el.setAttribute("expire", lineParts[offset + 5]);                        
                        el.setAttribute("minimumttl", lineParts[offset + 6]);                        
                        
                    } else if (recordType.equals("NS")) {
                        
                        el.setAttribute("recordtype", "ns");
                        el.setAttribute("nsname", normalizeAddr(lineParts[offset], origin, file));

                    } else if (recordType.equals("A")) {
                        el.setAttribute("recordtype", "a");
                        el.setAttribute("ip", lineParts[offset]);
                    } else if (recordType.equals("MX")) {
                        el.setAttribute("recordtype", "mx");
                        el.setAttribute("dist", lineParts[offset]);
                        el.setAttribute("mxname", normalizeAddr(lineParts[offset + 1], origin, file));
                    } else if (recordType.equals("RP")) {
                        el.setAttribute("recordtype", "rp");
                        el.setAttribute("email", lineParts[offset]);
                        el.setAttribute("text", normalizeAddr(lineParts[offset + 1],  origin, file));
                    } else if (recordType.equals("HINFO")) {
                        el.setAttribute("recordtype", "hinfo");
                        el.setAttribute("hw", lineParts[offset]);
                        el.setAttribute("soft", lineParts[offset + 1]);
                    } else if (recordType.equals("TXT")) {
                        el.setAttribute("recordtype", "txt");
                        el.setAttribute("text", lineParts[offset]);                        
                    } else if (recordType.equals("CNAME")) {
                        el.setAttribute("recordtype", "cname");
                        el.setAttribute("pointer", normalizeAddr(lineParts[offset],  origin, file));                                            
                    } else if (recordType.equals("PTR")) {
                        el.setAttribute("recordtype", "ptr");
                        el.setAttribute("addr", normalizeAddr(lineParts[offset], origin, file));                                            
                        el.setAttribute("ip", arpa2ip(owner));                                            
                    } else {
                        throw new RuntimeException("Record type " + recordType + " not supported");
                    }                   
                    
                    root.addContent(el);
                    
                }
            }

            return new Document(root);

        } catch (Exception e) {
            throw new RuntimeException("Error parsing configuration file ", e);
        }

    }
    
    private String normalizeAddr(String host, String origin, ConfigurationFile file) {
        if ( getNormalizeNames(file) && !host.endsWith(".")) {
            return host + "." + origin;
        } else {
            return host; 
        }
    }
    
    private String arpa2ip ( String arpa) {
        
        if (!arpa.endsWith("in-addr.arpa.")) throw new RuntimeException("Unsupported ptr owner " + arpa);
        
        String [] parts = arpa.split("\\.");
        String ip = "";
        
        for (int i = parts.length - 3 ; i > -1 ; i--) {
            ip += (ip.length() > 0 ? "." : "" ) + parts[i];
        }
        
        
        return ip;
    }

    @Override
    public void serializeConfiguration(Document config, Writer output, ConfigurationFile file) {

        try {

            for (Object o : config.getRootElement().getChildren()) {
                
                Element el = (Element) o;
                
                if (el.getName().equals("directive")) {
                    output.append("$" + el.getAttributeValue("name") + " " + el.getText() + "\n");
                } else {                                    
                    
                    String str;
                    
                    str =  el.getAttributeValue("owner");
                    
                    if (el.getAttribute("ttl") != null) str += "\t" + el.getAttributeValue("ttl");
                    if (el.getAttribute("class") != null) str += "\t" + el.getAttributeValue("class");                    
                    
                    if (el.getAttribute("recordtype") != null) str += "\t" + el.getAttributeValue("recordtype").toUpperCase();
                    
                    if (el.getAttributeValue("recordtype").equals("soa")) {                                               
                        if (el.getAttribute("nsname") != null) str += "\t" + el.getAttributeValue("nsname");
                        if (el.getAttribute("hostmaster") != null) str += "\t" + el.getAttributeValue("hostmaster");
                        if (el.getAttribute("serial") != null) str += "\t" + el.getAttributeValue("serial");
                        if (el.getAttribute("refresh") != null) str += "\t" + el.getAttributeValue("refresh");                        
                        if (el.getAttribute("retry") != null) str += "\t" + el.getAttributeValue("retry");                        
                        if (el.getAttribute("expire") != null) str += "\t" + el.getAttributeValue("expire");                        
                        if (el.getAttribute("minimumttl") != null) str += "\t" + el.getAttributeValue("minimumttl");                                                
                    } else if (el.getAttributeValue("recordtype").equals("ns")) {                                                
                        if (el.getAttribute("nsname") != null) str += "\t" + el.getAttributeValue("nsname");
                    } else if (el.getAttributeValue("recordtype").equals("a")) {                        
                        if (el.getAttribute("ip") != null) str += "\t" + el.getAttributeValue("ip");
                    } else if (el.getAttributeValue("recordtype").equals("mx")) {                        
                        if (el.getAttribute("dist") != null) str += "\t" + el.getAttributeValue("dist");
                        if (el.getAttribute("mxname") != null) str += "\t" + el.getAttributeValue("mxname");
                    } else if (el.getAttributeValue("recordtype").equals("rp")) {                        
                        if (el.getAttribute("email") != null) str += "\t" + el.getAttributeValue("email");
                        if (el.getAttribute("text") != null) str += "\t" + el.getAttributeValue("text");
                    } else if (el.getAttributeValue("recordtype").equals("hinfo")) {                        
                        if (el.getAttribute("hw") != null) str += "\t\"" + el.getAttributeValue("hw") + "\"";
                        if (el.getAttribute("soft") != null) str += "\t\"" + el.getAttributeValue("soft") + "\"";
                    } else if (el.getAttributeValue("recordtype").equals("txt")) {                        
                        if (el.getAttribute("text") != null) str += "\t\"" + el.getAttributeValue("text") + "\"";
                    } else if (el.getAttributeValue("recordtype").equals("cname")) {                        
                        if (el.getAttribute("pointer") != null) str += "\t" + el.getAttributeValue("pointer");                                            
                    } else if (el.getAttributeValue("recordtype").equals("ptr")) {                        
                        if (el.getAttribute("addr") != null) str += "\t" + el.getAttributeValue("addr");                                                                   
                    } else {
                        throw new RuntimeException("Record type " + el.getAttributeValue("recordtype") + " not supported");
                    }    
                                                         
                    output.append(str + "\n");
                                    
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error serializing configuration file ", e);
        }

    }

    private void serialize(Element n, Writer w) throws IOException {


        if (n.getName().equals("directive")) {
            w.write(n.getAttributeValue("name"));
            if (n.getAttribute("separator") != null)
                w.write(n.getAttributeValue("separator"));
            w.write(n.getText());
            w.write("\n");
        } else if (n.getName().equals("section")) {
            w.write(n.getAttributeValue("name"));            
            w.write("\n");
        }
        
        for (Object o : n.getChildren()) {
            serialize((Element) o, w);
        }

        w.flush();


    }

}
