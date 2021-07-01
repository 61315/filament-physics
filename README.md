# filament-physics

Physics simulation demo using **Filament** rendering engine and **Bullet Physics SDK**.

![filament-physics-cover](https://user-images.githubusercontent.com/46559594/124070508-e4884a80-da78-11eb-9304-cd680fc9b41a.png)

## Preview

Cube Instancing | Constraints
:-: | :-:
![preview](https://user-images.githubusercontent.com/46559594/124067157-dcc6a700-da74-11eb-8b40-715902b62f79.gif) | 
`CubeInstancingActivity.kt` | `ConstraintsActivity.kt`

## What is Filament?

[Filament](https://github.com/google/filament) is a real-time physically based rendering engine for Android, iOS, Linux, macOS, Windows, and WebGL. It is designed to be as small as possible and as efficient as possible on Android.

## What is Bullet?

[Bullet](https://github.com/bulletphysics/bullet3) is a physics SDK for real-time collision detection and multi-physics simulation for VR, games, visual effects, robotics, machine learning etc.

## Demo

### Cube Istancing

This demo implements rudimentary instancing and rigid body simulations. ~~Simply put😇,~~ this demo loads a single glTF asset and creates copies of it, each attached with a [rigid body](https://knowledge.autodesk.com/support/maya/learn-explore/caas/CloudHelp/cloudhelp/2019/ENU/Maya-SimulationEffects/files/GUID-F8C301AE-B746-4522-806F-27F2BC91C8B3-htm.html) from the physics engine that shares the transformation data of the rendering engine. The `CubeInstancingActivity` creates a `SurfaceView` and prepairs the rendering engine. The activity harnesses the [`ModelViewer`](https://github.com/google/filament/blob/b841709729d900897d1878140ef0393f263d70bf/android/filament-utils-android/src/main/java/com/google/android/filament/utils/ModelViewer.kt) class to pipe down the boilerplating.

The `CubeInstancingDemo` class initializes the physics world and performs the simulation. You can consult the [manual](http://www.cs.kent.edu/~ruttan/GameEngines/lectures/Bullet_User_Manual) for the details of the initialization phase. I have tried to keep it unopinionated as possible, it includes neither classes for node management nor interfaces that communicate with the physics engine, just so I have kept it simple.

You can tinker with the following constants in the `CubeInstanceActivity.kt`:
```kotlin
// The number of concurrent rigid bodies
private const val INSTANCE_POOL_CAPACITY: Int = 100

// Feed rate
private const val CUBE_PER_SECOND: Int = 10
```
- [Instancing in filament engine](https://github.com/google/filament/pull/2607)
- [JBullet HelloWorld](https://github.com/bubblecloud/jbullet/blob/00b51569461391d87436369ac723e875efef0dcd/src/test/java/com/bulletphysics/demos/helloworld/HelloWorld.java)
- [glTF-Sample-Models/2.0/Box](https://github.com/KhronosGroup/glTF-Sample-Models/tree/master/2.0/Box)

## References

- [Bullet](https://github.com/bulletphysics/bullet3)
    - [Bullet on Wikipedia](https://en.wikipedia.org/wiki/Bullet_(software))
    - [Bullet User Manual](http://www.cs.kent.edu/~ruttan/GameEngines/lectures/Bullet_User_Manual)
- [JBullet](http://jbullet.advel.cz)
    - Java port of Bullet Physics
- [jbullet-jme](https://code.google.com/archive/p/jbullet-jme/)
    - jMonkeyEngine integration of JBullet
- [affogato/JBullet-QIntBio-Fork](https://github.com/affogato/JBullet-QIntBio-Fork)
    - A fork of JBullet
- [davidB/jmbullet](https://github.com/davidB/jmbullet)
    - A fork of JBullet
- [bubblecloud/jbullet](https://github.com/bubblecloud/jbullet) ★
    - A fork of JBullet, **used in this project**
- [kotlin-graphics/bullet](https://github.com/kotlin-graphics/bullet)
    - Kotlin port of Bullet Physics
- [stephengold/Libbulletjme](https://github.com/stephengold/Libbulletjme)
    - JNI implementation of Bullet Physics
- [Many more...](https://github.com/search?p=1&q=jbullet&type=Repositories)

Most of the reasoning was from the links below. One may find them useful.

- [JStack or not JStack](https://jvm-gaming.org/t/jstackalloc-stack-allocation-of-value-objects-in-java/31983)
- [The versions of the JBullet](https://hub.jmonkeyengine.org/t/jbullet-version/30546/7)
- [Sceneform AR physics experiment](https://csaba.page/blog/sceneform-ar-physics.html)
- [ARCore integration](https://codelabs.developers.google.com/codelabs/augimg-intro/index.html#4)
    - Google codelabs demo
- [CsabaConsulting/ARPhysics](https://github.com/CsabaConsulting/ARPhysics)
    - Sceneform integration
- [mahmoudgalal/SceneForm-Bullet](https://github.com/mahmoudgalal/SceneForm-Bullet)
    - Introductory JNI wrapper for Bullet Physics and Sceneform integration

## License

**filament-physics** is available under the MIT license. See the [LICENSE](LICENSE) file for more info.
