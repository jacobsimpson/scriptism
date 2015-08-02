# Scriptism

My hobby scripting language. A marriage of static typing with accommodating
syntax.

In order to make it easy to experiment with syntax, the compilation steps are
actually just transpiling into Java, analogous to how CoffeeScript is transpiled
into JavaScript. Once the Java source is generated, it is compiled using the JDK
JavaCompiler API to compile the generated Java source code into byte code. The
byte code is dynamically loaded for execution. All these steps are executed in
memory, so the user experience to just to pass a script file to the interpreter
and see it executed.

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

