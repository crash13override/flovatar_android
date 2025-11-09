package com.flovatar.mobileapp.eventbus

import com.flovatar.mobileapp.model.AvatarModel

data class SaveListEvent(val list: List<AvatarModel>) {
}