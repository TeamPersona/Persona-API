Persona API
===========

[![Build Status](https://travis-ci.org/TeamPersona/Persona-API.svg?branch=master)](https://travis-ci.org/TeamPersona/Persona-API)
[![Coverage Status](https://coveralls.io/repos/TeamPersona/Persona-API/badge.svg?branch=master&service=github)](https://coveralls.io/github/TeamPersona/Persona-API?branch=master)

FYDP - Personal Information Banking

Setting Up
----------

### IntelliJ Idea
1. Open up IntelliJ.  Click on "Import Project"
2. Navigate to the "Persona-API" root folder
3. Select "Import from existing sources" and choose "SBT".  You will need the following:
    * IntelliJ Scala plugin
    * SBT (brew install sbt)
    * Java 8
4. You will have the option to download source and documentation.  I'd recommend it, but it's up to you
5. Wait an eternity for IntelliJ to pull in all dependencies
6. To have IntelliJ run/debug Play
    * Go to Run -> Edit Configurations
    * Click the '+' button.  Select SBT Task
    * Name the configuration "Play"
    * In "tasks" type "run"
    * In "Before launch" remove "Make"