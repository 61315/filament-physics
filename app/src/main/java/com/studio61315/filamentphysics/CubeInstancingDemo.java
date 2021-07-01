/*
 * CubeInstancingDemo.java
 * Filament-Physics
 *
 * Created by Minseo Park on 1 JUL 2021.
 * Copyright (c) 2021 STUDIO 61315 co., ltd. All rights reserved.
 */

package com.studio61315.filamentphysics;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import javax.vecmath.Vector3f;

import static com.bulletphysics.collision.dispatch.CollisionObject.DISABLE_DEACTIVATION;

public class CubeInstancingDemo {
    final static float CUBE_HALF_EXTENTS = 0.5f;
    private DynamicsWorld dynamicsWorld;
    private CollisionShape boxShape;

    private void setupEmptyDynamicsWorld() {
        DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        BroadphaseInterface overlappingPairCache = new DbvtBroadphase();
        ConstraintSolver constraintSolver = new SequentialImpulseConstraintSolver();
        dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, constraintSolver, collisionConfiguration);
    }

    public void initPhysics() {
        setupEmptyDynamicsWorld();

        CollisionShape groundShape = new StaticPlaneShape(new Vector3f(0, 1, 0), 40);

        Transform groundTransform = new Transform();
        groundTransform.setIdentity();
        groundTransform.origin.set(new Vector3f(0, -60, 0));

        RigidBody groundBody = createRigidBody(0, groundTransform, groundShape);

        boxShape = new BoxShape(new Vector3f(CUBE_HALF_EXTENTS, CUBE_HALF_EXTENTS, CUBE_HALF_EXTENTS));
    }

    public RigidBody instantiateCube(Transform transform) {
        RigidBody body = createRigidBody(1.0f, transform, boxShape);
        body.setDamping(0.0f, 0.5f);
        body.setFriction(1.0f);
        body.setActivationState(DISABLE_DEACTIVATION);

        return body;
    }

    private RigidBody createRigidBody(float mass, Transform startTransform, CollisionShape shape) {
        boolean isDynamic = (mass != 0f);

        Vector3f localInertia = new Vector3f(0f, 0f, 0f);

        if (isDynamic)
            shape.calculateLocalInertia(mass, localInertia);

        DefaultMotionState motionState = new DefaultMotionState(startTransform);
        RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(mass, motionState, shape, localInertia);

        RigidBody body = new RigidBody(constructionInfo);
        dynamicsWorld.addRigidBody(body);

        return body;
    }

    public void updatePhysics() {
        if (dynamicsWorld != null)
            dynamicsWorld.stepSimulation(1f / 20f, 0, 1f / 60f);
    }
}
