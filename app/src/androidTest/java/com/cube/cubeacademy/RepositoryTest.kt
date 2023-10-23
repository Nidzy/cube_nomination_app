package com.cube.cubeacademy

import com.cube.cubeacademy.lib.di.Repository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class RepositoryTest {
	@get:Rule
	var hiltRule = HiltAndroidRule(this)

	@Inject
	lateinit var repository: Repository

	@Before
	fun setUp() {
		hiltRule.inject()
	}

	@Test
	fun getNominationsTest() {
		// TODO: Write a test for getting all the nominations from the mock api
	}

	@Test
	fun getNomineesTest() {
		// TODO: Write a test for getting all the nominees from the mock api
	}

	@Test
	fun createNominationTest() {
		// TODO: Write a test for creating a new nomination using the mock api
	}
}