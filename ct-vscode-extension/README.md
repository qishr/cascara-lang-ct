# CT Language Support for Visual Studio Code

Syntax highlighting for the CT color transformation language.

The CT DSL is a domain-specific language designed for authoring, transforming, and compiling color schemes across arbitrary target formats.

![](https://sandydunlop.github.io/assets/images/ct-example.png)

## Showcase

For a fully working example, see [cascara-retro-theme's source code](https://github.com/sandydunlop/cascara-retro-theme). The cascara-retro-theme project uses CT to produce themes for **Visual Studio Code**, **Ghostty**, and **iTerm2** from a single source set. Additionally, [VSIX Gradle Plugin](https://qishr.github.io/cascara/file-formats/vsix/gradle-plugin/) is used by cascara-retro-theme to create the VSIX pacakge for its Visual Studio Code theme.

### Theme Screenshots

**Multiple app themes from one source set:**

![](https://sandydunlop.github.io/assets/images/ct-example-apps.png)

**Multiple Visual Studio Code theme variations from one source set**:

![](https://sandydunlop.github.io/assets/vscode-retro-preview.png)

## CT Features

**Dynamic Color Transformations**

Programmatic color manipulation including HSV/HSBA lerping, brightness/saturation scaling, and RGBA blending.

**Static Function Imports**

Import static methods from custom Java classes to use as transform functions in CT.

**Resource and File Imports**

Module structures using standard file paths and embedded resource schemes like `res://ghostty.ct` via cascara-common-io.

**Format-Agnostic Outputs**

Target definition parsing to emit theme configurations for VS Code, Ghostty, iTerm2, and other application formats from a single source pipeline.

## See Also

- [CT Language Reference](https://qishr.github.io/cascara/language-processors/ct/language-reference/)
- [CT on Hithub](https://github.com/qishr/cascara-lang-ct)
- [CT Gradle Plugin](https://qishr.github.io/cascara/language-processors/ct/gradle-plugin/)
- [VSIX Gradle Plugin](https://qishr.github.io/cascara/file-formats/vsix/gradle-plugin/)

## Installation

1. Open **Visual Studio Code**
2. Launch the Extension Marketplace (`Ctrl+Shift+X` or `Cmd+Shift+X`)
3. Search for `Cascara CT Language`
4. Click **Install**

## License

CT Language Support is released under the MIT license, which grants the following permissions:

- Commercial use
- Distribution
- Modification
- Private use

For more convoluted language, see the LICENSE.md
