<?xml version="1.0" encoding="UTF-8"?>

<!-- 
    Copyright 2021 EPOS ERIC

    This work is free: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This work is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
    
    Author: Rossana Paciello <rossana.paciello@ingv.it>
 -->
 <!--
    PURPOSE AND USAGE

    This XSLT is a proof of concept for the mapping WFS 2.0.0 GetCapabilities response to EPOS-DCAT-AP,
    as such, it must be considered as unstable, and can be updated any time based
    on the revisions to the EPOS-DCAT-AP specifications.  
-->

<xsl:transform
    xmlns:adms   = "http://www.w3.org/ns/adms#"
    xmlns:dc     = "http://purl.org/dc/elements/1.1/"
    xmlns:dct    = "http://purl.org/dc/terms/"
    xmlns:dctype = "http://purl.org/dc/dcmitype/"
    xmlns:dcat   = "http://www.w3.org/ns/dcat#"
    xmlns:dtct2.2 = "http://datacite.org/schema/kernel-2.2"
    xmlns:dtct3  = "http://datacite.org/schema/kernel-3"
    xmlns:dtct4  = "http://datacite.org/schema/kernel-4"
    xmlns:foaf   = "http://xmlns.com/foaf/0.1/"
    xmlns:geo    = "http://www.w3.org/2003/01/geo/wgs84_pos#"
    xmlns:gsp    = "http://www.opengis.net/ont/geosparql#"
    xmlns:locn   = "http://www.w3.org/ns/locn#"
    xmlns:oa     = "http://www.w3.org/ns/oa#"
    xmlns:org    = "http://www.w3.org/ns/org#"
    xmlns:owl    = "http://www.w3.org/2002/07/owl#"
    xmlns:prov   = "http://www.w3.org/ns/prov#"
    xmlns:rdf    = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:rdfs   = "http://www.w3.org/2000/01/rdf-schema#"
    xmlns:schema = "http://schema.org/"
    xmlns:skos   = "http://www.w3.org/2004/02/skos/core#"
    xmlns:vcard  = "http://www.w3.org/2006/vcard/ns#"
    xmlns:xlink  = "http://www.w3.org/1999/xlink"
    xmlns:xsi    = "http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xsl    = "http://www.w3.org/1999/XSL/Transform"
    xmlns:wdrs   = "http://www.w3.org/2007/05/powder-s#"
    xmlns:wfs    = "http://www.opengis.net/wfs/2.0"
    xmlns:ows    = "http://www.opengis.net/ows/1.1"
    xmlns:epos   = "https://www.epos-eu.org/epos-dcat-ap#"
    xmlns:hydra  = "http://www.w3.org/ns/hydra/core#"
    xmlns:http   = "http://www.w3.org/2006/http#"

    exclude-result-prefixes = "dtct2.2 dtct3 dtct4 oa xlink xsi xsl"
    version="1.0">

  <xsl:output method="xml"
              indent="yes"
              encoding="utf-8" />



  <xsl:template match="wfs:WMS_Capabilities">
    <rdf:RDF>
  	<xsl:call-template name="Dataset"/>
    <xsl:call-template name="wfs_service_respParty"/>
    </rdf:RDF>
  </xsl:template>


	<xsl:template name="SpatialCoverage">

    <xsl:variable name="southBoundLatitude" select="normalize-space(/ows:WMS_Capabilities/ows:Capability/ows:Layer/ows:EX_GeographicBoundingBox/ows:southBoundLatitude)"/>
    <xsl:variable name="northBoundLatitude" select="normalize-space(/ows:WMS_Capabilities/ows:Capability/ows:Layer/ows:EX_GeographicBoundingBox/ows:northBoundLatitude)"/>
    <xsl:variable name="eastBoundLongitude" select="normalize-space(/ows:WMS_Capabilities/ows:Capability/ows:Layer/ows:EX_GeographicBoundingBox/ows:eastBoundLongitude)"/>
    <xsl:variable name="westBoundLongitude" select="normalize-space(/ows:WMS_Capabilities/ows:Capability/ows:Layer/ows:EX_GeographicBoundingBox/ows:westBoundLongitude)"/>

