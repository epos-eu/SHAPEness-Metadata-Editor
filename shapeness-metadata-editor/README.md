# Intelligent Metadata Editor

The editor includes a graphic user interface to ease the creation of files also to those users who are not experts nor familiar with the DCAT-AP model, and an export function to ease the production of RDF/turtle files, which are those eventually ingested by DCAT-AP metadata systems.
The tool will include DCAT-AP schema import with SHACL constraints description.
The tool will also enable users to import previously edited files in DCAT-AP (upon uploading the SHACL schema), which will be validated against the SHACL.
The ambition is twofold: first, to provide users with a novel, useful metadata editor, filling the existing need for such a tool; second, make this project available on GitHub so to involve the developers community and enhance quality, features and performance of the tool.

### Tech

### Installation

Starting from an existing eclipse installation, you first have to install Xtext, which is a dependency for Xturtle, from this update site:
http://download.eclipse.org/modeling/tmf/xtext/updates/composite/releases/

After that, install Xturtle from this update site:
http://xturtle.aksw.org/site.xml
During the installation, you will get a warning that the package is unsigned.

Install new software from eclipse: Zest SDK + GEF Zest SDK

Install Nebula plugin from http://download.eclipse.org/nebula/snapshot

Multi-platform builds:
Open Window/Preferences.
Find PDE/Target Platform
Select your (active) target platform
Click Edit
Click Add
Select "Software Site"
Click Next
In "Work With" type: http://download.eclipse.org/eclipse/updates/4.15 (replace 4.15 with your current version)
Check "Eclipse RCP Target Components"
Check "Equinox Target Components"
Uncheck "Include required software"
Check "Include all environments"
Press Finish
Press Finish
Press Apply
Open your product file and select the "Export" option. You will see that the "Export for multiple platforms" checkbox is available.


### Plugins

### Development

#### Building for source

### Docker

#### Kubernetes + Google Cloud

### Todos
