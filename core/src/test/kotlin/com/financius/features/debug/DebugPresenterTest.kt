package com.financius.features.debug

import com.financius.CoroutinesTestRule
import com.financius.channel
import com.financius.data.LogoutService
import com.financius.features.debug.DebugPresenter.DebugItem
import com.financius.features.debug.DebugPresenter.DebugItem.Action
import com.financius.features.debug.DebugPresenter.DebugItem.Action.Logout
import com.financius.features.debug.DebugPresenter.Intent.Select
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DebugPresenterTest {

    @JvmField
    @Rule
    val rule = CoroutinesTestRule()

    private val debugItemSelectsChannel = channel<Select>()

    private val logoutService = mockk<LogoutService>(relaxed = true)
    private val presenter = DebugPresenter(logoutService)
    private val view = mockk<DebugPresenter.View>(relaxed = true)

    @Before
    fun setUp() {
        every { view.debugItemSelects() } returns debugItemSelectsChannel.openSubscription()
    }

    @Test
    fun `shows debug items`() {
        presenter attach view

        verify { view.showDebugItems(any()) }
    }

    @Test
    fun `executes action when action debug item is selected`() {
        val action = mockk<Action>(relaxed = true)
        presenter attach view

        select(action)

        coVerify { action.execute() }
    }

    @Test
    fun `restarts app after log out`() {
        val action = Logout(logoutService)
        presenter attach view

        select(action)

        verify { view.restartApp() }
    }

    private fun select(debugItem: DebugItem) = debugItemSelectsChannel.offer(Select(debugItem))
}