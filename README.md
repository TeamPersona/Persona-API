Persona API
===========

[![Build Status](https://travis-ci.org/TeamPersona/Persona-API.svg?branch=master)](https://travis-ci.org/TeamPersona/Persona-API)
[![Coverage Status](https://coveralls.io/repos/TeamPersona/Persona-API/badge.svg?branch=master&service=github)](https://coveralls.io/github/TeamPersona/Persona-API?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/grade/e35c5c4b09744971af5f39678977b063)](https://www.codacy.com/app/taylor-stark03/Persona-API)

FYDP - Personal Information Banking

Setting Up
---------

### Dependencies
1. Java 8
2. SBT (install using: brew install sbt)

### IntelliJ
1. Open up IntelliJ.  
2. Ensure that the IntelliJ Scala plugin is installed
3. Click on "Import Project"
4. Navigate to the "Persona-API" root folder
5. Select "Import from existing sources" and choose "SBT"
    * I'd recommend selecting "Use auto-import" as well as download source and documentation

### Postgres
1. brew install postgresql
2. postgres -D /usr/local/var/postgres
3. createdb persona

### Cassandra
1. scripts/startCassandra.sh
    * This will download Cassandra to ~/Downloads
2. scripts/setupCassandra.sh
