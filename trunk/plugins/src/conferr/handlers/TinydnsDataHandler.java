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

import conferr.ConfigurationFile;
import conferr.Handler;
import conferr.ImpossibleConfigurationException;
import conferr.Parameter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import java.util.Vector;
import org.jdom.Document;
import org.jdom.Element;


public class TinydnsDataHandler implements Handler {

    @Override
    public Vector<Parameter> getDefaultParameters() {
        return new Vector<Parameter>();
    }

    @Override
    public void serializeConfiguration(Document config, Writer output, ConfigurationFile file) throws ImpossibleConfigurationException {
        try {
    
            Element root = config.getRootElement();
    
            for (Object o : root.getChildren("record")) {

                Element record = (Element) o;
                
                String type = record.getAttributeValue("recordtype");

                if (type.equals("soa+ns")) {
                    
                        //.fqdn:ip:x:ttl:timestamp:lo
                        String symbol = ".";                        
                        String [] params = { "owner", "ip", "nsname", "ttl", "timestamp", "lo"};
                        serializeField(params, output, symbol, record);
                        
                } else if (type.equals("ns+a")) {
                    
                        //   &fqdn:ip:x:ttl:timestamp:lo
                        String symbol = "&";
                        String [] params = { "owner", "ip", "nsname", "ttl", "timestamp", "lo"};
                        serializeField(params, output, symbol, record);
                        
                } else if (type.equals("a+ptr")) {
                    
                        // =fqdn:ip:ttl:timestamp:lo
                        String symbol = "=";
                        String [] params = { "owner", "ip", "ttl", "timestamp", "lo"};
                        serializeField(params, output, symbol, record);                        
                } else if (type.equals("a")) {
                                    
                        //  +fqdn:ip:ttl:timestamp:lo
                        String symbol = "+";
                        String [] params = { "owner", "ip", "ttl", "timestamp", "lo"};
                        serializeField(params, output, symbol, record);                        
                        
                } else if (type.equals("mx+a")) {

                        //  @fqdn:ip:x:dist:ttl:timestamp:lo
                        String symbol = "@";
                        String [] params = { "owner", "ip", "mxname","dist", "ttl", "timestamp", "lo"};
                        serializeField(params, output, symbol, record);                        

                }  else if (type.equals("txt")) {
                        //  'fqdn:s:ttl:timestamp:lo
                        String symbol = "'";
                        String [] params = { "owner", "text", "ttl", "timestamp", "lo"};
                        serializeField(params, output, symbol, record);                        

                } else if (type.equals("cname")) {
                    
                        //Cfqdn:p:ttl:timestamp:lo
                        String symbol = "C";
                        String [] params = { "owner", "pointer", "ttl", "timestamp", "lo"};
                        serializeField(params, output, symbol, record);                        
                        
                } else if (type.equals("hinfo")) {
                    
                        // :fqdn:n:rdata:ttl:timestamp:lo
                        String typeId = 13 + "";
                        String data = "";                        
                        
                        if (record.getAttribute("hw") != null) {
                            data += str2dns(record.getAttributeValue("hw"));
                        }
                        if (record.getAttribute("soft") != null) {
                            data += str2dns(record.getAttributeValue("soft"));
                        }
                        
                        record.setAttribute("data", data);
                        record.setAttribute("typeid", typeId);
                        
                        String symbol = ":";
                        String [] params = { "owner", "typeid","data", "ttl", "timestamp", "lo"};
                        serializeField(params, output, symbol, record); 

                } else if (type.equals("rp")) {
                    
                        // :fqdn:n:rdata:ttl:timestamp:lo
                        String typeId = 17 + "";
                        String data = "";                        
                        
                        if (record.getAttribute("email") != null) {
                            data += name2dns(record.getAttributeValue("email"));
                        }
                        
                        data += "\\000";
                        
                        if (record.getAttribute("text") != null) {
                            data += name2dns(record.getAttributeValue("text"));
                        }
                        
                        record.setAttribute("data", data);
                        record.setAttribute("typeid", typeId);
                        
                        String symbol = ":";
                        String [] params = { "owner", "typeid","data", "ttl", "timestamp", "lo"};
                        serializeField(params, output, symbol, record); 
                        
                } else {
                        throw new RuntimeException("Unsupported record type " + type);
                }

            }
            
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        
    }

    
    @Override
    public Document parseConfiguration(Reader input, ConfigurationFile file) {
        try {

            BufferedReader buf = new BufferedReader(input);

            Element root = new Element("root");
            Document doc = new Document(root);



            String line;

            while ((line = buf.readLine()) != null) {

                char type = line.charAt(0);

                String[] parts = line.substring(1).split(":");

                Element record = new Element("record");

                switch (type) {

                    case '.':
                        
                        //.fqdn:ip:x:ttl:timestamp:lo
                        
                        record.setAttribute("recordtype", "soa+ns");
                        record.setAttribute("owner", parts[0]);

                        if (parts.length > 1) {
                            record.setAttribute("ip", parts[1]);
                        }
                        if (parts.length > 2) {
                            record.setAttribute("nsname", expand(parts[2], parts[0], ".ns."));
                        }
                        if (parts.length > 3) {
                            record.setAttribute("ttl", parts[3]);
                        }
                        if (parts.length > 4) {
                            record.setAttribute("timestap", parts[4]);
                        }
                        if (parts.length > 5) {
                            record.setAttribute("lo", parts[5]);
                        }
                        break;
                        
                    case '&':
                        //   &fqdn:ip:x:ttl:timestamp:lo
                        record.setAttribute("recordtype", "ns+a");
                        record.setAttribute("owner", parts[0]);

                        if (parts.length > 1) {
                            record.setAttribute("ip", parts[1]);
                        }
                        if (parts.length > 2) {
                            record.setAttribute("nsname", expand(parts[2], parts[0], ".ns."));
                        }
                        if (parts.length > 3) {
                            record.setAttribute("ttl", parts[3]);
                        }
                        if (parts.length > 4) {
                            record.setAttribute("timestap", parts[4]);
                        }
                        if (parts.length > 5) {
                            record.setAttribute("lo", parts[5]);
                        }
                        break;
                    case '=':
                        // =fqdn:ip:ttl:timestamp:lo
                        record.setAttribute("recordtype", "a+ptr");
                        record.setAttribute("owner", parts[0]);

                        if (parts.length > 1) {
                            record.setAttribute("ip", parts[1]);                                                       
                            record.setAttribute("inaddr", ip2inaddr(parts[1]) );
                        }
                        if (parts.length > 2) {
                            record.setAttribute("ttl", parts[2]);
                        }
                        if (parts.length > 3) {
                            record.setAttribute("timestap", parts[3]);
                        }
                        if (parts.length > 4) {
                            record.setAttribute("lo", parts[4]);
                        }
                        break;
                    case '+':
                        //  +fqdn:ip:ttl:timestamp:lo
                        record.setAttribute("recordtype", "a");
                        record.setAttribute("owner", parts[0]);

                        if (parts.length > 1) {
                            record.setAttribute("ip", parts[1]);
                        }
                        if (parts.length > 2) {
                            record.setAttribute("ttl", parts[2]);
                        }
                        if (parts.length > 3) {
                            record.setAttribute("timestap", parts[3]);
                        }
                        if (parts.length > 4) {
                            record.setAttribute("lo", parts[4]);
                        }
                        break;
                    case '@':
                        //  @fqdn:ip:x:dist:ttl:timestamp:lo
                        record.setAttribute("recordtype", "mx+a");
                        record.setAttribute("owner", parts[0]);

                        if (parts.length > 1) {
                            record.setAttribute("ip", parts[1]);
                        }
                        if (parts.length > 2) {
                            record.setAttribute("mxname", expand(parts[2], parts[0], ".mx."));
                        }
                        if (parts.length > 3) {
                            record.setAttribute("dist", parts[3]);
                        }
                        if (parts.length > 4) {
                            record.setAttribute("ttl", parts[4]);
                        }
                        if (parts.length > 5) {
                            record.setAttribute("timestap", parts[5]);
                        }
                        if (parts.length > 6) {
                            record.setAttribute("lo", parts[6]);
                        }
                        break;
                    case '\'':
                        //  'fqdn:s:ttl:timestamp:lo
                        record.setAttribute("recordtype", "txt");
                        record.setAttribute("owner", parts[0]);

                        if (parts.length > 1) {
                            record.setAttribute("text", parts[1]);
                        }
                        if (parts.length > 2) {
                            record.setAttribute("ttl", parts[2]);
                        }
                        if (parts.length > 3) {
                            record.setAttribute("timestap", parts[3]);
                        }
                        if (parts.length > 4) {
                            record.setAttribute("lo", parts[4]);
                        }
                        break;
                    case 'C':
                        //Cfqdn:p:ttl:timestamp:lo
                        record.setAttribute("recordtype", "cname");
                        record.setAttribute("owner", parts[0]);

                        if (parts.length > 1) {
                            record.setAttribute("pointer", parts[1]);
                        }
                        if (parts.length > 2) {
                            record.setAttribute("ttl", parts[2]);
                        }
                        if (parts.length > 3) {
                            record.setAttribute("timestap", parts[3]);
                        }
                        if (parts.length > 4) {
                            record.setAttribute("lo", parts[4]);
                        }
                        break;
                    case ':':
                        // :fqdn:n:rdata:ttl:timestamp:lo
                        
                        String [] partsD = dns2str(parts[2]);
                                
                        switch (Integer.parseInt(parts[1])) {
                            case 13:
                                record.setAttribute("recordtype", "hinfo");
                                record.setAttribute("owner", parts[0]);
                                record.setAttribute("soft", partsD[2]);
                                record.setAttribute("hw", partsD[1]);
                                break;
                            case 17:
                                record.setAttribute("recordtype", "rp");
                                record.setAttribute("owner", parts[0]);
                                int i = 1;
                                
                                String sub = "";
                                
                                while (!partsD[i].equals("")) {
                                    sub += partsD[i] + ".";
                                    i++;
                                }
                                
                                record.setAttribute("email", sub);
                                
                                i++;
                                sub = "";
                                while (i < partsD.length) {
                                    sub += partsD[i] + ".";
                                    i++;
                                }
                                
                                record.setAttribute("text", sub);
                                
                                break;
                        }
                        
                        if (parts.length > 2) {
                            record.setAttribute("data", parts[2]);
                        }
                        if (parts.length > 3) {
                            record.setAttribute("ttl", parts[3]);
                        }
                        if (parts.length > 4) {
                            record.setAttribute("timestap", parts[4]);
                        }
                        if (parts.length > 5) {
                            record.setAttribute("lo", parts[5]);
                        }
                        
                        
                        
                        break;
                    default:
                        throw new RuntimeException("Unsupported record type " + type);
                }

                root.addContent(record);
            }

            return doc;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        
    }
    
    private String expand(String x, String owner, String fix) {
        if (x.contains(".")) {
            return x;
        } else {
            return x + fix + owner;
        }
    }

    private String[] dns2str(String dns) {
        //FIXME: this is not generic, it doesn't always work
        return dns.split("\\\\...");
        
    }
    
    private String ip2inaddr(String ip) {
        String [] split = ip.split("\\.");
        
        String ret = "";
        
        for (int i = split.length - 1; i > -1; i--) {
            ret += split[i] + ".";
        }
        
        return ret + "in-addr.arpa";
    }
    
    private String name2dns (String dns) {

        if (dns == null) return "";
        
        //FIXME: doesn't work with escaped dots
        String [] split = dns.split("\\.");
        
        String ret = "";
        
        for (String s : split) {
            ret += str2dns(s);
        }
        
        return ret;
        
    }

    private void serializeField(String[] params, Writer output, String symbol, Element record) throws IOException {

        String[] parts = new String[params.length];

        for (int i = 0; i < params.length; i++) {
            if (record.getAttribute(params[i]) != null) {
                parts[i] = record.getAttributeValue(params[i]);
            } else {
                parts[i] = "";
            }
        }

        output.append(symbol + parts[0]);

        for (int i = 1; i < parts.length; i++) {
            output.append(":" + parts[i]);
        }
        
        output.append("\n");
    }
    
    private String str2dns (String text) {
        if (text == null) return ""; 
        return String.format("\\%03o", text.length()) + text;
    }
    
            
}
