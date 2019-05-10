package com.financius.features.debug

import com.financius.CoroutinesTestRule
import com.financius.channel
import com.financius.data.LogoutService
import com.financius.features.debug.DebugPresenter.DebugItem
import com.financius.features.debug.DebugPresenter.DebugItem.Action
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class DebugPresenterTest {

    @JvmField
    @Rule
    val rule = CoroutinesTestRule()

    private val debugItemSelectsChannel = channel<DebugItem>()

    private val logoutService = mockk<LogoutService>()
    private val presenter = DebugPresenter(logoutService)
    private val view = mockk<DebugPresenter.View>(relaxed = true)

    @Test
    fun `shows debug items`() {
        presenter attach view

        verify { view.showDebugItems(any()) }
    }

    @Test
    fun `executes action when action debug item is selected`() {
        val action = mockk<Action>()
        presenter attach view

        select(action)

        coVerify { action.execute() }
    }

    private fun select(debugItem: DebugItem) = debugItemSelectsChannel.offer(debugItem)
}