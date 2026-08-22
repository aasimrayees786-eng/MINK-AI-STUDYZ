package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Mink Study", appName)
  }

  @Test
  fun `default robot lessons contains human skeleton and vital organs`() {
    val defaultLessons = com.example.data.model.DefaultRobotLessons.lessons
    org.junit.Assert.assertTrue("Should have default lessons", defaultLessons.isNotEmpty())
    val skeletonLesson = defaultLessons.firstOrNull { it.id == "lesson_skeleton_206" }
    org.junit.Assert.assertNotNull("Should contain 206 bones lesson", skeletonLesson)
    org.junit.Assert.assertEquals("Human Skeleton & The 206 Bones", skeletonLesson?.title)
    
    val boneCue = skeletonLesson?.cues?.firstOrNull { it.keyword.equals("bones", ignoreCase = true) }
    org.junit.Assert.assertNotNull("Should have bones cue", boneCue)
    org.junit.Assert.assertEquals(
        com.example.data.model.HologramVisualType.BONES_SKELETON,
        boneCue?.visualType
    )
  }
}
