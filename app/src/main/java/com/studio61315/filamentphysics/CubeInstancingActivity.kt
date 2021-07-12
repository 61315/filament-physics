/*
 * CubeInstancingActivity.kt
 * Filament-Physics
 *
 * Created by Minseo Park on 1 JUL 2021.
 * Copyright (c) 2021 STUDIO 61315 co., ltd. All rights reserved.
 */

package com.studio61315.filamentphysics

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.*
import android.view.View
import com.bulletphysics.dynamics.RigidBody
import com.bulletphysics.linearmath.Transform
import com.google.android.filament.*
import com.google.android.filament.gltfio.*
import com.google.android.filament.utils.*
import java.nio.ByteBuffer
import javax.vecmath.Matrix4f

class CubeInstancingActivity : Activity() {

	companion object {
		init { Utils.init() }

		private const val TAG = "cube-instancing-demo"
		private const val INSTANCE_POOL_CAPACITY: Int = 100
		private const val CUBE_PER_SECOND: Int = 10
	}

	private lateinit var surfaceView: SurfaceView
	private lateinit var choreographer: Choreographer
	private val frameScheduler = FrameCallback()
	private lateinit var modelViewer: ModelViewer
	private lateinit var assetLoader: AssetLoader
	private lateinit var resourceLoader: ResourceLoader

	private val physicsController = CubeInstancingDemo()
	private var instanceRef = listOf<FilamentInstance>()
	private val rigidBodyRef = hashMapOf<Int, RigidBody>()
	private val tempMatrix4x4 = Mat4.identity().toFloatArray()
	private val tempTransform = Transform(Matrix4f(tempMatrix4x4))
	private var subsequentInstanceIndex: Int = 0

	@SuppressLint("ClickableViewAccessibility")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		// Hides the status bar and the navigation bar, in favor of using the entire screen.
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
			.or(WindowManager.LayoutParams.FLAG_FULLSCREEN)
			.or(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
			.or(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION))
		window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
		window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

		surfaceView = SurfaceView(this).apply { setContentView(this) }
		choreographer = Choreographer.getInstance()
		modelViewer = ModelViewer(surfaceView)

		surfaceView.setOnTouchListener { _, event ->
			modelViewer.onTouchEvent(event)
			true
		}

		// Creates engine parts that run out of the scope of the [ModelViewer] instance.
		assetLoader = AssetLoader(modelViewer.engine, MaterialProvider(modelViewer.engine), EntityManager.get())
		resourceLoader = ResourceLoader(modelViewer.engine, false, false)

		createCubeInstances()
		createIndirectLight()

		physicsController.initPhysics()
	}

	private fun createCubeInstances() {
		// Creates a pre-sized array to be filled with [FilamentInstance]s.
		val cubeInstances = arrayOfNulls<FilamentInstance>(INSTANCE_POOL_CAPACITY)

		// This transform component works as a root transform of the scene.
		val sceneRootTransform = modelViewer.engine.transformManager.create(EntityManager.get().create())

		readCompressedAsset("models/box.glb").let {
			// Creates a master asset and fills the instance array with instance objects.
			assetLoader.createInstancedAsset(it, cubeInstances)?.also { asset ->
				resourceLoader.loadResources(asset)

				// Makes instances concrete and set its hierarchy under root transform of the scene.
				instanceRef = cubeInstances.filterNotNull().onEach { instance ->
					modelViewer.engine.transformManager.setParent(instance.root, sceneRootTransform)
				}

				asset.releaseSourceData()
			}
		}

		// Adjusts the root transform of the scene to the position of interest, defaults to < 0, 0, -4 >.
		// Reasoning from ModelViewer.kt L374: private val kDefaultObjectPosition = Float3(0.0f, 0.0f, -4.0f)
		modelViewer.engine.transformManager.setTransform(sceneRootTransform,
			transpose(
			translation(Float3(0.0f, 1.5f, -4.0f)) *
				scale(Float3(0.15f))
			).toFloatArray()
		)
	}

	private fun addCubeInstanceToScene() {
		val instance = instanceRef[subsequentInstanceIndex]

		tempTransform.setIdentity()

		// We're using entity instance id of the root transform component as key for its perk of being unique.
		rigidBodyRef[instance.root]?.setWorldTransform(tempTransform) ?: run {
			rigidBodyRef[instance.root] = physicsController.instantiateCube(tempTransform)
			modelViewer.scene.addEntities(instance.entities)
		}

		subsequentInstanceIndex = (subsequentInstanceIndex + 1) % INSTANCE_POOL_CAPACITY
	}

	private fun createIndirectLight() {
		val engine = modelViewer.engine
		val scene = modelViewer.scene

		readCompressedAsset("envs/default_env_ibl.ktx").let {
			scene.indirectLight = KTXLoader.createIndirectLight(engine, it)
			scene.indirectLight!!.intensity = 30_000.0f
		}

		scene.skybox = Skybox.Builder().color(1.0f,1.0f,1.0f,1.0f).build(modelViewer.engine)
	}

	private fun readCompressedAsset(assetName: String): ByteBuffer {
		val input = assets.open(assetName)
		val bytes = ByteArray(input.available())
		input.read(bytes)

		return ByteBuffer.wrap(bytes)
	}

	override fun onResume() {
		super.onResume()
		choreographer.postFrameCallback(frameScheduler)
	}

	override fun onPause() {
		super.onPause()
		choreographer.removeFrameCallback(frameScheduler)
	}

	override fun onDestroy() {
		super.onDestroy()
		choreographer.removeFrameCallback(frameScheduler)
	}

	inner class FrameCallback : Choreographer.FrameCallback {
		private var frameNumber: Int = 0

		override fun doFrame(frameTimeNanos: Long) {
			choreographer.postFrameCallback(this)

			// Instantiates a new cube every N frame callbacks.
			if (frameNumber % (60 / CUBE_PER_SECOND) == 0)
				addCubeInstanceToScene()

			// Proceeds the physics engine by given amount of window.
			physicsController.updatePhysics()

			// Applies the transformation of the instantiated rigid bodies to the Filament instances'.
			modelViewer.engine.transformManager.openLocalTransformTransaction()

			instanceRef.forEach { instance ->
				rigidBodyRef[instance.root]?.getWorldTransform(tempTransform)?.getOpenGLMatrix(tempMatrix4x4)
				modelViewer.engine.transformManager.setTransform(instance.root, tempMatrix4x4)
			}

			modelViewer.engine.transformManager.commitLocalTransformTransaction()

			// Increments the frame counter.
			frameNumber++

			modelViewer.render(frameTimeNanos)
		}
	}
}