<xsl:choose>
    <xsl:when test="string((string-length(normalize-space(/ows:WMS_Capabilities/ows:Capability/ows:Layer/ows:EX_GeographicBoundingBox/ows:southBoundLatitude)) &gt; '0')) != 'false'">
             
    <dct:spatial>
          <dct:Location>
            <locn:geometry rdf:datatype="http://www.opengis.net/ont/geosparql#wktLiteral">POLYGON((<xsl:value-of select="$westBoundLongitude"/><xsl:text> </xsl:text><xsl:value-of select="$southBoundLatitude"/>,<xsl:value-of select="$westBoundLongitude"/><xsl:text> </xsl:text><xsl:value-of select="$northBoundLatitude"/>,<xsl:value-of select="$eastBoundLongitude"/><xsl:text> </xsl:text><xsl:value-of select="$northBoundLatitude"/>,<xsl:value-of select="$eastBoundLongitude"/><xsl:text> </xsl:text><xsl:value-of select="$southBoundLatitude"/>,<xsl:value-of select="$westBoundLongitude"/><xsl:text> </xsl:text><xsl:value-of select="$southBoundLatitude"/>))</locn:geometry>
          </dct:Location>
    </dct:spatial>
</xsl:when>
</xsl:choose>
    


</xsl:template>



  <xsl:template name="Dataset">

    <xsl:variable name="identifier" select="translate(ows:ServiceIdentification/ows:Title, ' ', '')"/>

    <xsl:variable name="ServiceTitle" select="normalize-space(ows:ServiceIdentification/ows:Title)"/>
    <xsl:variable name="ServiceAbstract" select="normalize-space(ows:ServiceIdentification/ows:Abstract)"/>

    <!--  
   
    
    
    <xsl:variable name="getCapabilitiesURL" select="normalize-space(/ows:WMS_Capabilities/ows:Capability/ows:Request/ows:GetCapabilities/ows:DCPType/ows:HTTP/ows:Get/ows:OnlineResource/@xlink:href)" />
   
    <xsl:variable name="getMapWMS" select="normalize-space(/ows:WMS_Capabilities/ows:Capability/ows:Request/ows:GetMap/ows:DCPType/ows:HTTP/ows:Get/ows:OnlineResource/@xlink:href)"/>
    
    <xsl:variable name="getMapURL" select="concat($getMapWMS,'SERVICE=WMS')"/>
     -->
    
    <xsl:variable name="LayerTitle" select="normalize-space(FeatureTypeList/FeatureType/Name)"/>
    <xsl:variable name="LayerAbstract" select="normalize-space(FeatureTypeList/FeatureType/Abstract)"/>
    
    <xsl:variable name="ContactPersonID" select="translate(/wfs:WMS_Capabilities/ows:ServiceProvider/ows:ServiceContact/ows:IndividualName, ' ', '')"/>
   
     
    <dcat:Dataset rdf:about="https://www.epos-eu.org/epos-dcat-ap/Dataset/{$identifier}">

      <dct:identifier>https://www.epos-eu.org/epos-dcat-ap/Dataset/<xsl:value-of select="$identifier"/></dct:identifier>

      <dct:title><xsl:value-of select="$ServiceTitle"/></dct:title>
      <dct:description><xsl:value-of select="$ServiceAbstract"/></dct:description>
      <!-- <xsl:call-template name="SpatialCoverage"/> -->
      <dcat:contactPoint rdf:resource="https://www.epos-eu.org/epos-dcat-ap/Person/{$ContactPersonID}/contactPoint"/>

        <xsl:for-each select="ows:ServiceIdentification/ows:Keywords/ows:Keyword">
          <dcat:keyword>
            <xsl:value-of select="normalize-space(.)"/>
          </dcat:keyword>
        </xsl:for-each>

        <xsl:for-each select="./FeatureTypeList/FeatureType">
          <dcat:distribution>
            <dcat:Distribution rdf:about="https://www.epos-eu.org/epos-dcat-ap/Dataset/Distribution/{$identifier}">
              <dct:identifier>https://www.epos-eu.org/epos-dcat-ap/Dataset/Distribution/<xsl:value-of select="$identifier"/></dct:identifier>
              <dct:title><xsl:value-of select="$LayerTitle"/></dct:title>
              <dct:description><xsl:value-of select="$LayerAbstract"/></dct:description>

              <dct:type rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://publications.europa.eu/resource/authority/distribution-type/WEB_SERVICE</dct:type>

              <dct:format rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI">http://publications.europa.eu/resource/authority/file-type/JSON</dct:format>

              <dct:conformsTo>
                <epos:WebService rdf:about="https://www.epos-eu.org/epos-dcat-ap/Dataset/Distribution/WS/{$identifier}">

                  <schema:identifier>https://www.epos-eu.org/epos-dcat-ap/Dataset/Distribution/WS/<xsl:value-of select="$identifier"/></schema:identifier>
                  <schema:name><xsl:value-of select="$LayerTitle"/></schema:name>
                  <schema:description><xsl:value-of select="$LayerAbstract"/></schema:description>
                  <xsl:call-template name="SpatialCoverage"/>
                  <dcat:contactPoint rdf:resource="https://www.epos-eu.org/epos-dcat-ap/Person/{$ContactPersonID}/contactPoint"/>
                  <hydra:entrypoint rdf:datatype="http://www.w3.org/2001/XMLSchema#anyURI"><xsl:value-of select="$getCapabilitiesURL"/>request=GetCapabilities</hydra:entrypoint>
                  <hydra:supportedOperation rdf:resource="{$getMapURL}" />
                  <xsl:for-each select="/ows:WMS_Capabilities/ows:Service/ows:KeywordList/ows:Keyword">
                    <schema:keywords>
                      <xsl:value-of select="normalize-space(.)"/>
                    </schema:keywords>
                  </xsl:for-each>
                </epos:WebService>
              </dct:conformsTo>


              <dcat:accessURL>


                <hydra:Operation rdf:about="{$getMapURL}">
                  <hydra:method>GET</hydra:method>
                  <hydra:returns>image/png</hydra:returns>
                  <hydra:property>
                  <hydra:IriTemplate>

                  <hydra:template><xsl:value-of select="$getMapURL"/>{&amp;version, request, layers, crs, format, width, height, styles}&amp;bbox={minlatitude,minlongitude,maxlatitude,maxlongitude}</hydra:template>

        <hydra:mapping>
          <hydra:IriTemplateMapping>
            <hydra:variable>version</hydra:variable>
            <rdfs:range>xsd:string</rdfs:range>
            <rdfs:label>WMS version</rdfs:label>
            <http:paramValue><xsl:value-of select="normalize-space(/ows:WMS_Capabilities/@version)" /></http:paramValue>
            <schema:defaultValue><xsl:value-of select="normalize-space(/ows:WMS_Capabilities/@version)" /></schema:defaultValue>
            <hydra:required rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean">true</hydra:required>
          </hydra:IriTemplateMapping>
        </hydra:mapping>

        <hydra:mapping>
          <hydra:IriTemplateMapping>
            <hydra:variable>request</hydra:variable>
            <rdfs:range>xsd:string</rdfs:range>
            <rdfs:label>Request type</rdfs:label>
            <http:paramValue>GetMap</http:paramValue>
            <schema:defaultValue>GetMap</schema:defaultValue>
            <hydra:required rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean">true</hydra:required>
          </hydra:IriTemplateMapping>
        </hydra:mapping>

        <hydra:mapping>
          <hydra:IriTemplateMapping>
            <hydra:variable>layers</hydra:variable>
            <rdfs:range>xsd:string</rdfs:range>
            <rdfs:label>Layers</rdfs:label>

            <xsl:for-each select="/ows:WMS_Capabilities/ows:Capability/ows:Layer/ows:Layer">
              <xsl:if test="@queryable = 1">
              <http:paramValue>
                <xsl:value-of select="normalize-space(ows:Name)"/>
              </http:paramValue>
              </xsl:if>
            </xsl:for-each>

            <schema:defaultValue>
              <xsl:value-of select="normalize-space(/ows:WMS_Capabilities/ows:Capability/ows:Layer/ows:Layer/ows:Name)"/></schema:defaultValue>
            <hydra:required rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean">true</hydra:required>
          </hydra:IriTemplateMapping>
        </hydra:mapping>

        <hydra:mapping>
          <hydra:IriTemplateMapping>
            <hydra:variable>crs</hydra:variable>
            <rdfs:range>xsd:string</rdfs:range>
            <rdfs:label>Spatial Reference System</rdfs:label>
            <http:paramValue>EPSG:4326</http:paramValue>
            <schema:defaultValue>EPSG:4326</schema:defaultValue>
            <hydra:required rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean">true</hydra:required>
          </hydra:IriTemplateMapping>
        </hydra:mapping>

        <hydra:mapping>
          <hydra:IriTemplateMapping>
            <hydra:variable>format</hydra:variable>
            <rdfs:range>xsd:string</rdfs:range>
            <rdfs:label>Output format</rdfs:label>
            <hydra:property>schema:encodingFormat</hydra:property>
            <hydra:required rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean">true</hydra:required>

            <xsl:for-each select="/ows:WMS_Capabilities/ows:Capability/ows:Request/ows:GetMap/ows:Format">

              <http:paramValue>
                <xsl:value-of select="normalize-space(.)"/>
              </http:paramValue>

            </xsl:for-each>

            <schema:defaultValue>
              <xsl:value-of select="normalize-space(/ows:WMS_Capabilities/ows:Capability/ows:Request/ows:GetMap/ows:Format)"/>
            </schema:defaultValue>

          </hydra:IriTemplateMapping>
        </hydra:mapping>


        <hydra:mapping>
          <hydra:IriTemplateMapping>
            <hydra:variable>width</hydra:variable>
            <rdfs:range>xsd:string</rdfs:range>
            <rdfs:label>Width of the output map</rdfs:label>
            <http:paramValue>1536</http:paramValue>
            <schema:defaultValue>1536</schema:defaultValue>
            <hydra:required rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean">true</hydra:required>
          </hydra:IriTemplateMapping>
        </hydra:mapping>

        <hydra:mapping>
          <hydra:IriTemplateMapping>
            <hydra:variable>height</hydra:variable>
            <rdfs:range>xsd:string</rdfs:range>
            <rdfs:label>Height of the output map</rdfs:label>
            <http:paramValue>811</http:paramValue>
            <schema:defaultValue>811</schema:defaultValue>
            <hydra:required rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean">true</hydra:required>
          </hydra:IriTemplateMapping>
        </hydra:mapping>

        <hydra:mapping>
          <hydra:IriTemplateMapping>
            <hydra:variable>styles</hydra:variable>
            <rdfs:range>xsd:string</rdfs:range>
            <rdfs:label>Symbols style</rdfs:label>

            <xsl:for-each select="/ows:WMS_Capabilities/ows:Capability/ows:Layer/ows:Layer/ows:Style">

              <http:paramValue>
                <xsl:value-of select="normalize-space(ows:Name)"/>
              </http:paramValue>

            </xsl:for-each>


            <schema:defaultValue>
              <xsl:value-of select="normalize-space(/ows:WMS_Capabilities/ows:Capability/ows:Layer/ows:Layer/ows:Style/ows:Name)"/>
            </schema:defaultValue>
            <hydra:required rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean">true</hydra:required>
          </hydra:IriTemplateMapping>
        </hydra:mapping>

        <hydra:mapping>
          <hydra:IriTemplateMapping>
            <hydra:variable>minlatitude</hydra:variable>
            <hydra:property>epos:southernmostLatitude</hydra:property>
            <rdfs:range>xsd:float</rdfs:range>
            <rdfs:label>Minimum latitude</rdfs:label>
            <schema:minValue>
              <xsl:value-of select="normalize-space(/ows:WMS_Capabilities/ows:Capability/ows:Layer/ows:EX_GeographicBoundingBox/ows:southBoundLatitude)"/>
            </schema:minValue>
            <schema:maxValue>
              <xsl:value-of select="normalize-space(/ows:WMS_Capabilities/ows:Capability/ows:Layer/ows:EX_GeographicBoundingBox/ows:northBoundLatitude)"/>
            </schema:maxValue>
            <schema:defaultValue>
              <xsl:value-of select="normalize-space(/ows:WMS_Capabilities/ows:Capability/ows:Layer/ows:EX_GeographicBoundingBox/ows:southBoundLatitude)"/>
            </schema:defaultValue>
            <hydra:required rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean">true</hydra:required>
          </hydra:IriTemplateMapping>
        </hydra:mapping>

        <hydra:mapping>
          <hydra:IriTemplateMapping>
            <hydra:variable>maxlatitude</hydra:variable>
            <hydra:property>epos:northernmostLatitude</hydra:property>
            <rdfs:range>xsd:float</rdfs:range>
            <rdfs:label>Maximum latitude</rdfs:label>
            <schema:minValue><xsl:value-of select="normalize-space(/ows:WMS_Capabilities/ows:Capability/ows:Layer/ows:EX_GeographicBoundingBox/ows:southBoundLatitude)"/></schema:minValue>
            <schema:maxValue><xsl:value-of select="normalize-space(/ows:WMS_Capabilities/ows:Capability/ows:Layer/ows:EX_GeographicBoundingBox/ows:northBoundLatitude)"/></schema:maxValue>
            <schema:defaultValue>
              <xsl:value-of select="normalize-space(/ows:WMS_Capabilities/ows:Capability/ows:Layer/ows:EX_GeographicBoundingBox/ows:northBoundLatitude)"/>
            </schema:defaultValue>
            <hydra:required rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean">true</hydra:required>
          </hydra:IriTemplateMapping>
        </hydra:mapping>

        <hydra:mapping>
          <hydra:IriTemplateMapping>
            <hydra:variable>minlongitude</hydra:variable>
            <hydra:property>epos:westernmostLongitude</hydra:property>
            <rdfs:range>xsd:float</rdfs:range>
            <rdfs:label>Minimum longitude</rdfs:label>
            <schema:minValue><xsl:value-of select="normalize-space(/ows:WMS_Capabilities/ows:Capability/ows:Layer/ows:EX_GeographicBoundingBox/ows:westBoundLongitude)"/></schema:minValue>
            <schema:maxValue>
              <xsl:value-of select="normalize-space(/ows:WMS_Capabilities/ows:Capability/ows:Layer/ows:EX_GeographicBoundingBox/ows:eastBoundLongitude)"/>
            </schema:maxValue>
            <schema:defaultValue>
              <xsl:value-of select="normalize-space(/ows:WMS_Capabilities/ows:Capability/ows:Layer/ows:EX_GeographicBoundingBox/ows:westBoundLongitude)"/>
            </schema:defaultValue>
            <hydra:required rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean">true</hydra:required>
          </hydra:IriTemplateMapping>
        </hydra:mapping>

        <hydra:mapping>
          <hydra:IriTemplateMapping>
            <hydra:variable>maxlongitude</hydra:variable>
            <hydra:property>epos:easternmostLongitude</hydra:property>
            <rdfs:range>xsd:float</rdfs:range>
            <rdfs:label>Maximum longitude</rdfs:label>
            <schema:minValue><xsl:value-of select="normalize-space(/ows:WMS_Capabilities/ows:Capability/ows:Layer/ows:EX_GeographicBoundingBox/ows:westBoundLongitude)"/></schema:minValue>
            <schema:maxValue><xsl:value-of select="normalize-space(/ows:WMS_Capabilities/ows:Capability/ows:Layer/ows:EX_GeographicBoundingBox/ows:eastBoundLongitude)"/></schema:maxValue>
            <schema:defaultValue><xsl:value-of select="normalize-space(/ows:WMS_Capabilities/ows:Capability/ows:Layer/ows:EX_GeographicBoundingBox/ows:eastBoundLongitude)"/></schema:defaultValue>
            <hydra:required rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean">true</hydra:required>
          </hydra:IriTemplateMapping>
        </hydra:mapping>




      </hydra:IriTemplate>
