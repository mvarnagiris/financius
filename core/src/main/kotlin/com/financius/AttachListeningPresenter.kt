package com.financius

import life.shank.AttachListener
import life.shank.Attachable

abstract class AttachListeningPresenter<INTENT, STATE, SIDE_EFFECT, VIEW : AttachListeningPresenter.View> :
    Presenter<INTENT, STATE, SIDE_EFFECT, VIEW>(),
    AttachListener<VIEW> {

    override fun onAttach(a: VIEW) = attach(a)
    override fun onDetach(a: VIEW) = detach(a)

    interface View : Presenter.View, Attachable
}