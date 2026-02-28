package com.sohitechnology.gymstudio.hammer.data.cache

import com.sohitechnology.gymstudio.hammer.data.model.MemberCountData
import com.sohitechnology.gymstudio.hammer.ui.member.MemberUiModel

object HomeCache {
    var memberCount: MemberCountData? = null
    var expiryMembers: List<MemberUiModel>? = null

    fun clear() {
        memberCount = null
        expiryMembers = null
    }
}
