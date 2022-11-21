# SHAPEness Metadata Editor

The SHAPEness Metadata Editor is a Java desktop application conceived to help users creating and updating RDF metadata descriptions. It provides a rich user interface which allows users to easy populate and validate metadata, structured as graphs, against a set of SHACL constraints.
The SHAPEness Metadata Editor has been developed in the framework of the European Plate Observing System (EPOS) where an extension of DCAT-AP, called EPOS-DCAT-AP, was created.
As it is a SHACL-driven Metadata Editor, it is suitable for all kinds of domains or use cases which structure their knowledge by means of SHACL constraints.


# Installation
Starting from an existing eclipse installation, you first have to install Xtext, which is a dependency for Xturtle, from this update site:
http://download.eclipse.org/modeling/tmf/xtext/updates/composite/releases/

After that, install Xturtle from this update site:
http://xturtle.aksw.org/site.xml

During the installation, you will get a warning that the package is unsigned.

Install new software from eclipse: Zest SDK + GEF Zest SDK

Install Nebula plugin from http://download.eclipse.org/nebula/snapshot

Multi-platform builds:

- Open Window/Preferences.
- Find PDE/Target Platform
- Select your (active) target platform
- Click Edit
- Click Add
- Select "Software Site"
- Click Next

In "Work With" type: http://download.eclipse.org/eclipse/updates/4.15 (replace 4.15 with your current version)
- Check "Eclipse RCP Target Components"
- Check "Equinox Target Components"
- Uncheck "Include required software"
- Check "Include all environments"
- Press Finish
- Press Finish
- Press Apply
- Open your product file and select the "Export" option. You will see that the "Export for multiple platforms" checkbox is available.

