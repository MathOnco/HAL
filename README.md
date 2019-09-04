
## Hybrid Agent Library: HAL

Hybrid Automata Library (HAL) is a Java library that facilitates hybrid modeling: spatial models with interacting agent-based and partial-differential equation components. HAL’s components can be broadly classified into: agent containers (on and off-lattice), finite difference diffusion fields, graphical user interface (GUI) components, and additional tools or utilities for computation and data manipulation. These components have a standardized interface that expedites the construction, analysis, and visualization of complex models.

HAL was originally developed to support mathematical oncology modeling efforts at the H. Lee Moffitt Cancer Center and Research Institute. To view several examples of projects built in HAL, since its inception in 2017, we direct the reader to the following website: [halloworld.org](http://halloworld.org/). More details on the philosophy and technical details behind HAL can be found in the preprint on [BioRxiv](https://www.biorxiv.org/content/early/2018/09/10/411538).

![What is Hybrid Modeling](manual/HAL_intro.png)

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

1. Open Intellij Idea and click "create project from existing sources" ("file/ new/ project from existing sources" from the main GUI) and direct it to the unzipped AgentFramework Source code directory.
2. Continue through the rest of the setup, click next until it asks for the Java SDK:
- "/Library/ Java/ JavaVirtualMachines/" on Mac.
- "C:\ Program Files\ Java\" on Windows.
3. Once the setup is complete we will need to do one more step and add some libraries that allow for 2D and 3D OpenGL visualization:
4. open the Intellij IDEA main gui
5. go to "file/ project structure"
6. click the "libraries" tab
7. use the minus button to remove any pre-existing library setup
8. click the plus button, and direct the file browser to the "HAL/ lib" folder.
9. click apply or ok
