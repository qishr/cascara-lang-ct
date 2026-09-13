# Color Transformation Language

*Pairing the science of color with the art of code*

## CT Module

The CT DSLis a domain-specific language designed for authoring, transforming, and compiling color schemes across arbitrary target formats. It supports:

* **Dynamic Color Transformations**

  Programmatic color manipulation including HSV/HSBA lerping, brightness/saturation scaling, and RGBA blending.

* **Static Function Imports**

  Import static methods from custom Java classes to use as transform functions in CT.

* **Resource and File Imports**

  Module structures using standard file paths and embedded resource schemes like `res://ghostty.ct` via `cascara-common-io`.

* **Format-Agnostic Outputs**

  Target definition parsing to emit theme configurations for VS Code, Ghostty, iTerm2, and other application formats from a single source pipeline.

## CT Gradle Plugin

The CT Gradle plugin (`io.github.qishr.cascara-gradle-plugins.ct`) provides first-class support for compiling `.ct` files as part of standard Gradle build routines.

### Configuration Example

Configure formats and themes declaratively in `build.gradle`:

```groovy
plugins {
    id 'io.github.qishr.cascara-gradle-plugins.ct' version '0.1.0'
}

ct {
    // Optional source directory configuration (defaults to src/main/ct)
    // sourceDir = layout.projectDirectory.dir("src/main/ct")

    formats {
        vscode {
            retroAmberOnBrightBeige { entry = "retro-amber-on-bright-beige.ct" }
            retroAmberOnDullBeige   { entry = "retro-amber-on-dull-beige.ct" }
        }
        ghostty {
            retroAmber { entry = "retro-amber-on-bright-beige.ct" }
            retroGreen { entry = "retro-green-on-bright-beige.ct" }
        }
    }
}
```

The plugin dynamically generates task providers for each target format and theme (e.g., compileCtVscodeRetroAmberOnBrightBeige) and binds them to the aggregate compilation lifecycle.

## Gradle Commands

Build and install locally:

```bash
./gradlew build nativeCompile publishToMavenLocal
```

Run CLI runner directly with -V (version):

```bash
./gradlew :ct-command:run
```

Compile all configured CT themes across formats:

```bash
./gradlew compileAllCt
```

Build the entire project including theme compilation:

```bash
./gradlew build
```

## License

[MIT](https://github.com/qishr/cascara-lang-ct/blob/main/LICENSE)