</hydra:property>

    </hydra:Operation>

  </dcat:accessURL>



            </dcat:Distribution>
          </dcat:distribution>
        </xsl:for-each>


    </dcat:Dataset>

   </xsl:template>



<xsl:template name="wfs_service_respParty">

  <xsl:variable name="ContactPerson" select="normalize-space(./ows:Service/ows:ContactInformation/ows:ContactPersonPrimary/ows:ContactPerson)"/>
  <xsl:variable name="ContactPersonID" select="translate(./ows:Service/ows:ContactInformation/ows:ContactPersonPrimary/ows:ContactPerson, ' ', '')"/>
  <schema:Person rdf:about="https://www.epos-eu.org/epos-dcat-ap/Person/{$ContactPersonID}">



     <schema:identifier>https://www.epos-eu.org/epos-dcat-ap/Person/<xsl:value-of select="$ContactPersonID"/></schema:identifier>
     <schema:familyName><xsl:value-of select="substring-after($ContactPerson, ' ')"/></schema:familyName>
     <schema:givenName><xsl:value-of select="substring-before($ContactPerson, ' ')"/></schema:givenName>



           <xsl:choose>
             <xsl:when test="string((string-length(string(./ows:Service/ows:ContactInformation/ows:ContactPersonPrimary/ows:ContactOrganization)) &gt; '0')) != 'false'">
               <schema:affiliation>
                 <schema:Organization>
	           <schema:identifier>
                     <xsl:value-of select="translate(./ows:Service/ows:ContactInformation/ows:ContactPersonPrimary/ows:ContactOrganization, ' ', '')"/>
                   </schema:identifier>
                   <schema:legalName>
                     <xsl:value-of select="string(./ows:Service/ows:ContactInformation/ows:ContactPersonPrimary/ows:ContactOrganization)"/>
                   </schema:legalName>
                 </schema:Organization>
               </schema:affiliation>
             </xsl:when>
           </xsl:choose>



     <!-- Address -->


       <schema:address>
         <schema:PostalAddress>

               <xsl:choose>
                 <xsl:when test="string((string-length(string(./ows:Service/ows:ContactInformation/ows:ContactAddress/ows:Address)) &gt; '0')) != 'false'">
                   <schema:streetAddress>
                     <xsl:value-of select="string(./ows:Service/ows:ContactInformation/ows:ContactAddress/ows:Address)"/>
                   </schema:streetAddress>
                 </xsl:when>

               </xsl:choose>


           <xsl:if test="normalize-space(./ows:Service/ows:ContactInformation/ows:ContactAddress/ows:City)">
             <schema:addressLocality>
                 <xsl:value-of select="normalize-space(./ows:Service/ows:ContactInformation/ows:ContactAddress/ows:City)"/>
               </schema:addressLocality>
           </xsl:if>



           <xsl:if test="normalize-space(./ows:Service/ows:ContactInformation/ows:ContactAddress/ows:PostCode)">
             <schema:postalCode>
                 <xsl:value-of select="normalize-space(./ows:Service/ows:ContactInformation/ows:ContactAddress/ows:PostCode)"/>
               </schema:postalCode>
           </xsl:if>

           <xsl:if test="normalize-space(./ows:Service/ows:ContactInformation/ows:ContactAddress/ows:Country)">
             <schema:addressCountry>
                 <xsl:value-of select="normalize-space(./ows:Service/ows:ContactInformation/ows:ContactAddress/ows:Country)"/>
              </schema:addressCountry>
           </xsl:if>

         </schema:PostalAddress>
      </schema:address>





   <!-- Phone/Fax -->

         <xsl:if test="normalize-space(./ows:Service/ows:ContactInformation/ows:ContactVoiceTelephone)">

               <schema:telephone>
                 <xsl:value-of select="normalize-space(./ows:Service/ows:ContactInformation/ows:ContactVoiceTelephone)"/>
             </schema:telephone>
         </xsl:if>
         <xsl:if test="normalize-space(./ows:Service/ows:ContactInformation/ows:ContactFacsimileTelephone)">

                <schema:telephone>
                  <xsl:value-of select="normalize-space(./ows:Service/ows:ContactInformation/ows:ContactFacsimileTelephone)"/>
                </schema:telephone>

         </xsl:if>

      <schema:email>
         <xsl:value-of select="normalize-space(./ows:Service/ows:ContactInformation/ows:ContactElectronicMailAddress)"/>
      </schema:email>

     <schema:contactPoint rdf:resource="https://www.epos-eu.org/epos-dcat-ap/Person/{$ContactPersonID}/contactPoint" />

   </schema:Person>

   <schema:ContactPoint rdf:about="https://www.epos-eu.org/epos-dcat-ap/Person/{$ContactPersonID}/contactPoint">
    <schema:email><xsl:value-of select="normalize-space(/ows:WMS_Capabilities/ows:Service/ows:ContactInformation/ows:ContactElectronicMailAddress)"/></schema:email>
    <schema:availableLanguage>en</schema:availableLanguage>
    <schema:contactType>manager</schema:contactType>
   </schema:ContactPoint>



</xsl:template>

</xsl:transform>