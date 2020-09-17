Create PERFORMANCE SCENARIOS 
=========================
Prerequisites
- Installed Maven
- Installed Scala plugin

Setup:
Execute following Maven command:

$mvn archetype:generate

“Choose a number or apply filter”: “gatling”
“Choose a number or apply filter” (again): “1”
Press enter for all 

Then specify Maven project names (packages etc)

Usage:
Execute Performance Scenario script
    $mvn gatling:test -Dgatling.simulationClass=<package>.<name>
