# NotesAdress2SQLite

## Description

This java application takes an XMLTrackFile (GPX Format) and converts it into an route file (GPX Format as well).
THis version does not strip any track points to shorten the amount of route points.

## Ant Build

On project level execute:
	ant -buildfile build/build.xml run
	

## Usage

java -classpath trk2rte.jar de.jjprojects.trk2rte -Djava.util.logging.config.file=trk2rte_logging.properties <XmlTrackFile>  <XmlRouteFile>


## Author

Joerg Juenger ( jjuenger, joerg@jj-projects.de ), JJ-Projects Joerg Juenger

