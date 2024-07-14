package com.remote.ghibli.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UtilsTest {
    @Test
    fun `getIdFromUrl should return id when url points to a person`() {
        val result = getIdFromUrl("https://ghibliapi.vercel.app/people/id")
        assertEquals(result, "id")
    }

    @Test
    fun `getIdFromUrl should return empty string when url points to all people`() {
        val result = getIdFromUrl("https://ghibliapi.vercel.app/people/")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `urlPointsToAllEntities should return true`() {
        val peopleUrls = listOf("https://ghibliapi.vercel.app/people/")
        val result = urlPointsToAllEntities(peopleUrls)
        assertTrue(result)
    }

    @Test
    fun `urlPointsToAllEntities should return false`() {
        val peopleUrls = listOf(
            "https://ghibliapi.vercel.app/people/id1",
            "https://ghibliapi.vercel.app/people/id2",
        )
        val result = urlPointsToAllEntities(peopleUrls)
        assertFalse(result)
    }
}
