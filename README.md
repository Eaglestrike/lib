# Team 144 Library

Team 114's standard utility library.

## Setup Development Environment (MacOS)

Use [this guide](http://wat.sinnpi.com/dl/FRC%20Getting%20Started%20-%20IntelliJ%20IDEA.pdf), it's
more comprehensive. Or refer to this shorter set of instructions, up to you.

### Clone repository
In your shell, run `git clone https://github.com/Eaglestrike/Javalotl/`. `cd` into the directory.

### Bootstrap `Gradle`
In your shell, run `./gradlew`. It will automatically install any missing dependencies.

### Build
Run `./gradlew build` to make sure everything compiles fine. If all is well, you're done!

## Setup and Integrate IntelliJ IDEA

### Download IntelliJ IDEA
Go ahead to the [download page on IntelliJ's website](https://www.jetbrains.com/idea/download)
and select the Community Edition. If you apply for their
[education package](https://www.jetbrains.com/student/), you can download the Ultimate edition
(but you won't really ever use any of the extra features).

### Generate IDEA project
In the project directory, run `./gradlew idea`. It will generate several files...

Go ahead and open `Javalotl.ipr` with IntelliJ IDEA. This will open a blank screen, and hopefully
a popup in the bottom right-hand corner will appear asking to link your Gradle project.

On the popup, `Import Gradle Project`. Select *only* `Use auto-import` and
`Use gradle wrapper task configuration`. Press OK.

In the toolbar, click `View`->`Tool Windows`->`Gradle`. This will open a Gradle tool window on the
right-hand side of the screen. This is where you can run tasks, etc.

Find and run the `build` task. If it runs successfully, everything is set up correctly!
Congratulations.

## Project style

We attempt to follow the
[Google Java Style guide](https://google.github.io/styleguide/javaguide.html),
except that:

1. We use 4 spaces for indents, in contravention of section
[4.2](https://google.github.io/styleguide/javaguide.html).

## Copyright

Copyright (C) 2017 Team 114 and Contributors

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published
by the Free Software Foundation, either version 3 of the License,
or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
