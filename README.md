# generate-speedment-sources
A command-line program that generates repeatable classes and interfaces for the Speedment Open Source project.

## Purpose
This project is only intended to be used by developers of the Speedment Open Source project. If you only wish to to use project as it is, you should not need to use this at all. If you however find a bug or improvement that you want to apply to a generated file in the Speedment repository, please add it to this project so that it is included in any regeneration that is done in the future.

## Usage
To regenerate source files used by the Speedment project, run the program from the command line with the base directory of the Speedment repository as a command-line parameter like this:
```
java -jar generate-speedment-sources.jar C:/Users/Emil/Documents/GitHub/speedment
```
This will generate all the specific types required to handle primitive fields.

## License
This project is licensed under the Apache 2 license.

A complete license file is located in the repository.
