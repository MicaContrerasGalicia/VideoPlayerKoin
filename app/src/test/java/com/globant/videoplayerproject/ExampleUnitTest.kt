package com.globant.videoplayerproject

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.globant.videoplayerproject.di.*
import com.globant.videoplayerproject.model.Data
import com.globant.videoplayerproject.ui.topGames.TopGamesViewModel
import com.globant.videoplayerproject.utils.Utils
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Test

import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

@ExperimentalCoroutinesApi
class TestCoroutineRule : TestRule {

    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)

    override fun apply(base: Statement, description: Description?) = object : Statement() {
        @Throws(Throwable::class)
        override fun evaluate() {
            Dispatchers.setMain(testCoroutineDispatcher)

            base.evaluate()

            Dispatchers.resetMain()
            testCoroutineScope.cleanupTestCoroutines()
        }
    }

    fun runBlockingTest(block: suspend TestCoroutineScope.() -> Unit) =
        testCoroutineScope.runBlockingTest { block() }

}

class TopGamesUnitTest : KoinTest {
    private val topGamesViewModel: TopGamesViewModel by inject()

    private val testDispatcher = TestCoroutineDispatcher()

    @get:Rule
    val testInstantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Before
    fun before() {
        startKoin {
            modules(
                listOf(
                    viewModel,
                    repositoryApiService,
                    repositoryApiServiceToken,
                    repositoryApiStreamUrlVideo,
                    repositoryApi,
                    retrofitModule
                )
            )
        }
    }

    @After
    fun after() {
        stopKoin()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun testGetTopGames() {
        testCoroutineRule.runBlockingTest {
            val listGames: MutableList<Data> = ArrayList()
            topGamesViewModel.accessToken.observeForever {
                topGamesViewModel.getListGames(Utils().adaptTypeToken(it.token_type) +" "+ it.access_token)
            }
            topGamesViewModel.listGames.observeForever {
                listGames.addAll(it)
            }
            Thread.sleep(60000)
            println(listGames)
            assert(listGames.isNotEmpty())
        }
    }
}