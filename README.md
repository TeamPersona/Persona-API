Persona API
===========

[![Build Status](https://magnum.travis-ci.com/taylorstark/uwpib.svg?token=HkaqTJ9DFwkKyNBNQpU5&branch=master)](https://magnum.travis-ci.com/taylorstark/uwpib)

FYDP - Personal Information Banking

Setting Up
----------

### IntelliJ Idea
1. Open up IntelliJ.  Click on "Import Project"
2. Navigate to the "Persona-API" root folder
3. Select "Import from existing sources" and check "SBT"
    * You will have the option to download source and documentation.  I'd recommend it, but it's up to you
4. Wait an eternity for IntelliJ to pull in all dependencies
5. To have IntelliJ run/debug Play
    * Go to Run -> Edit Configurations
    * Click the '+' button.  Select SBT Task
    * Name the configuration "Play"
    * In "tasks" type "run"
    * In "Before launch" remove "Make"