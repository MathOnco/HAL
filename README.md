
## Hybrid Agent Library: HAL

Hybrid Automata Library (HAL) is a Java library that facilitates hybrid modeling: spatial models with interacting agent-based and partial-differential equation components. HAL’s components can be broadly classified into: agent containers (on and off-lattice), finite difference diffusion fields, graphical user interface (GUI) components, and additional tools or utilities for computation and data manipulation. These components have a standardized interface that expedites the construction, analysis, and visualization of complex models.

HAL was originally developed to support mathematical oncology modeling efforts at the H. Lee Moffitt Cancer Center and Research Institute. To view several examples of projects built in HAL, since its inception in 2017, we direct the reader to the following website: [halloworld.org](http://halloworld.org/). More details on the philosophy and technical details behind HAL can be found in the preprint on [BioRxiv](https://www.biorxiv.org/content/early/2018/09/10/411538).

![What is Hybrid Modeling](https://github.com/torococo/AgentFramework/blob/master/manual/HAL_intro.png)

### What is hybrid modeling?
Hybrid Modeling is the integration of Agent-Based modeling and partial differential equation (PDE) modeling. It is commonly used in mathematical oncology to mechanistically model interactions between microen- vironmental diffusibles (e.g drugs or resources) and agents (tumor cells). Tissue is represented using agent-based modeling, where each agent acts as a single cell in two- or three-dimensional space. As seen in figure 1, agents may be stackable, unstackable, off-lattice, on-lattice, and two- or three-dimensional types. Agents are contained in grids. A single model may have multiple overlapping and interacting grids, representing moving and interacting cells, alongside diffusing drug and resources. Diffusibles that interact with the tissue are represented using partial differential equations (PDEs).

### Modularity
Each component (grids, agents) of HAL can function independently. This permits any combination of components to be used in a single model, with the use of spatial queries to combine them.

### Extensibility
HAL was designed to allow models and components to be extended and modified. Grids and agents from published models can be used as as a scaffold on which to do additional studies while keeping the prior work and their additions separated.

### Simplicity
Components are simple and generic making them applicable to a wide variety of modeling problems outside of mathematical oncology. A defensive programming paradigm was used to generate useful error messages when a component is used incorrectly. The purpose of this manual is to explain the modeling paradigm behind HAL, where the clear, consistent interface and methodology allows for ease of learning and implementation.

### Performance
HAL prioritizes performance in its algorithmic implementation. HAL includes efficient PDE solving algorithms, efficient visualization using BufferedImages and OpenGL, and leverages Java’s impressive performance for exe- cuting ABM logic. These performance considerations allow for real-time display and visualization of models with minimal lag.

## Before you start
In order to run models built using HAL's' code base, you'll need to download the latest version of [Java](http://www.oracle.com/technetwork/java/javase/downloads/jdk9-downloads-3848520.html) and an editor (we suggest using [IntelliJ Idea](https://www.jetbrains.com/idea/download/)).

### Setting up the project in IntelliJ Idea

1. Download or clone HAL.
2. Open Intellij Idea
(a) click "Import Project" from the welcome window. (If the main editor window opens, Navigate to the File menu and click New -> "Project from Existing Sources")
(b) Navigate to the directory with the unzipped HAL Source code ("Hal-master"). Click "Open." Inside this folder will be the following folders: Examples, LEARN_HERE, HAL, Testing, and the manual.pdf.
3. Intellij will now ask a series of questions/prompts. The first prompt will be "Import Project," and you will select the bubble that indicates "Create project from existing sources" and then click "Next."
4. The next prompt is to indicate which directory contains the existing sources. Navigate to the HAL-master folder and leave the project name as "HAL-master." Click Next.
5. Intellij may alert you that it has found several source files automatically. Leave the box checked and click Next.
6. Intellij should have imported two Libraries: 1) lib and 2) HalColorSchemes. If these are not found, you"ll need complete the optional step 10 after setup is complete.
7. Intellij will prompt you to review the suggested module structure. This should state the path to the "HAL- master" directory. Click next.
8. Intellij will ask you to select the Java JDK. Click the "+" and add the following files:
(a) Mac: navigate to "/Library/ Java/ JavaVirtualMachines/" (b) Windows: navigate to "C:\ Program Files\ Java\"
(c) Choose a JDK version 1.8 or later
9. Intellij will state "No frameworks detected." Click Finish.
10. If step 6 failed, you will need to do one more step and add libraries for 2D and 3D OpenGL visualization:
(a) Navigate to the File menu and click "Project Structure"
(b) Click the "Libraries" tab
(c) Use the minus button (-) to remove any pre-existing library entries
(d) Click the "+" button, then click "Java" and direct the file browser to the "HAL-master/HAL/lib" folder. (e) Click apply or OK
