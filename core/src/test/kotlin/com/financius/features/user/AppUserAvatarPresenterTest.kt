package com.financius.features.user

import com.financius.CoroutinesTestRule
import com.financius.aRandom
import com.financius.channel
import com.financius.data.AppUserDataSource
import com.financius.deferred
import com.financius.features.user.AppUserAvatarPresenter.Intent.Select
import com.financius.models.AppUser
import com.financius.never
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AppUserAvatarPresenterTest {

    @Rule
    @JvmField
    val rule = CoroutinesTestRule()

    private val selectsChannel = channel<Select>()

    private val appUser by aRandom<AppUser>()

    private val appUserDataSource = mockk<AppUserDataSource>()
    private val presenter by lazy { AppUserAvatarPresenter(appUserDataSource) }
    private val view = mockk<AppUserAvatarPresenter.View>(relaxed = true)

    @Before
    fun setUp() {
        coEvery { appUserDataSource.getAppUser() } returns appUser
        every { view.selects() } returns selectsChannel.openSubscription()
    }

    @Test
    fun `initially shows placeholder`() {
        coEvery { appUserDataSource.getAppUser() } coAnswers { never() }

        presenter attach view

        verify { view.showPlaceholder() }
    }

    @Test
    fun `shows photo when user loads`() {
        presenter attach view

        verify { view.showPhoto(appUser.photo) }
    }

    @Test
    fun `keeps showing placeholder if app user load fails`() {
        coEvery { appUserDataSource.getAppUser() } throws Exception()

        presenter attach view

        verify(exactly = 0) { view.showPhoto(any()) }
    }

    @Test
    fun `shows app user options when avatar is selected only when app user is loaded`() {
        val deferredAppUser = deferred<AppUser>()
        coEvery { appUserDataSource.getAppUser() } coAnswers { deferredAppUser.await() }

        presenter attach view
        select()
        verify(exactly = 0) { view.showAppUserOptions() }

        deferredAppUser.complete(appUser)
        select()
        verify { view.showAppUserOptions() }
    }

    private fun select() = selectsChannel.offer(Select)
}