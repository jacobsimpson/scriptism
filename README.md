# Scriptism

My hobby scripting language. A marriage of static typing with accommodating syntax.

## How to Build
1. Clone this project.
2. Download Gradle and install it.
3. Build the project and create a consolidate jar that is the language
   interpreter:

    gradle

4. Run a sample script file

    java -jar build//scriptism.jar scripts/hello-world.tsm

or

    build/scriptism scripts/hello-world.tsm

or

    scripts/hello-world.tsm

