<img align="right" src="./assets/logo.webp" width="10%" alt="The logo for varpu: a doodle of a plant leaf">

# varpu
A simple yet powerful geometry library for Java.

## Usage
```java
// example.jsh
import static java.lang.Math.*;
import static blue.lhf.varpu.polyhedra.Box.*;
import static blue.lhf.varpu.vector.Ternion.*;
import static blue.lhf.varpu.vector.Quaternion.*;

// A 10-by-10-by-10 box with a 45-degree clockwise roll.
final Box box = boxAt(ORIGO, ternion(10, 10, 10), euler(0, 0, toRadians(-45)));
box = box.rotate(euler(toRadians(45), 0, 0));
```
