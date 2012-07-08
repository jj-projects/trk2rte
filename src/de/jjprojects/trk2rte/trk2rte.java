/**
 * 
 */
package de.jjprojects.trk2rte;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Stack;
import java.util.logging.Logger;

import org.jdom.*;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 This java application takes an XMLTrackFile (GPX Format) and converts it into an route file (GPX Format as well).
 
 @author Copyright (C) 2012  JJ-Projects Joerg Juenger <BR>
  
<pre>
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 </pre>
 */
public class trk2rte extends DefaultHandler
{
  static final String GPXTag        = "gpx";
  static final String TrackTag      = "trk";
  static final String NameTag       = "name";
  static final String TimeTag       = "time";
  static final String TrackpointTag = "trkpt";
  
  static final String RouteTag      = "rte";
  static final String RoutepointTag = "rtept";
  
  static String routeFileName;
  
  static Logger log = Logger.getLogger("JJProjects");
  
  
  public trk2rte () {
     super();
     trkNodeStack = null;
     rteRoot = null;
     xmlNS = Namespace.getNamespace("http://www.topografix.com/GPX/1/0");
  }

  public static void main( String[] argv ) {
     

    if( argv.length != 2 )
    {
      System.err.println( "Usage:" );
      System.err.println( "java trk2rte <XmlTrackFile>  <XmlRouteFile>");
      System.err.println( "Example:" );
      System.err.println( "java trk2rte mytrack.gpx myroute.gpx" );
      System.exit( 1 );
    }

    try {
       XMLReader xr = XMLReaderFactory.createXMLReader();
       trk2rte handler = new trk2rte();
       xr.setContentHandler(handler);
       xr.setErrorHandler(handler);
       
       routeFileName = argv[1];
       FileReader r = new FileReader (argv[0]);
       
       xr.parse(new InputSource(r));

      
   } catch (SAXException  sxe ) {
      Exception e = ( sxe.getException() != null ) ? sxe.getException() : sxe;
      e.printStackTrace();
   } catch (IOException ioe) {
      ioe.printStackTrace();
   } finally {
   }
} // End of main function
  
  ////////////////////////////////////////////////////////////////////
  // Event handlers.
  ////////////////////////////////////////////////////////////////////


  public void startDocument ()
  {
     log.info("Start document");
     
     rteNodeStack = new Stack<Element>();
     trkNodeStack = new Stack<String> ();
  }


  public void endDocument ()
  {
     log.info("End document");
      try {
         Document doc = new Document(rteRoot);
         // serialize it onto System.out
         XMLOutputter serializer = new XMLOutputter();

         Format format = Format.getPrettyFormat();
         // use two space indent
         format.setIndent("  ");
         format.setLineSeparator ("\r\n"); 
         serializer.setFormat (format); 
         
         FileOutputStream outFile = new FileOutputStream (new File (routeFileName));
         serializer.output(doc, outFile);
         
      } catch (IOException e) {
         System.err.println(e);
      }
  }


  public void startElement (String uri, String name,
                String qName, Attributes atts)
  {
     String nodeName;
     if ("".equals (uri)) {
        log.info("Start element: " + qName);
        nodeName = qName;
     } else {
        log.info("Start element: {" + uri + "}" + name);
        nodeName = name;
     }
     
     // handle the gpx tag
     if (GPXTag == nodeName) {
        rteRoot = this.buildRootNode ();
        rteNodeStack.push(rteRoot);
     }
     
     // handle the track node
     if (TrackTag == nodeName) {
        log.info("Start element: " + atts.getLocalName(0) + ", " + atts.getType(0) + ", " + atts.getValue(0));
        log.info ("Start element: " + atts.getLocalName(1) + ", " + atts.getType(1) + ", " + atts.getValue(1) );
        
        //initiate an new route now; 
        Element rteElement = new Element (RouteTag, xmlNS);
        
        rteNodeStack.push(rteElement);
     }
     
     // handle track points
     if (TrackpointTag == nodeName) {
        log.info("Start element: " + atts.getLocalName(0) + ", " + atts.getType(0) + ", " + atts.getValue(0));
        log.info ("Start element: " + atts.getLocalName(1) + ", " + atts.getType(1) + ", " + atts.getValue(1) );
 
        // add new route waypoint here;
        Element rteElement = new Element (RoutepointTag, xmlNS);
        rteElement.setAttribute (new Attribute (atts.getLocalName(0), atts.getValue(0), Attribute.CDATA_TYPE));
        rteElement.setAttribute (new Attribute (atts.getLocalName(1), atts.getValue(1), Attribute.CDATA_TYPE));
        
        rteNodeStack.push(rteElement);
     }
     
     trkNodeStack.push(nodeName);
     log.info("stack: " + trkNodeStack); 
  }

  public void endElement (String uri, String name, String qName)
  {
    String nodeName;
     if ("".equals (uri)) {
        log.info("End element: " + qName);
        nodeName = qName;
     } else {
        log.info("End element:   {" + uri + "}" + name);
        nodeName = name;
     }
     
     if (GPXTag == nodeName || TrackTag == nodeName || TrackpointTag == nodeName) {
        Element child = rteNodeStack.pop ();
        if (! rteNodeStack.isEmpty ())
           rteNodeStack.peek().addContent(child);
        else if (child != rteRoot)
           rteRoot.addContent (child);
     }
     trkNodeStack.pop();
  }


  public void characters (char ch[], int start, int length)
  {
      String str = "";
      log.finest("Characters:    \"");
      for (int i = start; i < start + length; i++) {
         switch (ch[i]) {
            case '\\':
               log.finest("\\\\");
               break;
            case '"':
               log.finest("\\\"");
               break;
            case '\n':
               log.finest("\\n");
               break;
            case '\r':
               log.finest("\\r");
               break;
            case '\t':
               log.finest("\\t");
               break;
            default:
               str = str.concat(Character.toString(ch[i]));
               break;
         }
      }
      
      log.finest(str + "\"\n");
      
      log.info("Stack Element: " + trkNodeStack.peek() + " == " + str);
      // set the name in the route element
      if (NameTag == trkNodeStack.peek() && null != rteNodeStack.peek()) {
         Element nameEle = new Element(NameTag, xmlNS);
         nameEle.setText(str);
         rteNodeStack.peek().addContent(nameEle);
      }
  }

  private Element buildRootNode () {
     Element root =  new Element (GPXTag, xmlNS);
     root.setAttribute("version", "1.0");
     root.setAttribute("creator", "trk2rte by Joerg Juenger, JJ-Projects");
     Namespace xsiNS = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
     root.addNamespaceDeclaration (xsiNS);

     root.setAttribute (new Attribute("schemaLocation",
              "http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd",
                xsiNS));

     
    // Element metaData = new Element ("metadata");
    // metaData.addContent (timeNowElement());
    // root.addContent (metaData);
     
     return root;
  }
  
  @SuppressWarnings("unused")
private Element timeNowElement () {
     Element timeEle = new Element (TimeTag);
     Calendar cal = Calendar.getInstance();
     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
     timeEle.setText(sdf.format(cal.getTime()));

     return timeEle;
  };
  
  
  private Stack<String> trkNodeStack;
  private Stack<Element> rteNodeStack;
  private Element rteRoot;
  private Namespace xmlNS;
  
}  // End of Class Body
  

