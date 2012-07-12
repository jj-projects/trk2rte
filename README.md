# trk2rte

## Description

This java application takes an XMLTrackFile (GPX Format) and converts it into an route file (GPX Format as well).
THis version does not strip any track points to shorten the amount of route points.

## Ant Build

On project level execute:
	ant -buildfile build/build.xml run
	

## Usage

java -classpath trk2rte.jar de.jjprojects.trk2rte -Djava.util.logging.config.file=trk2rte_logging.properties <XmlTrackFile>  <XmlRouteFile>


## Author

Joerg Juenger ( jj-projects, joerg@jj-projects.de ), JJ-Projects Joerg Juenger

## Licenses

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
